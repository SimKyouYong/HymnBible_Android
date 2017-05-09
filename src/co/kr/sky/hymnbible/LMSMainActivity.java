package co.kr.sky.hymnbible;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import co.kr.sky.hymnbible.adapter.LMSMain_Adapter;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.LMSMainObj;

public class LMSMainActivity extends Activity{
	EditText lms_msg , phone_number;
	ListView                list_number;
	ArrayList<LMSMainObj> arrData = new ArrayList<LMSMainObj>();
	LMSMain_Adapter           m_Adapter;
	public static int onresume_0 = 0;
	CommonUtil dataSet = CommonUtil.getInstance();
	private Typeface ttf;
	private static final int INPUT_FILE_REQUEST_CODE = 1;
	protected ProgressDialog customDialog = null;
	int count_all = 0;

	private LinearLayout list_noti;
	private TextView font_1  , font_2, font_3 ,title , t_count , txt_byte , txt_notice;
	private Button tab1 , tab2 , send_lms;
	public class AccumThread extends Thread{
		public AccumThread(){
		}
		@Override
		public void run()
		{
			//setting
			for (int i = 0; i < dataSet.arrData_real.size(); i++) {
				Log.e("SKY", "dataSet.arrData_real.get(i).getNAME() :: " + dataSet.arrData_real.get(i).getNAME());
				Log.e("SKY", "dataSet.arrData_real.get(i).getPHONE() :: " + dataSet.arrData_real.get(i).getPHONE());
				if (i == 0) {
					arrData.add(new LMSMainObj(dataSet.arrData_real.get(i).getNAME(), dataSet.arrData_real.get(i).getPHONE().replace("-", "")));
				}else{
					Boolean flag = true;
					for (int j = 0; j < arrData.size(); j++) {
						Log.e("SKY", "arrData.get(j).getName() :: " + arrData.get(j).getName());
						Log.e("SKY", "arrData.get(j).getNumber() :: " + arrData.get(j).getNumber());
						if (arrData.get(j).getNumber().replace("-", "").equals(dataSet.arrData_real.get(i).getPHONE().replace("-", ""))) {
							flag = false;
						}
					}
					if (flag) {
						Log.e("SKY", "추가!");
						arrData.add(new LMSMainObj(dataSet.arrData_real.get(i).getNAME(), dataSet.arrData_real.get(i).getPHONE().replace("-", "")));
					}
				}
			}
			Message msg2 = mAfterAccum.obtainMessage();
			msg2.arg1 = 200;
			mAfterAccum.sendMessage(msg2);
		}
	}
	@Override
	public void onResume(){
		super.onResume();
		if (onresume_0 ==1) {
			customProgressPop("전화번호 불러오는중..");
			onresume_0 = 0;
			AccumThread av = new AccumThread();
			av.start();
		}
	}



	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lmsmain);
		ttf = Typeface.createFromAsset(getAssets(), "HANYGO230.TTF");

		lms_msg = (EditText)findViewById(R.id.lms_msg);
		phone_number = (EditText)findViewById(R.id.phone_number);
		list_number = (ListView)findViewById(R.id.list_number);
		font_1 = (TextView)findViewById(R.id.font_1);
		font_2 = (TextView)findViewById(R.id.font_2);
		txt_byte = (TextView)findViewById(R.id.txt_byte);
		font_3 = (TextView)findViewById(R.id.font_3);
		title = (TextView)findViewById(R.id.title);
		t_count = (TextView)findViewById(R.id.t_count);
		tab1 = (Button)findViewById(R.id.tab1);
		tab2 = (Button)findViewById(R.id.tab2);
		send_lms = (Button)findViewById(R.id.send_lms);
		list_noti = (LinearLayout)findViewById(R.id.list_noti);
		txt_notice = (TextView)findViewById(R.id.txt_notice);


		lms_msg.setTypeface(ttf);
		phone_number.setTypeface(ttf);
		font_1.setTypeface(ttf);
		txt_byte.setTypeface(ttf);
		font_2.setTypeface(ttf);
		font_3.setTypeface(ttf);
		tab1.setTypeface(ttf);
		tab2.setTypeface(ttf);
		send_lms.setTypeface(ttf);
		title.setTypeface(ttf);
		t_count.setTypeface(ttf);
		txt_notice.setTypeface(ttf);

		findViewById(R.id.bottomview_l).setOnClickListener(btnListener);
		findViewById(R.id.bottomview_l_copy).setOnClickListener(btnListener);
		findViewById(R.id.bottomview_c).setOnClickListener(btnListener);
		findViewById(R.id.bottomview_c_copy).setOnClickListener(btnListener);
		findViewById(R.id.bottomview_r).setOnClickListener(btnListener);
		findViewById(R.id.bottomview_r_copy).setOnClickListener(btnListener);
		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.tab1).setOnClickListener(btnListener);
		findViewById(R.id.tab2).setOnClickListener(btnListener);
		findViewById(R.id.number_plus).setOnClickListener(btnListener);
		findViewById(R.id.number_plus).setOnClickListener(btnListener);
		findViewById(R.id.number_minus).setOnClickListener(btnListener);
		findViewById(R.id.send_lms).setOnClickListener(btnListener);


		m_Adapter = new LMSMain_Adapter( this , arrData , mAfterAccum);
		list_number.setAdapter(m_Adapter);
		lms_msg.addTextChangedListener(editEvent);


	}
	private TextWatcher editEvent = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(lms_msg.isFocusable())
			{
				try
				{
					byte[] bytetext = lms_msg.getText().toString().getBytes("KSC5601");
					txt_byte.setText(Integer.toString(bytetext.length)+" Byte");
				}catch(Exception ex){}
			}
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			String after_text = s.toString();
			try
			{
				byte[] getbyte = after_text.getBytes("KSC5601");
				if(getbyte.length > 2000)
				{
					s.delete(s.length()-2, s.length()-1);
				}
			}catch (Exception e) {}
		}
	};
	Handler mAfterAccum = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.arg1  == 0 ) {
				int res = (Integer)msg.obj;
				Log.e("SKY" , "RESULT  -> " + res);
				arrData.remove(res);
				m_Adapter.notifyDataSetChanged();
				t_count.setText("보내는 사람 : " + arrData.size()+ " 명");
			} else if (msg.arg1  == 200 ) {
				dataSet.arrData_real.clear();
				m_Adapter.notifyDataSetChanged();
				list_noti.setVisibility(View.GONE);
				t_count.setText("보내는 사람 : " + arrData.size()+ " 명");
				customProgressClose();
			} else if (msg.arg1  == 8000 ) {
				count_all--;
				Log.e("SKY" , "count_all :: " + count_all);
				if (count_all == 0) {
					//customProgressClose();
					//디비 인설트 
					SAVE_LMS_HISTORY(""+arrData.size() , lms_msg.getText().toString());
					arrData.clear();
					lms_msg.setText("");
					phone_number.setText("");
					m_Adapter.notifyDataSetChanged();
					t_count.setText("보내는 사람 : " + arrData.size()+ " 명");
					AlertDialog.Builder alert = new AlertDialog.Builder(LMSMainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
					alert.setTitle("알림");
					alert.setMessage("전송 완료 하였습니다.");
					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					});
					alert.show(); 
				}else{
					setcustomProgressPop("발송진행중. . .\n\n총 "+ (arrData.size()-count_all) + "/" + arrData.size() + " 발송");
				}
			}

		}
	};
	private void Btn_bottomview_c(){
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
	private void Btn_bottomview_l(){
		Intent intent100 = new Intent(LMSMainActivity.this , LMSHistoryActivity.class);
		startActivityForResult(intent100, 100);
	}

	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:	
				finish();
				break;
			case R.id.bottomview_l:	//발송내역
				Btn_bottomview_l();
				break;
			case R.id.bottomview_l_copy:	//발송내역
				Btn_bottomview_l();
				break;
			case R.id.bottomview_c:	
				Btn_bottomview_c();
				break;
			case R.id.bottomview_c_copy:
				Btn_bottomview_c();
				break;
			case R.id.bottomview_r:	
				finish();
				break;
			case R.id.bottomview_r_copy:	
				finish();
				break;

			case R.id.tab1:	
				//폰주소록
				Intent intent300 = new Intent(LMSMainActivity.this , LMSMyPhoneActivity.class);
				startActivity(intent300);
				break;
			case R.id.tab2:	
				//서버주소록
				Intent intent200 = new Intent(LMSMainActivity.this , LMSServerActivity.class);
				startActivityForResult(intent200, 200);
				break;
			case R.id.send_lms:
				Date d = new Date();
				String s = d.toString();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				int count = (SELECT_Phone(sdf.format(d))+arrData.size());
				Log.e("SKY" , "count :: " + count);
				if (arrData.size() == 0) {
					Toast.makeText(getApplicationContext(), "이름 혹은 전화번호를 입력해주세요.", 0).show();
				}else if(lms_msg.getText().toString().trim().length() == 0){
					Toast.makeText(getApplicationContext(), "보내실 문자를 입력해주세요.", 0).show();
				}else if(arrData.size() > 499){
					Toast.makeText(getApplicationContext(), "하루에 전송 최대치(500건)를 넘었습니다.", 0).show();
				}else if((SELECT_Phone(sdf.format(d))+arrData.size()) > 499){
					Toast.makeText(getApplicationContext(), "하루에 전송 최대치(500건)를 넘었습니다.", 0).show();
				}else{
					//발송진행중. . .
					//총 000/000 발송
					customProgressPop("발송진행중. . .\n\n총 0/" + arrData.size() + " 발송");

					Thread myThread = new Thread(new Runnable() {
						public void run() {
							for (int i = 0; i < arrData.size(); i++) {
								try {
									handler.sendMessage(handler.obtainMessage());
									Thread.sleep(1000);
								} catch (Throwable t) {
								}
							}
						}
					});
					myThread.start();
				}
				break;
			case R.id.number_plus:	
				if (phone_number.getText().toString().length() == 0 || phone_number.getText().toString().length() == 0) {
					Toast.makeText(getApplicationContext(), "이름 혹은 전화번호를 입력해주세요.", 0).show();
					return;
				}
				for (int i = 0; i < arrData.size(); i++) {
					if(arrData.get(i).getNumber().equals(phone_number.getText().toString())){
						Toast.makeText(getApplicationContext(), "동일한 번호가 존재합니다.", 0).show();
						return;
					}
				}
				arrData.add(new LMSMainObj(phone_number.getText().toString().replace("-", ""), phone_number.getText().toString().replace("-", "")));
				m_Adapter.notifyDataSetChanged();
				t_count.setText("보내는 사람 : " + arrData.size()+ " 명");
				list_noti.setVisibility(View.GONE);
				phone_number.setText("");
				break;
			case R.id.number_minus:	

				arrData.clear();
				m_Adapter.notifyDataSetChanged();
				t_count.setText("보내는 사람 : " + "0"+ " 명");
				break;

			}
		}
	};
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			updateThread();
		}
	};
	private void updateThread() {
		Log.e("SKY" , "count_all :: " + count_all);
		Log.e("SKY" , "arrData.size() :: " + arrData.size());
		if (count_all == (arrData.size()-1)) {
			//멈춤
			customProgressClose();
			SAVE_LMS_HISTORY(""+arrData.size() , lms_msg.getText().toString());
			arrData.clear();
			lms_msg.setText("");
			phone_number.setText("");
			m_Adapter.notifyDataSetChanged();
			t_count.setText("보내는 사람 : " + arrData.size()+ " 명");
			AlertDialog.Builder alert = new AlertDialog.Builder(LMSMainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
			alert.setTitle("알림");
			alert.setMessage("전송 완료 하였습니다.");
			alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			});
			alert.show(); 
		}else{
			final String number = arrData.get(count_all).getNumber();
			sendSMS(lms_msg.getText().toString(),number);
			count_all++;
			setcustomProgressPop("발송진행중. . .\n\n총 "+ count_all + "/" + arrData.size() + " 발송");
		}

	}

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
				//04-20 22:56:40.193: E/SKY(29127): PATH :: /storage/emulated/0/KakaoTalkDownload/전화번호불러오기샘플데이터.txt
				String text = null;
				try {
					File file = new File(getPath(uri));		//파일명
					FileInputStream fis = new FileInputStream(file);
					Reader in = new InputStreamReader(fis,"euc-kr");

					int size = fis.available();
					char[] buffer = new char[size];
					in.read(buffer);
					in.close();

					text = new String(buffer);

					String arr_txt[] = text.split("\n");
					Log.e("SKY", "arr_txt size : " + arr_txt.length);

					for (int i = 0; i < arr_txt.length; i++) {
						if (arr_txt[i].replace("\u0000", "").replace("\n", "").replace("\r", "").replace("-", "").length() > 1) {
							Log.e("SKY", "arr_txt size1 : " + arr_txt[i]);
							String txt[] = arr_txt[i].split("\t");
							arrData.add(new LMSMainObj(txt[0], txt[1].replace("\u0000", "").replace("\n", "").replace("\r", "").replace("-", "")));
							list_noti.setVisibility(View.GONE);
						}
					}
					m_Adapter.notifyDataSetChanged();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} 
			break;
		}
	}
	public int SELECT_Phone(String date)		//디비 값 조회해서 저장하기
	{
		try{
			//  db파일 읽어오기
			SQLiteDatabase db = openOrCreateDatabase("lms_history.db", Context.MODE_PRIVATE, null);
			// 쿼리로 db의 커서 획득
			Cursor cur = db.rawQuery("SELECT * FROM `lms_history` where date like '%" + date + "%';", null);
			// 처음 레코드로 이동
			int count = 0;
			while(cur.moveToNext()){
				// 읽은값 출력
				Log.i("MiniApp",cur.getString(0)+"/"+cur.getString(1)+"/"+cur.getString(2));
				count=+ Integer.parseInt(cur.getString(1));
			}
			cur.close();
			db.close();
			Log.e("SKY" , "SELECT_Phone count :: " + count);

			return count;
		}
		catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ",se.toString());
			return 0;
		}   

	}
	//SELECT COUNT(*) FROM your_table;
	private void sendSMS(String msg, String number) {
		Log.e("SKY", "보낼 문자 번호 :: " + number);

		SmsManager sm = SmsManager.getDefault();

		if(msg.getBytes().length > 80) {
			ArrayList<String> parts = sm.divideMessage(msg);
			sm.sendMultipartTextMessage(number, null, parts, null, null);
		}
		else
			sm.sendTextMessage(number, null, msg, null, null);
	}
	public void SAVE_LMS_HISTORY(String phone, String body){
		//인서트쿼리
		try{
			Date d = new Date();
			String s = d.toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println("현재날짜 : "+ sdf.format(d));
			SQLiteDatabase db = openOrCreateDatabase("lms_history.db", Context.MODE_PRIVATE, null);
			String sql;
			try {
				sql = "INSERT INTO `lms_history`(`phone`,`body`,`date`) VALUES (";
				sql += "'"  + phone  + "'" ;
				sql += ",'" + body  + "'" ;
				sql += ",'" + sdf.format(d)  + "'" ;
				sql +=   ")";
				Log.e("SKY","sql  : "+ sql);
				db.execSQL(sql);
			} catch (Exception e) {
				db.close();
				Log.e("SKY","sql error : "+ e.toString());
			}
			db.close();
		}catch (Exception e) {
			Log.e("SKY","onPostExecute error : "+ e.toString());
		}
	}
	public void customProgressPop(String msg){
		try{
			if (customDialog==null){
				customDialog = new ProgressDialog( this );
			}
			customDialog.setCancelable(false);
			customDialog.setMessage(msg);
			customDialog.show();
		}catch(Exception ex){}
	}
	public void setcustomProgressPop(String msg){
		customDialog.setMessage(msg);
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
