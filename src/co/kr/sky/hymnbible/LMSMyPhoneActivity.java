package co.kr.sky.hymnbible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import co.kr.sky.AccumThread;
import co.kr.sky.hymnbible.adapter.LMSMyPhoneGroup_Adapter;
import co.kr.sky.hymnbible.common.Check_Preferences;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.fun.MySQLiteOpenHelper;
import co.kr.sky.hymnbible.obj.MyPhoneGroupObj;
import co.kr.sky.hymnbible.obj.MyPhoneListObj;
import co.kr.sky.hymnbible.obj.MyPhoneListObj2;

public class LMSMyPhoneActivity extends Activity{
	LMSMyPhoneGroup_Adapter           m_Adapter;
	ArrayList<MyPhoneGroupObj> arrData = new ArrayList<MyPhoneGroupObj>();
	ListView                list_number;
	CommonUtil dataSet = CommonUtil.getInstance();
	protected ProgressDialog customDialog = null;
	private Typeface ttf;
	CheckBox check_all;
	Map<String, String> map = new HashMap<String, String>();
	AccumThread mThread;
	private TextView font_1 , font_2 , font_3 , font_4 , t_count , t_name; 
	TextView title ;
	public static TextView check_count;
	public static int onresume_1 = 0;
	int count_server = 0;
	
	private HashMap<String, String> group_kr;
	private HashMap<String, String> group_title;
	private HashMap<String, Integer> group_count;
	@Override
	public void onResume(){
		super.onResume();
		if (LMSMainActivity.onresume_0 ==1) {
			//그냥 끄기!
			finish();
		}
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myphone);
		list_number = (ListView)findViewById(R.id.list_number);
		check_all = (CheckBox)findViewById(R.id.check_all);
		title = (TextView)findViewById(R.id.title);
		check_count = (TextView)findViewById(R.id.check_count);

		font_1 = (TextView)findViewById(R.id.font_1);
		font_2 = (TextView)findViewById(R.id.font_2);
		font_3 = (TextView)findViewById(R.id.font_3);
		font_4 = (TextView)findViewById(R.id.font_4);
		t_count = (TextView)findViewById(R.id.t_count);
		t_name = (TextView)findViewById(R.id.t_name);

		ttf = Typeface.createFromAsset(getAssets(), "HANYGO230.TTF");




		title.setTypeface(ttf);

