package co.kr.sky.hymnbible;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import co.kr.sky.AccumThread;
import co.kr.sky.hymnbible.adapter.LMSServerPhoneGroup_Adapter;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.LMSMainObj;
import co.kr.sky.hymnbible.obj.MyPhoneListObj;
import co.kr.sky.hymnbible.obj.MyPhoneListObj2;
import co.kr.sky.hymnbible.obj.MyServerGroupObj;
import co.kr.sky.hymnbible.obj.MyServerListObj;

public class LMSServerActivity extends Activity{
	protected ProgressDialog customDialog = null;
	ArrayList<MyServerGroupObj> arrData = new ArrayList<MyServerGroupObj>();
	LMSServerPhoneGroup_Adapter           m_Adapter;
	ListView                list_number;
	private Typeface ttf;
	ArrayList<MyServerListObj> arrData_detail = new ArrayList<MyServerListObj>();
	CheckBox check_all;
	private static final int INPUT_FILE_REQUEST_CODE = 1;

	private TextView font_1 , font_2 , font_3 , font_4 , t_count , t_name; 

	Map<String, String> map = new HashMap<String, String>();
	AccumThread mThread;
	TextView title;
	public static TextView check_count;

	CommonUtil dataSet = CommonUtil.getInstance();
	String [][]Object_Array;
	
	@Override
	public void onResume(){
		super.onResume();
		if (LMSMainActivity.onresume_0 ==1) {
			//그냥 끄기!
			finish();
		}
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
		list_number = (ListView)findViewById(R.id.list_number);
		title = (TextView)findViewById(R.id.title);
		t_name = (TextView)findViewById(R.id.t_name);
		check_all = (CheckBox)findViewById(R.id.check_all);
		check_count = (TextView)findViewById(R.id.check_count);
		
		t_count = (TextView)findViewById(R.id.t_count);
		font_1 = (TextView)findViewById(R.id.font_1);
		font_2 = (TextView)findViewById(R.id.font_2);
		font_3 = (TextView)findViewById(R.id.font_3);
		font_4 = (TextView)findViewById(R.id.font_4);
		
		ttf = Typeface.createFromAsset(getAssets(), "HANYGO230.TTF");
		title.setTypeface(ttf);
		t_name.setTypeface(ttf);
		check_count.setTypeface(ttf);
		t_count.setTypeface(ttf);
		
		font_1.setTypeface(ttf);
		font_2.setTypeface(ttf);
		font_3.setTypeface(ttf);
		font_4.setTypeface(ttf);

		m_Adapter = new LMSServerPhoneGroup_Adapter( this , arrData , mAfterAccum);
		list_number.setOnItemClickListener(mItemClickListener);
		list_number.setAdapter(m_Adapter);

		customProgressPop();
		String []val = {"item1","item2","item3","item4" };
		map.put("url", dataSet.SERVER + "Server_Sel.jsp");
		map.put("my_phone", dataSet.PHONE);
		mThread = new AccumThread(this , mAfterAccum , map , 1 , 0 , val);

		mThread.start();		//스레드 시작!!


		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.btn_reflash).setOnClickListener(btnListener);
		findViewById(R.id.btn_reflash1).setOnClickListener(btnListener);
		findViewById(R.id.bottomview_c).setOnClickListener(btnListener);
		findViewById(R.id.bottomview_c_copy).setOnClickListener(btnListener);
		

		findViewById(R.id.btn_ok).setOnClickListener(btnListener);
		findViewById(R.id.btn_ok1).setOnClickListener(btnListener);

