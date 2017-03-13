package co.kr.sky.hymnbible;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import co.kr.sky.AccumThread;
import co.kr.sky.hymnbible.adapter.ChurchSearch_Adapter;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.ChurchObj;

public class ChurchSearch extends FragmentActivity implements LocationListener {
	private GoogleMap mMap;
	LocationManager locationManager; 
	LocationListener locationListener;
	double latitude = 0;
	double longitude=0;
	int mylocationmove = 0;
	Boolean GPS_STATUE= false;
	protected ProgressDialog customDialog = null;
	Map<String, String> map = new HashMap<String, String>();
	AccumThread mThread;
	CommonUtil dataSet = CommonUtil.getInstance();
	String [][]Object_Array;
	ArrayList<ChurchObj> arrData = new ArrayList<ChurchObj>();
	private ArrayList<Bitmap> bm = new ArrayList<Bitmap>();
	private ChurchSearch_Adapter           m_Adapter;
	private ListView                m_ListView;

	EditText e_search1;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("CHECK2", "onDestroy");
		try {
			recycleImages();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/** 메모리에 남아있는 이미지 제거 */
	private void recycleImages() {
		if (bm == null) {
			return;
		}
		for (Bitmap bm : bm) {
			if (bm != null) {
				bm.recycle();
				bm = null;
				Log.e("CHECK2", "bm bye..");
			}
		}
		bm.clear();
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_church);
		e_search1 = (EditText)findViewById(R.id.e_search1);
		m_ListView = (ListView)findViewById(R.id.list_cummun);

		SupportMapFragment fragment =   (SupportMapFragment)getSupportFragmentManager()
				.findFragmentById(R.id.mapview);
		mMap = fragment.getMap();
		findViewById(R.id.btn_sp2).setOnClickListener(btnListener);
		e_search1.setText("부천");
		//SendHttp();
		//GPS_Start();
	}
	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.btn_sp2:	
				SendHttp();
				break;

			}
		}
	};
	/*
	 * GPS 모듈 검색 & 리스너 등록
	 * */
	private void GPS_Start(){
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				Log.e("SKY" , "latitude ::" + latitude);
				Log.e("SKY" , "longitude ::" + longitude);
				if (mylocationmove == 0) {
					mylocationmove = 1;
					GPS_STATUE = true;
					//MapMarker(filter_btn.getText().toString());
				}
			}
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
			public void onProviderEnabled(String provider) {
			}
			public void onProviderDisabled(String provider) {
				/*
				if (flag == 0) {
					flag = 1;
					AlertDialog.Builder gsDialog = new AlertDialog.Builder(ChurchSearch.this); 
					gsDialog.setTitle("위치 서비스 설정");   
					gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?"); 
					gsDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int which) { 
							// GPS설정 화면으로 이동 
							Intent intent = new Intent(
									android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							intent.addCategory(Intent.CATEGORY_DEFAULT);
							startActivityForResult(intent, 999);
						} 
					})
					.setNegativeButton("취소", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							return;
						}
					}).create().show();
				}
				 */
			}

		};

		//		SendHttp();
		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
	}
	private void SendHttp(){
		customProgressPop();
		//String []val = {"KEY_INDEX" , "CODE","NAME","ADDRESS", 
		//		"LOAD_ADDRESS","PHONE","LATITUDE", "HARDNESS","FILE_NAME","PLACE","INTRODUCE"};
		String []val = {};
		map.put("url", dataSet.SERVER + "searchChurchJson.do");
		map.put("type", e_search1.getText().toString());
		mThread = new AccumThread(ChurchSearch.this , mAfterAccum , map , 0 , 0 , null);

		mThread.start();		//스레드 시작!!
	}
	Handler mAfterAccum = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.arg1  == 0 ) {
				bm.clear();
				String res = (String)msg.obj;
				Log.e("SKY", "RES :: " +  res);
				try {
					JSONArray ja = new JSONArray(res);
					for (int i = 0; i < ja.length(); i++) {
						bm.add(null);
						JSONObject order = ja.getJSONObject(i);
						String key_index = order.getString("key_index");
						String church_name = order.getString("church_name");
						String church_type = order.getString("church_type");
						String person_name = order.getString("person_name");
						String church_address = order.getString("church_address");
						String church_number = order.getString("church_number");
						String church_fax = order.getString("church_fax");
						String church_homepage = order.getString("church_homepage");
						String church_body  = order.getString("church_body");
						String church_img = order.getString("church_img");
						String church_img2 = order.getString("church_img2");
						String church_img3 = order.getString("church_img3");
						String church_img4 = order.getString("church_img4");
						String church_img5 = order.getString("church_img5");
						String church_img6 = order.getString("church_img6");
						String church_img7 = order.getString("church_img7");
						String church_img8 = order.getString("church_img8");
						String church_img9 = order.getString("church_img9");
						String church_img10 = order.getString("church_img10");
						String search_index = order.getString("search_index");
						Log.e("SKY" , "key_index :: " + key_index);

						String latitude = "" + findGeoPoint(church_address).getLatitudeE6();
						String hardness = "" + findGeoPoint(church_address).getLongitudeE6();
						Log.e("SKY" , "latitude :: " + latitude.substring(0 , 2) + "." + latitude.substring(2,latitude.length()));
						Log.e("SKY" , "hardness :: " + hardness.substring(0 , 3) + "." + hardness.substring(3,hardness.length()));


//						write("" + key_index + "/" + church_address + "/" + latitude + "/" + hardness);
						arrData.add(new ChurchObj(key_index, 
								church_name, 
								church_type, 
								person_name, 
								church_address, 
								church_number, 
								church_fax, 
								church_homepage, 
								church_body, 
								church_img,
								church_img2,
								church_img3,
								church_img4,
								church_img5,
								church_img6,
								church_img7,
								church_img8,
								church_img9,
								church_img10,
								search_index,
								latitude.substring(0 , 2) + "." + latitude.substring(2,latitude.length()),
								hardness.substring(0 , 3) + "." + hardness.substring(3,hardness.length())));
					}
					write(arrData);
//					write("" + key_index + "/" + church_address + "/" + latitude + "/" + hardness);
					m_ListView.setVisibility(View.VISIBLE);
					m_Adapter = new ChurchSearch_Adapter( ChurchSearch.this , arrData);
					//					 Xml에서 추가한 ListView 연결
					m_ListView.setOnItemClickListener(mItemClickListener);
					//					 ListView에 어댑터 연결
					m_ListView.setAdapter(m_Adapter);

				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				customProgressClose();
				MapMarker();
			}else if(msg.arg1 == 2){
				//mapMarker(msg.arg2);
			}
		}
	};
	private void write(ArrayList<ChurchObj> arrDat1a) throws IOException{
		String dirPath = "" + Environment.getExternalStorageDirectory();
		File file = new File(dirPath); 

		// 일치하는 폴더가 없으면 생성
		if( !file.exists() ) {
			file.mkdirs();
			Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
		}
		
		try {
			File savefile = new File(dirPath+"/test.txt");
			FileOutputStream fos = null;
			fos = new FileOutputStream(savefile);
			String testStr ="";
			for (int i = 0; i < arrDat1a.size(); i++) {
				testStr += "key : " + i +
						"주소 :" + arrDat1a.get(i).getChurch_address() +
						"위도 :" + arrDat1a.get(i).getLatitude() +
						"경도 :" + arrDat1a.get(i).getLongitude() +
						"\n";
				
				Log.e("SKY" , "testStr :: " + testStr);
			}
			fos.write(testStr.getBytes());
			fos.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			Intent board = new Intent(ChurchSearch.this, ChurchSearch_Detail.class);
			board.putExtra("Object", arrData.get(position));
			startActivity(board);
		}
	};
	private void Allpin(int i){
		if (!arrData.get(i).getLatitude().equals("")  && !arrData.get(i).getLongitude().equals("")) {
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.title("First Location");
			markerOptions.snippet("This Is Test Location");
			LatLng latlng = null;
			latlng = new LatLng(Double.parseDouble(arrData.get(i).getLatitude()), Double.parseDouble(arrData.get(i).getLongitude()));
			markerOptions.position(latlng);
			mMap.addMarker(new MarkerOptions()
					.position(latlng)
					.title(""+i)
					.snippet(arrData.get(i).getChurch_name())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_experience)));

			AccumThread1 av = new AccumThread1(arrData.get(i).getChurch_img(), i );
			av.start();
		}
	}
	public class AccumThread1 extends Thread{
		String url_;
		int i_;
		ImageView a_;
		public AccumThread1(String url, int i){
			this.url_ = url;
			this.i_ = i;
		}
		public AccumThread1(String url, int i , ImageView a){
			this.url_ = url;
			this.i_ = i;
			this.a_ = a;
		}
		@Override
		public void run()
		{
			bm.set(i_, getBitmapFromURL(url_));
			Message msg2 = mAfterAccum.obtainMessage();
			msg2.arg1 = 2;
			msg2.arg2 = i_;
			mAfterAccum.sendMessage(msg2);
		}
	}
	public static Bitmap getBitmapFromURL(String src) {
		Log.e("SKY" , "HTTP : " + src);
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			Bitmap bitmap_resize = BitmapFactory.decodeStream(input,null, options);
			return bitmap_resize;
		} catch (IOException e) {
			// Log exception
			return null;
		}
	}
	private void MapMarker(){

		mMap.setMyLocationEnabled(true);
		for (int i = 0; i < arrData.size(); i++) {
			Allpin(i);
		}
		mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter() {
			@Override
			public View getInfoContents(Marker marker) {
				View myContentView = getLayoutInflater().inflate(
						R.layout.custommarker, null);
				ImageView img = (ImageView) myContentView.findViewById(R.id.img_c);
				marker.getId();
				int index = Integer.parseInt(marker.getTitle());
				Log.e("SKY" , "index ::" + index);
				//				String[] array = dataSet.Token_Str(arrData.get(index).getFILE_NAME());
				img.setImageBitmap(bm.get(index));
				TextView tvTitle = ((TextView) myContentView.findViewById(R.id.title));
				tvTitle.setText(arrData.get(index).getChurch_address());
				TextView tvSnippet = ((TextView) myContentView.findViewById(R.id.snippet));
				tvSnippet.setText(arrData.get(index).getChurch_name());
				myContentView.setBackgroundColor(Color.WHITE);
				return myContentView;
			}
		});



		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker arg0) {
				// TODO Auto-generated method stub
				Log.e("SKY" , "-- onInfoWindowClick --");
				int index = Integer.parseInt(arg0.getTitle());

				Intent board = new Intent(ChurchSearch.this , ChurchSearch_Detail.class);
				board.putExtra("Object", arrData.get(index));
				startActivity(board);			
			}
		});
		if (arrData.size() < 1) {
			return;
		}
		CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(arrData.get(0).getLatitude()), Double.parseDouble(arrData.get(0).getLongitude())));
		mMap.moveCamera(update);		//자기 위치로 이동
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
		mMap.animateCamera(zoom);
	}
	/**
	 * 주소로부터 위치정보 취득
	 * @param address 주소
	 */
	private GeoPoint findGeoPoint(String address) {
		Geocoder geocoder = new Geocoder(this);
		Address addr;
		GeoPoint location = null;
		try {
			List<Address> listAddress = geocoder.getFromLocationName(address, 1);
			if (listAddress.size() > 0) { // 주소값이 존재 하면
				addr = listAddress.get(0); // Address형태로
				int lat = (int) (addr.getLatitude() * 1E6);
				int lng = (int) (addr.getLongitude() * 1E6);
				location = new GeoPoint(lat, lng);

				Log.d("SKY", "주소로부터 취득한 위도 : " + lat + ", 경도 : " + lng);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return location;
	} 
	private class CustomInfoWindowAdapter implements InfoWindowAdapter {
		private View view;
		public CustomInfoWindowAdapter() {
			view = getLayoutInflater().inflate(R.layout.custommarker,
					null);
		}
		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}

		@Override
		public View getInfoWindow(final Marker marker) {

			View myContentView = getLayoutInflater().inflate(
					R.layout.custommarker, null);
			//NetworkImageView img = ((NetworkImageView) myContentView.findViewById(R.id.img_c));
			ImageView img = (ImageView) myContentView.findViewById(R.id.img_c);
			marker.getId();
			int index = Integer.parseInt(marker.getTitle());
			//			String[] array = dataSet.Token_Str(mData.get(index).getFILE_NAME());
			img.setImageBitmap(bm.get(index));
			TextView tvTitle = ((TextView) myContentView.findViewById(R.id.title));
			tvTitle.setText(arrData.get(index).getChurch_address());
			TextView tvSnippet = ((TextView) myContentView.findViewById(R.id.snippet));
			tvSnippet.setText(arrData.get(index).getChurch_name());
			myContentView.setBackgroundColor(Color.WHITE);
			return myContentView;
		}
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}
	public void customProgressPop(){
		try{
			if (customDialog==null){
				customDialog = new ProgressDialog( this );
			}
			customDialog.show();
		}catch(Exception ex){}
	}
	public void customProgressClose(){
		if (customDialog!=null && customDialog.isShowing()){
			try{
				customDialog.cancel();
				customDialog.dismiss();
				customDialog = null;
			}catch(Exception e)
			{}
		}
	}
}