		font_1.setTypeface(ttf);
		font_2.setTypeface(ttf);
		font_3.setTypeface(ttf);
		font_4.setTypeface(ttf);
		t_count.setTypeface(ttf);
		t_name.setTypeface(ttf);
		check_count.setTypeface(ttf);

		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.btn_server).setOnClickListener(btnListener);
		findViewById(R.id.btn_server1).setOnClickListener(btnListener);
		findViewById(R.id.btn_reflash).setOnClickListener(btnListener);
		findViewById(R.id.btn_ok).setOnClickListener(btnListener);

		group_title=new HashMap<String, String>();
    	group_count=new HashMap<String, Integer>();

		m_Adapter = new LMSMyPhoneGroup_Adapter( this , arrData , mAfterAccum);
		list_number.setOnItemClickListener(mItemClickListener);
		list_number.setAdapter(m_Adapter);

		//디비에 값이 있으면 디비 조
		if (!Check_Preferences.getAppPreferencesboolean(this, "phonedb")) {

			customProgressPop();
			AccumThread1 av = new AccumThread1();
			av.start();
		}else{
			Log.e("SKY", "DB 조회해오기");
			SELECT_GROUP();
		}

		check_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {

				if (buttonView.getId() == R.id.check_all) {
					if (isChecked) {
						Log.e("SKY" , "all클릭");
						Message msg2 = mAfterAccum.obtainMessage();
						msg2.arg1 = 5000;
						mAfterAccum.sendMessage(msg2);
					} else {
						Log.e("SKY" , "all not 클릭" );
						Message msg2 = mAfterAccum.obtainMessage();
						msg2.arg1 = 6000;
						mAfterAccum.sendMessage(msg2);
					}
				}
			}
		});

	}
	public class AccumThread1 extends Thread{
		public AccumThread1(){
		}
		@Override
		public void run()
		{
			getGroupContacts();
			//getGroup();
			Message msg2 = mAfterAccum.obtainMessage();
			msg2.arg1 = 100;
			mAfterAccum.sendMessage(msg2);
		}
	}
	private void getGroupContacts(){
	    Log.e("SKY","--getGroupContacts--");
	    ArrayList<String > arr = new ArrayList<String>();
		Uri uri_group = ContactsContract.Groups.CONTENT_SUMMARY_URI;
		String[] group_projection = new String[]{
				ContactsContract.Groups._ID,
				ContactsContract.Groups.TITLE
		};
		String group_selection = ContactsContract.Groups.DELETED + " = 0 AND " + ContactsContract.Groups.GROUP_VISIBLE + " = 1";
		String orderby = ContactsContract.Groups.TITLE+ " COLLATE LOCALIZED ASC";
	    Cursor gc = managedQuery(uri_group, group_projection, group_selection, null,orderby);
	    while(gc.moveToNext())
	    {
	    	String gtitle=gc.getString(1);
	    	if(gtitle!=null && !gtitle.equals(""))
	    	{
	    		int _ID = gc.getInt(0);
	    		int people_count=0;
		    	String selection=ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "="+gc.getString(0);
//	    		String[] qry={ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID};
//		    	Cursor people_rlt = managedQuery(ContactsContract.Data.CONTENT_URI,qry, selection, null,null);//		    	
//		    	final int people_count = people_rlt.getCount();
//		    	Log.i("getGroupContacts",gc.getString(0)+" "+gc.getString(1)+" ("+people_count+")");
	    		Uri lookupUri = Uri.withAppendedPath(ContactsContract.Data.CONTENT_URI, "");
			    Cursor is_c = getContentResolver().query(
			    		lookupUri, 
			    		new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID},
			    		selection,
			    		null,
			    		null);
			    
			    people_count=is_c.getCount();
			    try {
			    	people_count=is_c.getCount();
			    	
			    } catch (Exception e) {}
			    finally{is_c.close();}
		    	if(group_title.get(gtitle)!=null)
		    	{
		    		int g_tcount = group_count.get(gtitle)+people_count;
		    		group_count.put(gtitle,g_tcount);
		    		Log.e("SKY" , "1::_ID :: " + _ID);
		    		Log.e("SKY" , "1::gtitle :: " + gtitle);
		    		Log.e("SKY" , "1::g_tcount :: " + g_tcount);
		    		arr.add(""+_ID);
		    		SAVE_DB_Group(gtitle ,""+g_tcount ,""+ _ID );
		    		getSampleContactList(_ID);
		    	}else{
		    		group_title.put(gtitle,gtitle);
		    		group_count.put(gtitle,people_count);
		    		Log.e("SKY" , "2::_ID :: " + _ID);
		    		Log.e("SKY" , "2::gtitle :: " + gtitle);
		    		Log.e("SKY" , "2::people_count :: " + people_count);
		    		arr.add(""+_ID);
		    		
		    		SAVE_DB_Group(gtitle ,""+people_count ,""+ _ID );
		    		getSampleContactList(_ID);
		    	}
	    	}
	    }
	    //getGroupall(and_where);
	    String[] array = new String[arr.size()];
	    int size=0;
	    for(String temp : arr){
	      array[size++] = temp;
	    }
	    //getGroupall(array);
	    getSampleContactList2(array);
	    //미지정 가져오기
	}
	public void getSampleContactList2(String[] groupID) {
		Log.e("SKY" , "--getSampleContactList2-- :: " + groupID);
		String var = "";
		for (int i = 0; i < groupID.length; i++) {
			Log.e("SKY" , "--var-- :: " + groupID[i]);
			if (i ==0) {
				var += ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + " not like ?";
			}else{
				var += " and " + ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + " not like ?";
			}
		}
		Log.e("SKY" , "--var-- :: " + var);

	    Uri groupURI = ContactsContract.Data.CONTENT_URI;
	    String[] projection = new String[] {
	            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
	            ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID
	            };

	    Cursor c = getContentResolver().query(
	            groupURI,
	            projection,
	            var, 
	            groupID, 
	            null);

	    int count_all = 0;
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
				Log.e("SKY" , "aaa" + count_all + ".name:: " + name + " // phone :: " + phone + "//contact :: " +c.getString(1));
				Phone_ox(name.replace(" ", "") , phone.replace("-", ""));
				
				//SAVE_DB_Phone(name, phone, ""+groupID);
				count_all++;
	        }
	        pCur.close();
	    }
	    
	}
	public Boolean Phone_ox(String name, String phone)
	{
		Boolean ox= false;
		try{
			//인서트쿼리
			SQLiteDatabase db = openOrCreateDatabase("phonedb.db", Context.MODE_PRIVATE, null);
			String sql;
			try {
				
				Cursor cur = db.rawQuery("SELECT COUNT(*) FROM phone where name ='" + name +"' and phone = '" + phone +"'", null);
				// 처음 레코드로 이동
				while(cur.moveToNext()){
					Log.e("SKY" , "cur :: " + cur.getString(0));
					if (cur.getString(0).equals("0")) {
						ox = true;
					}
				}
				cur.close();
				db.close();
			} catch (Exception e) {
				db.close();
				Log.e("MiniApp","sql error : "+ e.toString());
			}
			db.close();
		}catch (Exception e) {
			Log.e("SKY","onPostExecute error : "+ e.toString());
		}
		if (ox) {
			Log.e("SKY" , "ox :: INSERT GOGO");
			Log.e("SKY" , "ox :: name ::" + name + " -- phone :: " + phone);
			//db 인설트! 미지정
		}else{
			Log.e("SKY" , "ox :: INSERT XXXX");
		}
		
		return ox;
	}
	private void getGroupall(String[] groupID){
		String var = "";
		for (int i = 0; i < groupID.length; i++) {
			Log.e("SKY" , "--var-- :: " + groupID[i]);
			if (i ==0) {
				var += ContactsContract.Contacts._ID + " not like " + groupID[i];
			}else{
				var += " and " + ContactsContract.Contacts._ID + " not like "+groupID[i];
			}
		}
		Log.e("SKY" , "--var-- :: " + var);
		Cursor pCur = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, 
                null,
                var,
                null, 
                null);

        int i =0;
        while (pCur.moveToNext()) {
        	i++;
			Log.e("SKY" , "aaa" + i );
        }
        pCur.close();
		

		
	}
	public void getSampleContactList(int groupID) {
		Log.e("SKY" , "--getSampleContactList-- :: " + groupID);
	    Uri groupURI = ContactsContract.Data.CONTENT_URI;
	    String[] projection = new String[] {
	            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
	            ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID };

	    Cursor c = getContentResolver().query(
	            groupURI,
	            projection,
	            ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
	                    + "=" + groupID, null, null);

	    int count_all = 0;
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
				SAVE_DB_Phone(name.replace(" ", ""), phone.replace("-", ""), ""+groupID);
				count_all++;
	        }
	        pCur.close();
	    }
	    SAVE_DB_GROUP_COUNT(""+count_all, groupID);
	    
	}
	
	public void SELECT_GROUP()		//디비 값 조회해서 저장하기
	{
		arrData.clear();
		try{
			//  db파일 읽어오기
			SQLiteDatabase db = openOrCreateDatabase("phonedb.db", Context.MODE_PRIVATE, null);
			// 쿼리로 db의 커서 획득
			Cursor cur = db.rawQuery("SELECT * FROM `group` where count is not 0;", null);
			// 처음 레코드로 이동
			while(cur.moveToNext()){

				// 읽은값 출력
				Log.i("MiniApp",cur.getString(0)+"/"+cur.getString(1)+"/"+cur.getString(2));
				arrData.add(new MyPhoneGroupObj(""+cur.getString(3), 
						cur.getString(1), 
						"", 
						"", 
						"",
						"", 
						""+cur.getString(2), 
						"false",
						0,
						0));
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
	public void DeleteTb()		//디비 값 조회해서 저장하기
	{
		try{
			MySQLiteOpenHelper vc = new MySQLiteOpenHelper(this);
			vc.copyDB(this);
			customProgressPop();
			AccumThread1 av = new AccumThread1();
			av.start();
		}
		catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ",se.toString());
		}   

	}
	public void SAVE_DB_Group(String name, String count , String group_key)
	{
		try{
			//인서트쿼리
			//			Toast.makeText(One.this, "즐겨찾기 등록완료!", 0).show();
			SQLiteDatabase db = openOrCreateDatabase("phonedb.db", Context.MODE_PRIVATE, null);
			String sql;
			try {

				sql = "INSERT INTO `group`(`name`,`count`,`group_id`) VALUES (";
				sql += "'" + name  + "'" ;
				sql += ",'" + count  + "'" ;
				sql += ",'" + group_key  + "'" ;
				sql +=   ")";
				Log.e("SKY","sql  : "+ sql);
				db.execSQL(sql);
			} catch (Exception e) {
				db.close();
				Log.e("SKY","sql error : "+ e.toString());
			}
			db.close();
		}catch (Exception e) {
			Log.e("SKY","onPostExecute error : "+ e.toString());
		}
	}

	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:	
				finish();
				break;
			case R.id.btn_server:	
				ServerSave();
				break;
			case R.id.btn_server1:	
				ServerSave();
				break;
			case R.id.btn_reflash:
				DeleteTb();
				break;
			case R.id.btn_reflash1:
				DeleteTb();
				break;
			case R.id.btn_ok:	
				customProgressPop();
				AccumThread2 av = new AccumThread2();
				av.start();
				break;
			case R.id.btn_ok1:	
				customProgressPop();
				AccumThread2 av1 = new AccumThread2();
				av1.start();
				break;
			}
		}
	};
	private void ServerSave(){
		//서버에 그룹 저장
		AlertDialog.Builder alert = new AlertDialog.Builder(LMSMyPhoneActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		alert.setTitle("알림");
		LinearLayout layout = new LinearLayout(LMSMyPhoneActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		final EditText name = new EditText(LMSMyPhoneActivity.this);
		name.setSingleLine(true);
		layout.setPadding(20, 0, 20, 0);
		name.setHint("그룹명을 입력해주세요.");
		layout.addView(name);
		alert.setView(layout);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				customProgressPop();
				String post_val = SendDataServer();
				Log.e("SKY" , "post_val :: " + post_val );
				
				//post 발송
				map.put("url", dataSet.SERVER+"Server_Insert.jsp");
				map.put("user_id",dataSet.PHONE);
				map.put("group_name",name.getText().toString());
				map.put("val",post_val);
				map.put("count",""+count_server);

				//post 발송
				mThread = new AccumThread(LMSMyPhoneActivity.this , mAfterAccum , map , 0 , 1 , null);
				mThread.start();		//스레드 시작!!
			}
		});
		alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		alert.show();
	}
	private String SendDataServer(){
		count_server = 0;
		JSONObject obj = new JSONObject();
		try {
			JSONArray jArray = new JSONArray();//배열이 필요할때
			ArrayList<MyPhoneListObj> arr = new ArrayList<MyPhoneListObj>();
			
			for (int i = 0; i < arrData.size(); i++)//배열
			{
				if (arrData.get(i).getSELECTED() == 1) {
					Log.e("SKY","get_ID :: " + arrData.get(i).get_ID());

					arr.clear();
					arr = SELECT_Phone("" +(Integer.parseInt(arrData.get(i).get_ID()) - 1));
					for (int j = 0; j < arr.size(); j++) {
						JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
						Log.e("SKY","NAME :: " + arr.get(j).getNAME());
						Log.e("SKY","PHONE :: " + arr.get(j).getPHONE());
						sObject.put("NAME", arr.get(j).getNAME());
						sObject.put("PHONE", arr.get(j).getPHONE());
						jArray.put(sObject);
						count_server++;
					}
					
					obj.put("data",jArray);
				}
			}
			Log.e("SKY","JSON DATA :: " + obj.toString());
			System.out.println(obj.toString());
			return obj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}
	public class AccumThread2 extends Thread{
		public AccumThread2(){
		}
		@Override
		public void run()
		{
			for (int i = 0; i < arrData.size(); i++) {
				if (arrData.get(i).getSELECTED() == 1) {
					int key = Integer.parseInt(arrData.get(i).get_ID());
					ArrayList<MyPhoneListObj> vo = SELECT_Phone(""+(key-1));
					for (int j = 0; j < vo.size(); j++) {
						dataSet.arrData_real.add(new MyPhoneListObj2(0,
								vo.get(j).getNAME(),
								vo.get(j).getPHONE(),
								vo.get(j).getCHECK(),
								Integer.parseInt(arrData.get(i).get_ID())));
					}
				}
			}
			Message msg2 = mAfterAccum.obtainMessage();
			msg2.arg1 = 200;
			mAfterAccum.sendMessage(msg2);
		}
	}
	public ArrayList<MyPhoneListObj> SELECT_Phone(String key)		//디비 값 조회해서 저장하기
	{
		ArrayList<MyPhoneListObj> arr = new ArrayList<MyPhoneListObj>();
		try{
			//  db파일 읽어오기
			SQLiteDatabase db = openOrCreateDatabase("phonedb.db", Context.MODE_PRIVATE, null);
			// 쿼리로 db의 커서 획득
			Cursor cur = db.rawQuery("SELECT * FROM `phone` where group_key = '" + key + "';", null);
			// 처음 레코드로 이동
			while(cur.moveToNext()){
				// 읽은값 출력
				Log.i("MiniApp",cur.getString(0)+"/"+cur.getString(1)+"/"+cur.getString(2));
				arr.add(new MyPhoneListObj(cur.getString(1), cur.getString(2), 0 ,0));
			}
			cur.close();
			db.close();

		}
		catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ",se.toString());
		}   
		return arr;

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
			}else if (msg.arg1  == 1 ) {
				//서버 저장
				String res = (String)msg.obj;
				Log.e("SKY" , "RESULT  -> " + res);
				customProgressClose();
			}else if (msg.arg1  == 100 ) {
				//조회 끝 
				Log.e("SKY" , "조회 끝 !");
				customProgressClose();
				SELECT_GROUP();
				Check_Preferences.setAppPreferences(LMSMyPhoneActivity.this, "phonedb", true);
			}else if(msg.arg1  == 200 ){
				LMSMainActivity.onresume_0 = 1;
				customProgressClose();
				finish();
			}else if(msg.arg1  == 5000 ){//전체선택 
				for (int i = 0; i < arrData.size(); i++) {
					arrData.get(i).setSELECTED(1);
				}
				m_Adapter.notifyDataSetChanged();
			}else if(msg.arg1  == 6000 ){//전체선택 해제
				for (int i = 0; i < arrData.size(); i++) {
					arrData.get(i).setSELECTED(0);
				}
				m_Adapter.notifyDataSetChanged();
				LMSMyPhoneActivity.check_count.setText("0");

			}else if(msg.arg1  == 7000 ){
				check_all.setEnabled(true);//활성화
				check_all.setChecked(true);
				int count = 0;
				for (int i = 0; i < arrData.size(); i++) {
					count += Integer.parseInt(arrData.get(i).getGROUP_COUNT());
				}
				check_count.setText("" + count);
			}else if(msg.arg1  == 8000 ){
				check_all.setEnabled(true);//  ??
				check_all.setChecked(false);
			}else if(msg.arg1  == 9000 ){
				check_all.setEnabled(true); //모두 선택
				check_all.setChecked(false);
			}

		}
	};
	public void SAVE_DB_GROUP_COUNT(String count , int key)
	{
		Log.e("SKY","count  : "+ count);
		Log.e("SKY","key  : "+ key);
		try{
			//인서트쿼리
			SQLiteDatabase db = openOrCreateDatabase("phonedb.db", Context.MODE_PRIVATE, null);
			String sql;
			try {
				sql = "UPDATE `group` SET count = '" + count + "' WHERE group_id='" +  (key)+"'";
				Log.e("SKY","sql2  : "+ sql);
				db.execSQL(sql);
			} catch (Exception e) {
				db.close();
				Log.e("MiniApp","sql error : "+ e.toString());
			}
			db.close();
		}catch (Exception e) {
			Log.e("SKY","onPostExecute error : "+ e.toString());
		}
	}
	public void SAVE_DB_Phone(String name, String phone, String group_key)
	{
		try{
			//인서트쿼리
			SQLiteDatabase db = openOrCreateDatabase("phonedb.db", Context.MODE_PRIVATE, null);
			String sql;
			try {
				sql = "INSERT INTO  phone";
				sql += " VALUES(";
				sql += "" + "NULL"  + "" ;
				sql += ",'" + name  + "'" ;
				sql += ",'" + phone  + "'" ;
				sql += ",'" + group_key  + "'" ;
				sql +=   ")";
				Log.e("SKY","sql  : "+ sql);
				db.execSQL(sql);
			} catch (Exception e) {
				db.close();
				Log.e("MiniApp","sql error : "+ e.toString());
			}
			db.close();
		}catch (Exception e) {
			Log.e("SKY","onPostExecute error : "+ e.toString());
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
			Intent board = new Intent(LMSMyPhoneActivity.this, LMSMyPhoneDetailActivity.class);
			board.putExtra("Object", arrData.get(position));
			startActivity(board);

		}
	};
	
	public void customProgressPop(){
		try{
			if (customDialog==null){
				customDialog = new ProgressDialog( this );
			}
			customDialog.setCancelable(false);
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