		check_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {

				if (buttonView.getId() == R.id.check_all) {
					if (isChecked) {
						Log.e("SKY" , "all클릭");
						Message msg2 = mAfterAccum.obtainMessage();
						msg2.arg1 = 5000;
						mAfterAccum.sendMessage(msg2);
					} else {
						Log.e("SKY" , "all not 클릭" );
						Message msg2 = mAfterAccum.obtainMessage();
						msg2.arg1 = 6000;
						mAfterAccum.sendMessage(msg2);
					}
				}
			}
		});


	}
	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:	
				finish();
				break;
			case R.id.btn_reflash:
				customProgressPop();
				String []val = {"item1","item2","item3","item4" };
				map.put("url", dataSet.SERVER + "Server_Sel.jsp");
				map.put("my_phone", dataSet.PHONE);
				mThread = new AccumThread(LMSServerActivity.this , mAfterAccum , map , 1 , 0 , val);

				mThread.start();		//스레드 시작!!
				break;
			case R.id.btn_reflash1:
				customProgressPop();
				String []val1 = {"item1","item2","item3","item4" };
				map.put("url", dataSet.SERVER + "Server_Sel.jsp");
				map.put("my_phone", dataSet.PHONE);
				mThread = new AccumThread(LMSServerActivity.this , mAfterAccum , map , 1 , 0 , val1);

				mThread.start();		//스레드 시작!!
				break;
			case R.id.btn_ok:	
				btn_ok();
				break;
			case R.id.btn_ok1:
				btn_ok();
				break;
			case R.id.bottomview_c:	
				Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
				contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
				contentSelectionIntent.setType("file/*");

				Intent[] intentArray;
				intentArray = new Intent[0];

				Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
				chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
				chooserIntent.putExtra(Intent.EXTRA_TITLE, "파일을 선택해주세요.");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

				startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
				//Btn_bottomview_c();
				break;
			case R.id.bottomview_c_copy:
				Intent contentSelectionIntent1 = new Intent(Intent.ACTION_GET_CONTENT);
				contentSelectionIntent1.addCategory(Intent.CATEGORY_OPENABLE);
				contentSelectionIntent1.setType("file/*");

				Intent[] intentArray1;
				intentArray1 = new Intent[0];

				Intent chooserIntent1 = new Intent(Intent.ACTION_CHOOSER);
				chooserIntent1.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent1);
				chooserIntent1.putExtra(Intent.EXTRA_TITLE, "파일을 선택해주세요.");
				chooserIntent1.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray1);

				startActivityForResult(chooserIntent1, INPUT_FILE_REQUEST_CODE);
				//Btn_bottomview_c();
				break;
			}
		}
	};
	private String getPath(Uri uri)
	{
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("SKY" , "RESULT :: " + requestCode);
		Log.e("SKY" , "resultCode :: " + resultCode);
		Log.e("SKY" , "data :: " + data);
		if (data ==null) {
			return;
		}
		switch (requestCode) {
		case 1:
			if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
				Uri uri = data.getData();
				//경로 구하기
				String PATH = getPath(uri);
				Log.e("SKY" , "PATH :: " + PATH);

				if (!PATH.matches(".*txt")) {
					Toast.makeText(getApplicationContext(), "확장자가 txt 파일이 아닙니다.", 0).show();
					return;
				}
				String text = null;
				try {
					File file = new File(getPath(uri));		//파일명
					FileInputStream fis = new FileInputStream(file);
					Reader in = new InputStreamReader(fis);
					int size = fis.available();
					char[] buffer = new char[size];
					in.read(buffer);
					in.close();

					text = new String(buffer);
					Log.e("SKY", "text : " + text);
					JSONObject obj = new JSONObject();
					JSONArray jArray = new JSONArray();//배열이 필요할때
					String arr_txt[] = text.split("\n");
					Log.e("SKY", "arr_txt size : " + arr_txt.length);

					int count_all = 0;
					for (int i = 0; i < arr_txt.length; i++) {
						Log.e("SKY", "arr_txt size1 : " + arr_txt[i]);
						String txt[] = arr_txt[i].split(",");
						JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
						sObject.put("GROUP", txt[0]);
						sObject.put("NAME", txt[1]);
						sObject.put("PHONE", txt[2]);
						jArray.put(sObject);
						count_all++;
					}
					obj.put("data",jArray);
					Log.e("SKY","JSON DATA :: " + obj.toString());
					
					
					//post 발송
					map.put("url", dataSet.SERVER+"Server_Insert.jsp");
					map.put("user_id",dataSet.PHONE);
					map.put("group_name", gg_name);
					map.put("val",obj.toString());
					map.put("count",""+count_all);

					//post 발송
					mThread = new AccumThread(LMSServerActivity.this , mAfterAccum , map , 0 , 2 , null);
					mThread.start();		//스레드 시작!!
				} catch (IOException e) {
					throw new RuntimeException(e);
				}catch (JSONException e) {
					e.printStackTrace();
				}
			} 
			break;
		}
	}
	String gg_name = "";
	private void Btn_bottomview_c(){
		//서버에 그룹 저장
		AlertDialog.Builder alert = new AlertDialog.Builder(LMSServerActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		alert.setTitle("알림");
		LinearLayout layout = new LinearLayout(LMSServerActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		final EditText name = new EditText(LMSServerActivity.this);
		name.setSingleLine(true);
		layout.setPadding(20, 0, 20, 0);
		name.setHint("그룹명을 입력해주세요.");
		layout.addView(name);
		alert.setView(layout);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				gg_name = name.getText().toString();
				if (gg_name.length() ==0 ) {
					Toast.makeText(getApplicationContext(), "그룹명을 입력해주세요.", 0).show();
				}else{
					Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
					contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
					contentSelectionIntent.setType("file/*");

					Intent[] intentArray;
					intentArray = new Intent[0];

					Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
					chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
					chooserIntent.putExtra(Intent.EXTRA_TITLE, "파일을 선택해주세요.");
					chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

					startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
				}
			}
		});
		alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		alert.show();


		
	}
	private void btn_ok(){
		arrData_detail.clear();
		String key_index_str = "";
		for (int i = 0; i < arrData.size(); i++) {
			Log.e("SKY", "체크!!  :: " + arrData.get(i).getCheck());
			if (arrData.get(i).getCheck() == 1) {
				if (key_index_str.equals("")) {
					key_index_str = arrData.get(i).getKey_index();
				}else{
					key_index_str += "," + arrData.get(i).getKey_index();
				}
			}
		}
		customProgressPop();

		String []val = {"item1","item2","item3","item4" };
		map.clear();
		map.put("url", dataSet.SERVER + "Server_Phone_Sel.jsp");
		map.put("key_index", key_index_str);
		mThread = new AccumThread(this , mAfterAccum , map , 1 , 1 , val);

		mThread.start();		//스레드 시작!!
	}
	Handler mAfterAccum = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.arg1  == 0 ) {
				customProgressClose();
				arrData.clear();
				Object_Array = (String [][]) msg.obj;
				if (Object_Array.length == 0) {
					return;
				}
				//				Log.e("CHECK" ,"**********************  --->" + Object_Array[0].length);
				for (int i = 0; i < Object_Array.length; i++) {
					for (int j = 0; j < Object_Array[0].length; j++) {
						Log.e("CHECK" ,"value----> ---> Object_Array [" +i+"]["+j+"]"+  Object_Array[i][j]);
					}
				}
				for (int i = 0; i < (Object_Array[0].length); i++){
					if (Object_Array[0][i] != null) {
						arrData.add(new MyServerGroupObj(""+Object_Array[0][i], 
								Object_Array[1][i], 
								Object_Array[2][i], 
								Object_Array[3][i], 
								0));
					}
				}
				m_Adapter.notifyDataSetChanged();
			}else if(msg.arg1 == 2){
				String res = (String)msg.obj;
				Log.e("SKY" , "RESULT  -> " + res);
				customProgressClose();
				
				customProgressPop();
				String []val = {"item1","item2","item3","item4" };
				map.put("url", dataSet.SERVER + "Server_Sel.jsp");
				map.put("my_phone", dataSet.PHONE);
				mThread = new AccumThread(LMSServerActivity.this , mAfterAccum , map , 1 , 0 , val);

				mThread.start();		//스레드 시작!!
			}else if(msg.arg1 == 1){

				Object_Array = (String [][]) msg.obj;
				if (Object_Array.length == 0) {
					return;
				}
				//				Log.e("CHECK" ,"**********************  --->" + Object_Array[0].length);
				for (int i = 0; i < Object_Array.length; i++) {
					for (int j = 0; j < Object_Array[0].length; j++) {
						Log.e("CHECK" ,"value----> ---> Object_Array [" +i+"]["+j+"]"+  Object_Array[i][j]);
					}
				}
				for (int i = 0; i < (Object_Array[0].length); i++){
					if (Object_Array[0][i] != null) {
						arrData_detail.add(new MyServerListObj(""+Object_Array[0][i], 
								Object_Array[1][i], 
								Object_Array[2][i], 
								Object_Array[3][i], 
								0,
								0));

						dataSet.arrData_real.add(new MyPhoneListObj2(0,
								arrData_detail.get(i).getName(),
								arrData_detail.get(i).getPhone(),
								1,
								0));

					}
				}
				customProgressClose();
				LMSMainActivity.onresume_0 = 1;
				finish();
			}else if(msg.arg1  == 2 ){//전체선택 
				
				
			}else if(msg.arg1  == 5000 ){//전체선택 
				for (int i = 0; i < arrData.size(); i++) {
					arrData.get(i).setCheck(1);
				}
				m_Adapter.notifyDataSetChanged();
			}else if(msg.arg1  == 6000 ){//전체선택 해제
				for (int i = 0; i < arrData.size(); i++) {
					arrData.get(i).setCheck(0);
				}
				m_Adapter.notifyDataSetChanged();
				check_count.setText("0");

			}else if(msg.arg1  == 7000 ){
				check_all.setEnabled(true);//활성화
				check_all.setChecked(true);
				int count = 0;
				for (int i = 0; i < arrData.size(); i++) {
					count += Integer.parseInt(arrData.get(i).getCount());
				}
				check_count.setText("" + count);
			}else if(msg.arg1  == 8000 ){
				check_all.setEnabled(true);//  ??
				check_all.setChecked(false);
			}else if(msg.arg1  == 9000 ){
				check_all.setEnabled(true); //모두 선택
				check_all.setChecked(false);
			}else if(msg.arg1  == 9001 ){//삭제
				int del_position = (int)msg.arg2;
				customProgressPop();
				map.put("url", dataSet.SERVER + "Server_Group_Del.jsp");
				map.put("key_index", ""+del_position);
				mThread = new AccumThread(LMSServerActivity.this , mAfterAccum , map , 0 , 2 , null);

				mThread.start();		//스레드 시작!!
			}
		}
	};

	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			Log.e("SKY", "POSITION :: "+position);
			//arrData.get(position).setSELECTED(1);
			Intent board = new Intent(LMSServerActivity.this, LMSServerDetailActivity.class);
			board.putExtra("Object", arrData.get(position));
			startActivity(board);
		}
	};
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
