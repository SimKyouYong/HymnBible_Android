package co.kr.sky.hymnbible;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;
import co.kr.sky.hymnbible.adapter.LMSMyPhone_Adapter;
import co.kr.sky.hymnbible.obj.MyPhoneObj;

public class LMSMyPhoneActivity extends Activity{
	LMSMyPhone_Adapter           m_Adapter;
	ArrayList<MyPhoneObj> arrData = new ArrayList<MyPhoneObj>();
	ListView                list_number;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myphone);
		list_number = (ListView)findViewById(R.id.list_number);
		
		
		
		
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
				arrData.add(new MyPhoneObj(""+cursor.getInt(0), 
						cursor.getString(1), 
						cursor.getString(2), 
						cursor.getString(3), 
						cursor.getString(4),
						cursor.getString(5), 
						""+getGroupSummaryCount(cursor.getString(0)), 
						"false"));
				Log.e("SKY" , "_ID :: " + cursor.getInt(0));
				Log.e("SKY" , "TITLE :: " + cursor.getString(1));
				Log.e("SKY" , "ACCOUNT_NAME :: " + cursor.getString(2));
				Log.e("SKY" , "ACCOUNT_TYPE :: " + cursor.getString(3));
				Log.e("SKY" , "DELETED :: " + cursor.getString(4));
				Log.e("SKY" , "GROUP_VISIBLE :: " + cursor.getString(5));
				Log.e("SKY" , "GROUP_COUNT :: " + getGroupSummaryCount(cursor.getString(0)));
			}
		} finally {
			cursor.close();
			m_Adapter = new LMSMyPhone_Adapter( this , arrData , mAfterAccum);
			list_number.setAdapter(m_Adapter);
		}
	}
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
