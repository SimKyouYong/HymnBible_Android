package co.kr.sky.hymnbible;

import java.util.ArrayList;
import java.util.TreeSet;

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
import android.widget.Button;
import android.widget.ListView;
import co.kr.sky.hymnbible.adapter.LMSMyPhoneGroup_Adapter;
import co.kr.sky.hymnbible.adapter.LMSMyPhoneList_Adapter;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.MyPhoneGroupObj;
import co.kr.sky.hymnbible.obj.MyPhoneListObj;

public class LMSMyPhoneActivity extends Activity{
	LMSMyPhoneGroup_Adapter           m_Adapter;
	ArrayList<MyPhoneGroupObj> arrData = new ArrayList<MyPhoneGroupObj>();
	ListView                list_number;
	CommonUtil dataSet = CommonUtil.getInstance();

	public static int position_click = 0;
	public static int onresume_1 = 0;
	@Override
	public void onResume(){
		super.onResume();
		if (onresume_1 == 1) {
			onresume_1 = 0;
			int count = 0;
			for (int i = 0; i < dataSet.arrData_real.size(); i++) {
				if (position_click == dataSet.arrData_real.get(i).getPosition()) {
					count++;
				}
			}
			arrData.get(position_click).setSELECTED_COUNT(count);
			m_Adapter.notifyDataSetChanged();
			((Button)findViewById(R.id.btn_count)).setText(""+dataSet.arrData_real.size());
		}
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myphone);
		list_number = (ListView)findViewById(R.id.list_number);
		
		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.btn_server).setOnClickListener(btnListener);
		findViewById(R.id.btn_reflash).setOnClickListener(btnListener);
		findViewById(R.id.btn_ok).setOnClickListener(btnListener);

		getGroup();
	}
	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:	
				finish();
				break;
			case R.id.btn_server:	
				break;
			case R.id.btn_reflash:
				getGroup();
				break;
			case R.id.btn_ok:	
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
				String res = (String)msg.obj;
				Log.e("SKY" , "RESULT  -> " + res);
				arrData.remove(Integer.parseInt(res));
				m_Adapter.notifyDataSetChanged();
			} 
		}
	};
	private void getGroup() {
		arrData.clear();
		dataSet.arrData_real.clear();
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
								""+0, 
								"false",
								0,
								0));
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
//								Log.e("SKY" , "for if getTITLE :: " + TITLE);
//								Log.e("SKY" , "for if GROUP_COUNT :: " + GROUP_COUNT);
								//add
								arrData.get(i).set_Add_ID(""+_ID);
								arrData.get(i).set_Add_GROUP_COUNT(0);
							}else{
								//								Log.e("SKY" , "for else  :: " + i);
								//default add
//								Log.e("SKY" , "2for if getTITLE :: " + TITLE);
//								Log.e("SKY" , "2for if GROUP_COUNT :: " + GROUP_COUNT);
								arrData.add(new MyPhoneGroupObj(""+_ID, 
										TITLE, 
										ACCOUNT_NAME, 
										ACCOUNT_TYPE, 
										DELETED,
										GROUP_VISIBLE, 
										""+0, 
										"false",
										0,
										0));
							}
							continue;
						}
					}
				}
			}
		} finally {
			cursor.close();
			for (int i = 0; i < arrData.size(); i++) {
				//그룹 테이블인설트 !
				//key // name // count 
				Log.e("SKY" , "그룹 테이블인설트 ! :: " +  arrData.get(i).get_ID() );
				if (arrData.get(i).get_ID().indexOf(",") == -1) {
					//없을떄
					getSampleContactList(Integer.parseInt(arrData.get(i).get_ID()) , i);
				}else{
					String id_str[] = arrData.get(i).get_ID().split(",");
					String[] dupArray=id_str;
					Object[] noDupArray=removeDuplicateArray(dupArray);
					for(int j=0; j<noDupArray.length; j++){
						//Log.e("SKY" , "for if GROUP_COUNT :: " + (String)noDupArray[j] +"//i :: "+ i  );
						getSampleContactList(Integer.parseInt((String)noDupArray[j]) , i);
					}
				}
			}
			
			
			
			m_Adapter = new LMSMyPhoneGroup_Adapter( this , arrData , mAfterAccum);
			list_number.setOnItemClickListener(mItemClickListener);
			list_number.setAdapter(m_Adapter);
		}
	}
	public void getSampleContactList(int groupID , int ii) {

		Uri groupURI = ContactsContract.Data.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID };

		Cursor c = getContentResolver().query(
				groupURI,
				projection,
				ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
				+ "=" + groupID, null, null);
		
		while (c.moveToNext()) {
			String id = c
					.getString(c
							.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));
			Cursor pCur = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
					new String[] { id }, null);

			int i =0;
			while (pCur.moveToNext()) {
				i++;
				String name = pCur
						.getString(pCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phone = pCur
						.getString(pCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				Log.e("SKY" , "" + i + ".name:: " + name + " // phone :: " + phone);
				arrData.get(ii).set_Add_GROUP_COUNT(1);
				//회원 테이블인설트 !
				//key // name // phone // group_key 
				//arrData.add(new MyPhoneListObj(name, phone , 0));
			}
			pCur.close();

		}
	}
	public Object[] removeDuplicateArray(String[] array){
		Object[] removeArray=null;
		TreeSet ts=new TreeSet();
		for(int i=0; i<array.length; i++){
			ts.add(array[i]);
		}
		removeArray= ts.toArray();
		return removeArray;
	}
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			Log.e("SKY", "POSITION :: "+position);
			arrData.get(position).setSELECTED(1);
			position_click = position;
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
