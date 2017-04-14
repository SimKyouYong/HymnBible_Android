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
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.LMSMainObj;

public class LMSMainActivity extends Activity{
	EditText lms_msg , phone_number;
	ListView                list_number;
	ArrayList<LMSMainObj> arrData = new ArrayList<LMSMainObj>();
	LMSMain_Adapter           m_Adapter;
	public static int onresume_0 = 0;
	CommonUtil dataSet = CommonUtil.getInstance();

	@Override
	public void onResume(){
		super.onResume();
		if (onresume_0 ==1) {
			onresume_0 = 0;
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
						if (arrData.get(j).getName().equals(dataSet.arrData_real.get(i).getNAME())  &&  arrData.get(j).getNumber().replace("-", "").equals(dataSet.arrData_real.get(i).getPHONE().replace("-", ""))) {
							flag = false;
						}
					}
					if (flag) {
						Log.e("SKY", "추가!");
						arrData.add(new LMSMainObj(dataSet.arrData_real.get(i).getNAME(), dataSet.arrData_real.get(i).getPHONE().replace("-", "")));
					}
				}
			}
			dataSet.arrData_real.clear();
			m_Adapter.notifyDataSetChanged();
		}
	}
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
		findViewById(R.id.send_lms).setOnClickListener(btnListener);


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
				Intent intent100 = new Intent(LMSMainActivity.this , LMSMyPhoneActivity.class);
				startActivityForResult(intent100, 100);
				break;
			case R.id.tab2:	
				//폰주소록
				Intent intent200 = new Intent(LMSMainActivity.this , LMSMyPhoneActivity.class);
				startActivityForResult(intent200, 200);
				break;
			case R.id.send_lms:
				
				if (arrData.size() == 0) {
					Toast.makeText(getApplicationContext(), "이름 혹은 전화번호를 입력해주세요.", 0).show();
				}else if(lms_msg.getText().toString().trim().length() == 0){
					Toast.makeText(getApplicationContext(), "보내실 문자를 입력해주세요.", 0).show();
				}else{
					for (int i = 0; i < arrData.size(); i++) {
						sendSMS(lms_msg.getText().toString(),arrData.get(i).getNumber());
					}
				}
				break;
			case R.id.number_plus:	
				if (phone_number.getText().toString().length() == 0 || phone_number.getText().toString().length() == 0) {
					Toast.makeText(getApplicationContext(), "이름 혹은 전화번호를 입력해주세요.", 0).show();
					return;
				}
				arrData.add(new LMSMainObj(phone_number.getText().toString().replace("-", ""), phone_number.getText().toString().replace("-", "")));
				m_Adapter.notifyDataSetChanged();
				break;
			case R.id.exel_plus:	
				break;


			}
		}
	};
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
}
