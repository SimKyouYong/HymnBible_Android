package co.kr.sky.hymnbible;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;
import co.kr.sky.hymnbible.adapter.LMSMyPhoneList_Adapter;
import co.kr.sky.hymnbible.obj.MyPhoneGroupObj;
import co.kr.sky.hymnbible.obj.MyPhoneListObj;

public class LMSMyPhoneDetailActivity extends Activity{
	LMSMyPhoneList_Adapter           m_Adapter;
	ArrayList<MyPhoneListObj> arrData = new ArrayList<MyPhoneListObj>();
	ListView                list_number;
	MyPhoneGroupObj obj;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myphone);
		list_number = (ListView)findViewById(R.id.list_number);
		//list_number.setOnItemClickListener(mItemClickListener);

		Bundle bundle = getIntent().getExtras();
		obj = bundle.getParcelable("Object");
		String id_str[] = obj.get_ID().split(",");
		
		
		for (int i = 0; i < id_str.length; i++) {
			getSampleContactList(Integer.parseInt(id_str[i]));
		}
	}
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
				arrData.add(new MyPhoneListObj(name, phone));
	        }
	        pCur.close();
	        
	    }
//	    m_Adapter = new LMSMyPhoneList_Adapter( this , arrData , mAfterAccum);
//		list_number.setAdapter(m_Adapter);
	}
//	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
//		public void onItemClick(AdapterView parent, View view, int position,
//				long id) {
//			Intent board = new Intent(LMSMyPhoneDetailActivity.this, HellowTalk_Detail_Comment.class);
//			board.putExtra("Object", arrData.get(position));
//			startActivity(board);
//		}
//	};
}
