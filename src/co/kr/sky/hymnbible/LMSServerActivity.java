package co.kr.sky.hymnbible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import co.kr.sky.AccumThread;
import co.kr.sky.hymnbible.adapter.LMSServerPhoneGroup_Adapter;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.MyServerGroupObj;

public class LMSServerActivity extends Activity{
	protected ProgressDialog customDialog = null;
	ArrayList<MyServerGroupObj> arrData = new ArrayList<MyServerGroupObj>();
	LMSServerPhoneGroup_Adapter           m_Adapter;
	ListView                list_number;
	private Typeface ttf;

	Map<String, String> map = new HashMap<String, String>();
	AccumThread mThread;
	TextView title;

	CommonUtil dataSet = CommonUtil.getInstance();
	String [][]Object_Array;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
		list_number = (ListView)findViewById(R.id.list_number);
		title = (TextView)findViewById(R.id.title);
		ttf = Typeface.createFromAsset(getAssets(), "HANYGO230.TTF");
		title.setTypeface(ttf);

		m_Adapter = new LMSServerPhoneGroup_Adapter( this , arrData , mAfterAccum);
		list_number.setOnItemClickListener(mItemClickListener);
		list_number.setAdapter(m_Adapter);

		customProgressPop();
		String []val = {"item1","item2","item3","item4" , "item5"};
		map.put("url", dataSet.SERVER + "Server_Sel.jsp");
		map.put("my_phone", dataSet.PHONE);
		mThread = new AccumThread(this , mAfterAccum , map , 1 , 0 , val);

		mThread.start();		//스레드 시작!!


		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.btn_reflash).setOnClickListener(btnListener);
		findViewById(R.id.btn_ok).setOnClickListener(btnListener);

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
				String []val = {"item1","item2","item3","item4" , "item5"};
				map.put("url", dataSet.SERVER + "Server_Sel.jsp");
				map.put("my_phone", dataSet.PHONE);
				mThread = new AccumThread(LMSServerActivity.this , mAfterAccum , map , 1 , 0 , val);

				mThread.start();		//스레드 시작!!
				break;
			case R.id.btn_ok:	
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
						arrData.add(new MyServerGroupObj(""+Object_Array[0][i], 
								Object_Array[1][i], 
								Object_Array[2][i], 
								Object_Array[3][i], 
								Object_Array[4][i],
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
			Log.e("SKY", "POSITION :: "+arrData.get(position).getG_id());
			//arrData.get(position).setSELECTED(1);
			Intent board = new Intent(LMSServerActivity.this, LMSServerDetailActivity.class);
			board.putExtra("Object", arrData.get(position));
			startActivity(board);
			finish();
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
