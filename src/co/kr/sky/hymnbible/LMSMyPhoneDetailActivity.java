package co.kr.sky.hymnbible;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;
import co.kr.sky.hymnbible.adapter.LMSMyPhone_Adapter;
import co.kr.sky.hymnbible.obj.MyPhoneGroupObj;

public class LMSMyPhoneDetailActivity extends Activity{
	LMSMyPhone_Adapter           m_Adapter;
	ArrayList<MyPhoneGroupObj> arrData = new ArrayList<MyPhoneGroupObj>();
	ListView                list_number;
	MyPhoneGroupObj obj;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myphone);
		list_number = (ListView)findViewById(R.id.list_number);
		//list_number.setOnItemClickListener(mItemClickListener);

		Bundle bundle = getIntent().getExtras();
		obj = bundle.getParcelable("Object");
		
		getSampleContactList(Integer.parseInt(obj.get_ID()));
		
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
	        }
	        pCur.close();
	    }
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
