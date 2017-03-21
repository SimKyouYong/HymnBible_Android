package co.kr.sky.hymnbible;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import co.kr.sky.hymnbible.adapter.LMSMain_Adapter;
import co.kr.sky.hymnbible.obj.LMSMainObj;

public class LMSMainActivity extends Activity{
	EditText lms_msg , phone_number;
	ListView                list_number;
	ArrayList<LMSMainObj> arrData = new ArrayList<LMSMainObj>();
	LMSMain_Adapter           m_Adapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lmsmain);

		lms_msg = (EditText)findViewById(R.id.lms_msg);
		phone_number = (EditText)findViewById(R.id.phone_number);
		list_number = (ListView)findViewById(R.id.list_number);


		findViewById(R.id.bottomview_l).setOnClickListener(btnListener);
		findViewById(R.id.bottomview_c).setOnClickListener(btnListener);
		findViewById(R.id.bottomview_r).setOnClickListener(btnListener);
		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.tab1).setOnClickListener(btnListener);
		findViewById(R.id.tab2).setOnClickListener(btnListener);
		findViewById(R.id.number_plus).setOnClickListener(btnListener);
		findViewById(R.id.exel_plus).setOnClickListener(btnListener);


		m_Adapter = new LMSMain_Adapter( this , arrData , mAfterAccum);
		list_number.setAdapter(m_Adapter);
	}
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
			} 
		}
	};
	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:	
				finish();
				break;
			case R.id.bottomview_l:	
				break;

			case R.id.bottomview_c:	
				break;
			case R.id.bottomview_r:	
				finish();
				break;

			case R.id.tab1:	
				//폰주소록
				Intent intent100 = new Intent(LMSMainActivity.this , LMSMainActivity.class);
				startActivityForResult(intent100, 100);
				break;
			case R.id.tab2:	
				//폰주소록
				Intent intent200 = new Intent(LMSMainActivity.this , LMSMainActivity.class);
				startActivityForResult(intent200, 200);
				break;
			case R.id.send_lms:
				if (phone_number.getText().toString().trim().length() > 0) {
					for (int i = 0; i < FOCUSED_STATE_SET.length; i++) {
						Log.e("SKY" , "I :: "  +i);
						sendSMS(phone_number.getText().toString(),arrData.get(i).getNumber());
					}
				}else{
					Toast.makeText(getApplicationContext(), "보내실 문자를 입력해주세요.", 0).show();
				}
				break;
			case R.id.number_plus:	
				arrData.add(new LMSMainObj(phone_number.getText().toString().replace("-", ""), phone_number.getText().toString().replace("-", "")));
				m_Adapter.notifyDataSetChanged();
				break;
			case R.id.exel_plus:	
				break;


			}
		}
	};
	private void sendSMS(String msg, String number) {
		SmsManager sm = SmsManager.getDefault();

		if(msg.getBytes().length > 80) {
			ArrayList<String> parts = sm.divideMessage(msg);
			sm.sendMultipartTextMessage(number, null, parts, null, null);
		}
		else
			sm.sendTextMessage(number, null, msg, null, null);
	}
}
