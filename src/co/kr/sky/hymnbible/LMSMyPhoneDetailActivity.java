package co.kr.sky.hymnbible;

import java.util.ArrayList;
import java.util.TreeSet;

import android.app.Activity;
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
		setContentView(R.layout.activity_myphone);
		list_number = (ListView)findViewById(R.id.list_number);
		//list_number.setOnItemClickListener(mItemClickListener);

		Bundle bundle = getIntent().getExtras();
		obj = bundle.getParcelable("Object");
		Log.e("SKY", "ID :: " + obj.get_ID());

		if (obj.get_ID().indexOf(",") == -1) {
			//없을떄
			getSampleContactList(Integer.parseInt(obj.get_ID()));
		}else{
			String id_str[] = obj.get_ID().split(",");
			String[] dupArray=id_str;
			Object[] noDupArray=removeDuplicateArray(dupArray);
			for(int i=0; i<noDupArray.length; i++){
				System.out.println(i+"......."+(String)noDupArray[i]);
				getSampleContactList(Integer.parseInt((String)noDupArray[i]));
			}
		}


		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.btn_ok).setOnClickListener(btnListener);
		

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

	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:	
				finish();
				break;
			case R.id.btn_ok:
				LMSMyPhoneActivity.onresume_1 = 1;
				for (int i = 0; i < arrData.size(); i++) {
					if (arrData.get(i).getCHECK() == 1) {
						Log.e("SKY", "DATA SELECTED :: " + arrData.get(i).getCHECK());
						dataSet.arrData_real.add(new MyPhoneListObj2(LMSMyPhoneActivity.position_click ,
								arrData.get(i).getNAME(),
								arrData.get(i).getPHONE(),
								arrData.get(i).getCHECK()));
					}
				}
				finish();
				break;
			}
		}
	};
	public void getSampleContactList(int groupID) {

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
				arrData.add(new MyPhoneListObj(name, phone , 0));
			}
			pCur.close();

		}
		m_Adapter = new LMSMyPhoneList_Adapter( this , arrData , mAfterAccum);
		list_number.setOnItemClickListener(mItemClickListener);
		list_number.setAdapter(m_Adapter);
	}
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
