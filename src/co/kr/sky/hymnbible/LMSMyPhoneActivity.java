package co.kr.sky.hymnbible;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import co.kr.sky.hymnbible.adapter.LMSMyPhone_Adapter;
import co.kr.sky.hymnbible.obj.MyPhoneGroupObj;

public class LMSMyPhoneActivity extends Activity{
	LMSMyPhone_Adapter           m_Adapter;
	ArrayList<MyPhoneGroupObj> arrData = new ArrayList<MyPhoneGroupObj>();
	ListView                list_number;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myphone);
		list_number = (ListView)findViewById(R.id.list_number);
		list_number.setOnItemClickListener(mItemClickListener);

		
		
		
		getGroup();
	}
	Handler mAfterAccum = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.arg1  == 0 ) {
				String res = (String)msg.obj;
				Log.e("SKY" , "RESULT  -> " + res);
				arrData.remove(Integer.parseInt(res));
				m_Adapter.notifyDataSetChanged();
			} 
		}
	};
	private void getGroup() {
		Cursor cursor = getContentResolver().query(
				ContactsContract.Groups.CONTENT_URI, 
				new String[] {
						ContactsContract.Groups._ID,
						ContactsContract.Groups.TITLE,
						ContactsContract.Groups.ACCOUNT_NAME,
						ContactsContract.Groups.ACCOUNT_TYPE,
						ContactsContract.Groups.DELETED,
						ContactsContract.Groups.GROUP_VISIBLE
				}, 
				ContactsContract.Groups.DELETED + "=0" + " AND " +
						ContactsContract.Groups.GROUP_VISIBLE + "=1", 
						null, 
						null);

		
		try {
			while (cursor.moveToNext()) {
				int _ID = 				cursor.getInt(0);
				String TITLE = 			cursor.getString(1);
				String ACCOUNT_NAME = 		cursor.getString(2);
				String ACCOUNT_TYPE = 		cursor.getString(3);
				String DELETED = 			cursor.getString(4);
				String GROUP_VISIBLE = 	cursor.getString(5);
				int GROUP_COUNT = 		getGroupSummaryCount(cursor.getString(0));
				Log.e("SKY" , "1.GROUP_COUNT :: " + GROUP_COUNT);
				if (GROUP_COUNT  > 0) {
					if (arrData.size() == 0) {
						/*
						Log.e("SKY" , "최초 등록!");
						Log.e("SKY" , "_ID :: " + _ID);
						Log.e("SKY" , "TITLE :: " + TITLE);
						Log.e("SKY" , "ACCOUNT_NAME :: " + ACCOUNT_NAME);
						Log.e("SKY" , "ACCOUNT_TYPE :: " + ACCOUNT_TYPE);
						Log.e("SKY" , "DELETED :: " + DELETED);
						Log.e("SKY" , "GROUP_VISIBLE :: " + GROUP_VISIBLE);
						Log.e("SKY" , "GROUP_COUNT :: " + GROUP_COUNT);
						*/
						arrData.add(new MyPhoneGroupObj(""+_ID, 
								TITLE, 
								ACCOUNT_NAME, 
								ACCOUNT_TYPE, 
								DELETED,
								GROUP_VISIBLE, 
								""+GROUP_COUNT, 
								"false"));
					}else{
						/*
						Log.e("SKY" , "_ID :: " + _ID);
						Log.e("SKY" , "TITLE :: " + TITLE);
						Log.e("SKY" , "ACCOUNT_NAME :: " + ACCOUNT_NAME);
						Log.e("SKY" , "ACCOUNT_TYPE :: " + ACCOUNT_TYPE);
						Log.e("SKY" , "DELETED :: " + DELETED);
						Log.e("SKY" , "GROUP_VISIBLE :: " + GROUP_VISIBLE);
						Log.e("SKY" , "GROUP_COUNT :: " + GROUP_COUNT);
						Log.e("SKY" , "arrDataSIZE i  :: " + arrData.size());
						*/
						for (int i = 0; i < arrData.size(); i++) {
							if (arrData.get(i).getTITLE().equals(TITLE)) {
								Log.e("SKY" , "for if  :: " + i);
								//add
								arrData.get(i).set_Add_ID(""+_ID);
								arrData.get(i).set_Add_GROUP_COUNT(GROUP_COUNT);
							}else{
//								Log.e("SKY" , "for else  :: " + i);
								//default add
								arrData.add(new MyPhoneGroupObj(""+_ID, 
										TITLE, 
										ACCOUNT_NAME, 
										ACCOUNT_TYPE, 
										DELETED,
										GROUP_VISIBLE, 
										""+GROUP_COUNT, 
										"false"));
							}
							continue;
						}
					}
				}
			}
		} finally {
			cursor.close();
			m_Adapter = new LMSMyPhone_Adapter( this , arrData , mAfterAccum);
			list_number.setAdapter(m_Adapter);
		}
	}
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			Intent board = new Intent(LMSMyPhoneActivity.this, LMSMyPhoneDetailActivity.class);
			board.putExtra("Object", arrData.get(position));
			startActivity(board);
		}
	};
	private int getGroupSummaryCount(String groupId) {
		Uri uri = ContactsContract.Groups.CONTENT_SUMMARY_URI;
		String[] projection = new String[]  { 
				ContactsContract.Groups.SUMMARY_COUNT,
				ContactsContract.Groups.ACCOUNT_NAME,
				ContactsContract.Groups.ACCOUNT_TYPE
		};
		String selection = ContactsContract.Groups._ID + "=" + groupId;
		Cursor cursor = managedQuery(uri, projection, selection, null, null);
		int cnt = 0;
		while (cursor.moveToNext()) {
			cnt = cursor.getInt(0);
		}
		return cnt;
	}
}
