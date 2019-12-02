package com.example.test.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.test.db.CoolWeatherDB;
import com.example.test.model.City;
import com.example.test.model.County;
import com.example.test.model.Province;
import com.example.test.util.HttpCallbackListener;
import com.example.test.util.HttpUtil;
import com.example.test.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseAreaActivity extends Activity{
	 
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	/*
	 * �Ƿ��WeatherActivity����ת����
	 */
	private boolean isFromWeatherActivity;
	private List<String> datalist=new ArrayList<String>();
	 /*
	  * ʡ�б�
	  */
	private List<Province> provinceList;
	/*
	 * ���б�
	 */
	private List<City> cityList;
	/*
	 * ���б�
	 */
	private List<County> countyList;
	/*
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/*
	 * ѡ�еĳ���
	 */
	private City selectedCity;
	/*
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView=(ListView) findViewById(R.id.list_view);
		titleText=(TextView) findViewById(R.id.titile_text);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,datalist);
		listView.setAdapter(adapter);
		coolWeatherDB=CoolWeatherDB.getInstance(this);
		
		//--------------������---------------------//
		isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity)
		{
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		//
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel==LEVEL_PROVINCE)
				{
					selectedProvince=provinceList.get(index);
					queryCities();
				}else if(currentLevel==LEVEL_CITY)
				{
					selectedCity=cityList.get(index);
					queryCounties();
				}else if(currentLevel==LEVEL_COUNTY)
				{	String countyCode=countyList.get(index).getCountyCode();
					Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_code", countyCode);startActivity(intent);finish();
				} 
			}
			
		});
		
		queryProvinces();//����ʡ������
		
		
	}
	/*
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryProvinces()
	{
		provinceList=coolWeatherDB.loadProvinces();
		if(provinceList.size()>0)
		{
			datalist.clear();
			for(Province province:provinceList)
			{
				datalist.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	/*
	 * ��ѯѡ���������е��У����ȴ����ݿ��ѯ�����û�в�ѯ���ٵ���������ѯ��
	 */
	private void queryCities()
	{
		cityList=coolWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size()>0)
		{
			datalist.clear();
			for(City city:cityList)
			{
				datalist.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	/*
	 * ��ѯѡ���������е��أ����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCounties()
	{
		countyList=coolWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size()>0)
		{
			datalist.clear();
			for(County county:countyList)
			{
				datalist.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"county");
		}
		
	}
	/*
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯ��������
	 */
	private void queryFromServer(final String code,final String type)
	{
		String address;
		if(!TextUtils.isEmpty(code))
		{
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				if("province".equals(type))
				{
					result=Utility.handleProvincesResponse(coolWeatherDB, response);
				}else if("city".equals(type))
				{
					result=Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				}else if("county".equals(type))
				{
					result=Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if(result)
				{
					//ͨ��runOnUiThread�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type))
							{
								queryProvinces();
							}else if("city".equals(type))
							{
								queryCities();
							}else if("county".equals(type))
							{
								queryCounties();
							}
							
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				//ͨ��runOnUiThread()�ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	/*
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog()
	{
		if(progressDialog==null)
		{
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("���ڼ��ء�����");
			progressDialog.setCanceledOnTouchOutside(false);
			//��ProgressDialog�ĵط�����ü���������ԣ���ֹ4.0ϵͳ�����⡣mProgressDialog.setCanceledOnTouchOutside(false);
			//������loading��ʱ������㴥����Ļ�������򣬾ͻ������progressDialog��ʧ��Ȼ����ܳ��ֱ�������
		}
		progressDialog.show();
	}
	/*
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog()
	{
		if(progressDialog!=null)
		{
			progressDialog.dismiss();
		}
	}
	
	/*
	 * ����Back���������ݵ�ǰ�������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳���
	 */
	public void onBackPressed()
	{
		if(currentLevel==LEVEL_COUNTY)
		{
			queryCities();
		}else if(currentLevel==LEVEL_CITY)
		{
			queryProvinces();
		}else{
			
			finish();
		}
	}
}
