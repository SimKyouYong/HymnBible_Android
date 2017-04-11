package co.kr.sky.hymnbible;

import java.util.ArrayList;
import java.util.TreeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import co.kr.sky.hymnbible.adapter.LMSMyPhoneGroup_Adapter;
import co.kr.sky.hymnbible.adapter.LMSMyPhoneList_Adapter;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.MyPhoneGroupObj;
import co.kr.sky.hymnbible.obj.MyPhoneListObj;
import co.kr.sky.hymnbible.obj.MyPhoneListObj2;

public class LMSMyPhoneDetailActivity extends Activity{
	LMSMyPhoneList_Adapter           m_Adapter;
	ArrayList<MyPhoneListObj> arrData = new ArrayList<MyPhoneListObj>();
	ListView                list_number;
	MyPhoneGroupObj obj;
	CommonUtil dataSet = CommonUtil.getInstance();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myphonedetail);
		list_number = (ListView)findViewById(R.id.list_number);
		//list_number.setOnItemClickListener(mItemClickListener);

		Bundle bundle = getIntent().getExtras();
		obj = bundle.getParcelable("Object");
		Log.e("SKY", "ID :: " + obj.get_ID());

		//디비 조회해서 값 뿌려주면 끝!!

		
		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.btn_ok).setOnClickListener(btnListener);
		
		m_Adapter = new LMSMyPhoneList_Adapter( this , arrData , mAfterAccum);
		list_number.setOnItemClickListener(mItemClickListener);
		list_number.setAdapter(m_Adapter);
		
		SELECT_Phone();

	}
	public void SELECT_Phone()		//디비 값 조회해서 저장하기
	{
		arrData.clear();
		try{
			//  db파일 읽어오기
			SQLiteDatabase db = openOrCreateDatabase("phonedb.db", Context.MODE_PRIVATE, null);
			// 쿼리로 db의 커서 획득
			Cursor cur = db.rawQuery("SELECT * FROM `phone`;", null);
			// 처음 레코드로 이동
			while(cur.moveToNext()){
				// 읽은값 출력
				Log.i("MiniApp",cur.getString(0)+"/"+cur.getString(1)+"/"+cur.getString(2));
				arrData.add(new MyPhoneListObj(cur.getString(1), cur.getString(2), 0));
			}
			cur.close();
			db.close();
			m_Adapter.notifyDataSetChanged();
			
		}
		catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ",se.toString());
		}   

	}
	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:	
				finish();
				break;
			case R.id.btn_ok:
				int j = 0;
				for (int i = 0; i < arrData.size(); i++) {
					if (arrData.get(i).getCHECK() == 1) {
						j++;
						dataSet.arrData_real.add(new MyPhoneListObj2(LMSMyPhoneActivity.position_click ,
								arrData.get(i).getNAME(),
								arrData.get(i).getPHONE(),
								arrData.get(i).getCHECK()));
					}
				}
				if (j > 499) {
					//499개 보다 많으면.. 다음화면으로 못가게 맊음.!
					AlertDialog.Builder ab = new AlertDialog.Builder(LMSMyPhoneDetailActivity.this , AlertDialog.THEME_HOLO_LIGHT);
					//		.setTitle("부적결제 후 전화상담 서비스로 연결 되며 12시간 동안 재연결 무료 입니다.\n(운수대톡 )")
					ab.setMessage("500개 이상 선택은 불가능합니다.");
					ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							return;
						}
					})
					.show();
				}else{
					LMSMyPhoneActivity.onresume_1 = 1;
					finish();
				}
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
				String res = (String)msg.obj;
				Log.e("SKY" , "RESULT  -> " + res);
				//arrData.remove(Integer.parseInt(res));
				//m_Adapter.notifyDataSetChanged();
			} 
		}
	};
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			Log.e("SKY", "POSITION : " + position);
			if (arrData.get(position).getCHECK() == 0) {
				arrData.get(position).setCHECK(1);
			}else{
				arrData.get(position).setCHECK(0);
			}
			m_Adapter.notifyDataSetChanged();

		}
	};
}
