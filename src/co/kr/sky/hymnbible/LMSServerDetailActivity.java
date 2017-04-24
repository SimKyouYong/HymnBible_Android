package co.kr.sky.hymnbible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;
import co.kr.sky.AccumThread;
import co.kr.sky.hymnbible.adapter.LMSMyPhoneList_Adapter;
import co.kr.sky.hymnbible.adapter.LMSServerList_Adapter;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.MyPhoneListObj;
import co.kr.sky.hymnbible.obj.MyPhoneListObj2;
import co.kr.sky.hymnbible.obj.MyServerGroupObj;
import co.kr.sky.hymnbible.obj.MyServerListObj;

public class LMSServerDetailActivity extends Activity implements OnEditorActionListener{
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
	private TextView font_1 , font_2 , font_3 , font_4 , t_count , t_name; 
	private Button btn_ok;

	ArrayList<MyServerListObj> arrData_copy = new ArrayList<MyServerListObj>();
	CheckBox check_all;

	CommonUtil dataSet = CommonUtil.getInstance();
	String [][]Object_Array;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serverdetail);
		list_number = (ListView)findViewById(R.id.list_number);
		e_lms = (EditText)findViewById(R.id.e_lms);
		t_name = (TextView)findViewById(R.id.t_name);
		check_all = (CheckBox)findViewById(R.id.check_all);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		t_count = (TextView)findViewById(R.id.t_count);
		
		
		ttf = Typeface.createFromAsset(getAssets(), "HANYGO230.TTF");
		
		btn_ok.setTypeface(ttf);
		t_name.setTypeface(ttf);
		t_count.setTypeface(ttf);
		e_lms.setTypeface(ttf);
		e_lms.setOnEditorActionListener(this); //mEditText와 onEditorActionListener를 연결
		
		
		Bundle bundle = getIntent().getExtras();
		obj = bundle.getParcelable("Object");
		Log.e("SKY", "ID :: " + obj.getKey_index());

		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.btn_ok).setOnClickListener(btnListener);
		findViewById(R.id.btn_sp2).setOnClickListener(btnListener);
		findViewById(R.id.btn_sp3).setOnClickListener(btnListener);

		customProgressPop();
		String []val = {"item1","item2","item3","item4" };
		map.put("url", dataSet.SERVER + "Server_Phone_Sel.jsp");
		map.put("key_index", obj.getKey_index());
		mThread = new AccumThread(this , mAfterAccum , map , 1 , 0 , val);

		mThread.start();		//스레드 시작!!

		m_Adapter = new LMSServerList_Adapter( this , arrData , mAfterAccum);
		list_number.setOnItemClickListener(mItemClickListener);
		list_number.setAdapter(m_Adapter);
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
	@Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // TODO Auto-generated method stub
        if(v.getId()==R.id.e_search1 && actionId==EditorInfo.IME_ACTION_SEARCH){ 
        	// 뷰의 id를 식별, 키보드의 완료 키 입력 검출
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
        }
        return false;
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
								1));
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
