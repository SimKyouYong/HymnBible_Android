package co.kr.sky.hymnbible;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	LinearLayout list_view_11;
	EditText e_search1;
	TextView list_count;
	private Typeface ttf;

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
		ttf = Typeface.createFromAsset(getAssets(), "HANYGO230.TTF");

		try {
			PackageInfo info = getPackageManager().getPackageInfo("패키지이름", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("SKY" , "HASH KEY :: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		e_search1 = (EditText)findViewById(R.id.e_search1);
		m_ListView = (ListView)findViewById(R.id.list_cummun);
		list_view_11 = (LinearLayout)findViewById(R.id.list_view_11);
		list_count = (TextView)findViewById(R.id.list_count);
		list_count.setTypeface(ttf);
		e_search1.setTypeface(ttf);

		SupportMapFragment fragment =   (SupportMapFragment)getSupportFragmentManager()
				.findFragmentById(R.id.mapview);
		mMap = fragment.getMap();
		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.btn_sp2).setOnClickListener(btnListener);
		findViewById(R.id.btn_sp3).setOnClickListener(btnListener);
		e_search1.setText("");
//		SendHttp();
		
		CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(36.3370879,127.5477928));
		mMap.moveCamera(update);		//자기 위치로 이동
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(7);
		mMap.animateCamera(zoom);
		 
	}
	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.btn_sp2:	
				SendHttp();
				break;
			case R.id.btn_back:	
				finish();
				break;
			case R.id.btn_sp3:	
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
						RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speech to Text");
				try {
					startActivityForResult(intent, MainActivity.REQ_CODE_SPEECH_INPUT);
				} catch (ActivityNotFoundException a) {
					Toast.makeText(ChurchSearch.this,"다시 시도해주세요.",
							Toast.LENGTH_SHORT).show();
				}
				break;

			}
		}
	};
	/**
	 * Receiving speech input
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("SKY" , "RESULT :: " + requestCode);
		if (resultCode == RESULT_OK && null != data) {
			
			ArrayList<String> result = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			Log.e("SKY" , "RESULT :: " + result.get(0));
			e_search1.setText(""+result.get(0));
		}
	}
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
					if (ja.length() == 0 ) {
						Toast.makeText(getApplicationContext(), "해당 검색 데이터는 존재하지 않습니다.", 0).show();
						customProgressClose();
						return;
					}
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
						String latitude = order.getString("latitude");
						String hardness = order.getString("hardness");
						//latitude1 = "" + latitude.substring(0 , 2) + "." + latitude.substring(2,latitude.length());
						//hardness1 = "" + hardness.substring(0 , 3) + "." + hardness.substring(3,hardness.length());
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
								latitude,
								hardness));
					}
					//write(arrData);
//					write("" + key_index + "/" + church_address + "/" + latitude + "/" + hardness);
					list_view_11.setVisibility(View.VISIBLE);
					list_count.setText(e_search1.getText().toString() + " 검색 결과는 총 " + arrData.size() + " 건 입니다.");
					m_Adapter = new ChurchSearch_Adapter( ChurchSearch.this , arrData);
					m_ListView.setOnItemClickListener(mItemClickListener);
					m_ListView.setAdapter(m_Adapter);

				} catch (JSONException e) {
					e.printStackTrace();
				} 

				customProgressClose();
				MapMarker();
			}else if(msg.arg1 == 2){
				//mapMarker(msg.arg2);
			}
		}
	};
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
			//37.5132042,126.846721
			CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(37.5132042,126.846721));
			mMap.moveCamera(update);		//자기 위치로 이동
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
			mMap.animateCamera(zoom);
			return;
		}
		CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(arrData.get(0).getLatitude()), Double.parseDouble(arrData.get(0).getLongitude())));
		mMap.moveCamera(update);		//자기 위치로 이동
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
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
		Log.e("SKY", "address : " + address);

		try {
			List<Address> listAddress = geocoder.getFromLocationName(address, 1);
			if (listAddress.size() > 0) { // 주소값이 존재 하면
				addr = listAddress.get(0); // Address형태로
				int lat = (int) (addr.getLatitude() * 1E6);
				int lng = (int) (addr.getLongitude() * 1E6);
				location = new GeoPoint(lat, lng);

				Log.e("SKY", "주소로부터 취득한 위도 : " + lat + ", 경도 : " + lng);
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("SKY", "e : " + e);

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
