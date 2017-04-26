package co.kr.sky.hymnbible;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import co.kr.sky.hymnbible.adapter.LMSHistory_Adapter;
import co.kr.sky.hymnbible.common.Check_Preferences;
import co.kr.sky.hymnbible.obj.HistoryObj;

public class LMSHistoryActivity extends Activity{
	LMSHistory_Adapter           m_Adapter;
	ListView                list_number;
	private Typeface ttf;

	Button bottomview_exit;
	TextView titlename , title , font_txt1 , font_txt2;
	ArrayList<HistoryObj> arr = new ArrayList<HistoryObj>();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		list_number = (ListView)findViewById(R.id.list_number);
		title = (TextView)findViewById(R.id.title);
		titlename = (TextView)findViewById(R.id.titlename);
		font_txt1 = (TextView)findViewById(R.id.font_txt1);
		font_txt2 = (TextView)findViewById(R.id.font_txt2);
		bottomview_exit = (Button)findViewById(R.id.bottomview_exit);

		ttf = Typeface.createFromAsset(getAssets(), "HANYGO230.TTF");


		title.setTypeface(ttf);
		titlename.setTypeface(ttf);
		font_txt1.setTypeface(ttf);
		font_txt2.setTypeface(ttf);
		bottomview_exit.setTypeface(ttf);


		title.setText("발송내역");
		titlename.setText("단체문자");
		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.bottomview_exit).setOnClickListener(btnListener);


		SELECT_Phone();
	}
	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:	
				finish();
				break;
			case R.id.bottomview_exit:	
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
			if (msg.arg1  == 9001 ) {
				int arg2 = (int)msg.arg2;
				//삭제
				DEL_History(arg2);
			}
		}
	};
	public void DEL_History(int del)		//디비 값 조회해서 저장하기
	{
		try{
			//  db파일 읽어오기
			SQLiteDatabase db = openOrCreateDatabase("phonedb.db", Context.MODE_PRIVATE, null);
			// 쿼리로 db의 커서 획득

			String sql = "delete from `lms_history` where key_index = '" + del + "'";
			Log.e("SKY","sql2  : "+ sql);
			db.execSQL(sql);
			db.close();
			SELECT_Phone();
		}
		catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ",se.toString());
		}   

	}
	public void SELECT_Phone()		//디비 값 조회해서 저장하기
	{
		arr.clear();
		try{
			//  db파일 읽어오기
			SQLiteDatabase db = openOrCreateDatabase("lms_history.db", Context.MODE_PRIVATE, null);
			// 쿼리로 db의 커서 획득
			Cursor cur = db.rawQuery("SELECT * FROM `lms_history`;", null);
			// 처음 레코드로 이동
			int count = 0;
			while(cur.moveToNext()){
				// 읽은값 출력
				Log.i("MiniApp",cur.getString(0)+"/"+cur.getString(1)+"/"+cur.getString(2)+"/"+cur.getString(3));
				arr.add(new HistoryObj(cur.getString(0), cur.getString(1), cur.getString(2), cur.getString(3)));
			}
			cur.close();
			db.close();
			Log.e("SKY" , "SELECT_Phone count :: " + count);

			m_Adapter = new LMSHistory_Adapter( LMSHistoryActivity.this , arr , mAfterAccum);
			list_number.setAdapter(m_Adapter);
		}
		catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ",se.toString());
		}   

	}

}
