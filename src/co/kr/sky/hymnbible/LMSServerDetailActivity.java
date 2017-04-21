package co.kr.sky.hymnbible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import co.kr.sky.AccumThread;
import co.kr.sky.hymnbible.adapter.LMSServerList_Adapter;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.MyPhoneListObj2;
import co.kr.sky.hymnbible.obj.MyServerGroupObj;
import co.kr.sky.hymnbible.obj.MyServerListObj;

public class LMSServerDetailActivity extends Activity{
	protected ProgressDialog customDialog = null;
	ArrayList<MyServerListObj> arrData = new ArrayList<MyServerListObj>();
	LMSServerList_Adapter           m_Adapter;
	ListView                list_number;
	private Typeface ttf;
	MyServerGroupObj obj;

	Map<String, String> map = new HashMap<String, String>();
	AccumThread mThread;
	TextView title;
	EditText e_lms;
	ArrayList<MyServerListObj> arrData_copy = new ArrayList<MyServerListObj>();

	CommonUtil dataSet = CommonUtil.getInstance();
	String [][]Object_Array;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serverdetail);
		list_number = (ListView)findViewById(R.id.list_number);
		e_lms = (EditText)findViewById(R.id.e_lms);

		Bundle bundle = getIntent().getExtras();
		obj = bundle.getParcelable("Object");
		Log.e("SKY", "ID :: " + obj.getG_id());

		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.btn_ok).setOnClickListener(btnListener);
		findViewById(R.id.btn_sp2).setOnClickListener(btnListener);
		findViewById(R.id.btn_sp3).setOnClickListener(btnListener);

		customProgressPop();
		String []val = {"item1","item2","item3","item4" };
		map.put("url", dataSet.SERVER + "Server_Phone_Sel.jsp");
		map.put("g_id", obj.getG_id());
		mThread = new AccumThread(this , mAfterAccum , map , 1 , 0 , val);

		mThread.start();		//스레드 시작!!

		m_Adapter = new LMSServerList_Adapter( this , arrData , mAfterAccum);
		list_number.setOnItemClickListener(mItemClickListener);
		list_number.setAdapter(m_Adapter);

	}
	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:	
				finish();
				break;
			case R.id.btn_sp2:	
				if (e_lms.getText().toString().length() ==0) {
					//모두 보여주기
					m_Adapter = new LMSServerList_Adapter( LMSServerDetailActivity.this , arrData , mAfterAccum);
					list_number.setAdapter(m_Adapter);
				}else {
					arrData_copy.clear();
					for (int i = 0; i < arrData.size(); i++) {
						if (arrData.get(i).getName().matches(".*" + e_lms.getText().toString() +".*") || arrData.get(i).getPhone().matches(".*" + e_lms.getText().toString() +".*")) {
							Log.e("SKY", "같은 값! :: " + i);
							arrData_copy.add(new MyServerListObj(arrData.get(i).getKey_index(), 
									arrData.get(i).getName(), 
									arrData.get(i).getPhone(), 
									arrData.get(i).getG_keyindex(), 
									0));
						}
					}
					m_Adapter = new LMSServerList_Adapter( LMSServerDetailActivity.this , arrData_copy , mAfterAccum);
					list_number.setAdapter(m_Adapter);
				}
				break;
			case R.id.btn_sp3:	
				break;
			case R.id.btn_ok:
				int j = 0;
				for (int i = 0; i < arrData.size(); i++) {
					if (arrData.get(i).getCheck() == 1) {
						j++;
						dataSet.arrData_real.add(new MyPhoneListObj2(0 ,
								arrData.get(i).getName(),
								arrData.get(i).getPhone(),
								arrData.get(i).getCheck(),
								Integer.parseInt(obj.getG_id())));
					}
				}
				if (j == 0) {
					AlertDialog.Builder ab = new AlertDialog.Builder(LMSServerDetailActivity.this , AlertDialog.THEME_HOLO_LIGHT);
					//		.setTitle("부적결제 후 전화상담 서비스로 연결 되며 12시간 동안 재연결 무료 입니다.\n(운수대톡 )")
					ab.setMessage("1개 이상은 선택해야 합니다.");
					ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							return;
						}
					})
					.show();
					return;
				}
				LMSMainActivity.onresume_0 = 1;
				finish();
				break;
			}
		}
	};
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
						arrData.add(new MyServerListObj(""+Object_Array[0][i], 
								Object_Array[1][i], 
								Object_Array[2][i], 
								Object_Array[3][i], 
								0));
					}
				}
				m_Adapter.notifyDataSetChanged();
			}else if(msg.arg1 == 2){
			}
		}
	};

	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			Log.e("SKY", "POSITION :: "+position);
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