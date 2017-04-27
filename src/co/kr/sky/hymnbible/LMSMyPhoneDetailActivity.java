package co.kr.sky.hymnbible;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import co.kr.sky.hymnbible.adapter.LMSMyPhoneList_Adapter;
import co.kr.sky.hymnbible.common.Check_Preferences;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.MyPhoneGroupObj;
import co.kr.sky.hymnbible.obj.MyPhoneListObj;
import co.kr.sky.hymnbible.obj.MyPhoneListObj2;

public class LMSMyPhoneDetailActivity extends Activity implements OnEditorActionListener{
	LMSMyPhoneList_Adapter           m_Adapter;
	public static ArrayList<MyPhoneListObj> arrData = new ArrayList<MyPhoneListObj>();
	ListView                list_number;
	protected ProgressDialog customDialog = null;

	MyPhoneGroupObj obj;
	CommonUtil dataSet = CommonUtil.getInstance();
	ArrayList<MyPhoneListObj> arrData_copy = new ArrayList<MyPhoneListObj>();
	private Typeface ttf;
	CheckBox check_all;
	private TextView font_1 , font_2 , font_3 , font_4 , t_count , t_name; 

	private Button btn_ok;
	EditText e_lms;
	public static Boolean search_flag = false;
	int key;
	int all_count = 0;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myphonedetail);
		
		ttf = Typeface.createFromAsset(getAssets(), "HANYGO230.TTF");

		list_number = (ListView)findViewById(R.id.list_number);
		e_lms = (EditText)findViewById(R.id.e_lms);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		check_all = (CheckBox)findViewById(R.id.check_all);
		t_name = (TextView)findViewById(R.id.t_name);

		
		
		btn_ok.setTypeface(ttf);
		e_lms.setTypeface(ttf);
		t_name.setTypeface(ttf);

		
		e_lms.setOnEditorActionListener(this); //mEditText와 onEditorActionListener를 연결

		
		
		
		Bundle bundle = getIntent().getExtras();
		obj = bundle.getParcelable("Object");
		Log.e("SKY", "ID :: " + obj.get_ID());

		//디비 조회해서 값 뿌려주면 끝!!
		findViewById(R.id.btn_back).setOnClickListener(btnListener);
		findViewById(R.id.btn_ok).setOnClickListener(btnListener);
		findViewById(R.id.btn_sp2).setOnClickListener(btnListener);
		findViewById(R.id.btn_sp3).setOnClickListener(btnListener);

		m_Adapter = new LMSMyPhoneList_Adapter( this , arrData , mAfterAccum);
		list_number.setOnItemClickListener(mItemClickListener);
		list_number.setAdapter(m_Adapter);

		key = Integer.parseInt(obj.get_ID());
		SELECT_Phone(""+(key));
		
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
	@Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // TODO Auto-generated method stub
        if(v.getId()==R.id.e_lms && actionId==EditorInfo.IME_ACTION_SEARCH){ 
        	// 뷰의 id를 식별, 키보드의 완료 키 입력 검출
        	if (e_lms.getText().toString().length() ==0) {
				Log.e("SKY","모두 보여주기");
				//모두 보여주기
				search_flag = false;
				for (int i = 0; i < arrData_copy.size(); i++) {
					if (arrData_copy.get(i).getCHECK() == 1) {
						Log.e("SKY","POSITION :: "  + arrData_copy.get(i).getCopy_position());
						arrData.set(arrData_copy.get(i).getCopy_position(), new MyPhoneListObj(arrData_copy.get(i).getKey(),arrData_copy.get(i).getNAME(), arrData_copy.get(i).getPHONE(), arrData_copy.get(i).getCHECK(),0));
					}
				}
				m_Adapter = new LMSMyPhoneList_Adapter( LMSMyPhoneDetailActivity.this , arrData , mAfterAccum);
				list_number.setAdapter(m_Adapter);
			}else {
				arrData_copy.clear();
				for (int i = 0; i < arrData.size(); i++) {
					if (arrData.get(i).getNAME().matches(".*" + e_lms.getText().toString() +".*") || arrData.get(i).getPHONE().matches(".*" + e_lms.getText().toString() +".*")) {
						Log.e("SKY", "같은 값! :: " + i);
						search_flag = true;
						arrData_copy.add(new MyPhoneListObj(arrData.get(i).getKey(),arrData.get(i).getNAME(), arrData.get(i).getPHONE(), arrData.get(i).getCHECK() , i));
					}
				}
				m_Adapter = new LMSMyPhoneList_Adapter( LMSMyPhoneDetailActivity.this , arrData_copy , mAfterAccum);
				list_number.setAdapter(m_Adapter);
			}
        }
        return false;
    }
	public void SELECT_Phone(String key)		//디비 값 조회해서 저장하기
	{
		arrData.clear();
		try{
			//  db파일 읽어오기
			SQLiteDatabase db = openOrCreateDatabase("phonedb.db", Context.MODE_PRIVATE, null);
			// 쿼리로 db의 커서 획득
			Cursor cur = db.rawQuery("SELECT * FROM `phone` where group_key = '" + key + "';", null);
			// 처음 레코드로 이동
			while(cur.moveToNext()){
				// 읽은값 출력
				all_count++;
				Log.i("MiniApp",cur.getString(0)+"/"+cur.getString(1)+"/"+cur.getString(2));
				arrData.add(new MyPhoneListObj(cur.getString(0),cur.getString(1), cur.getString(2), 0 ,0));
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
			case R.id.btn_sp2:	
				if (e_lms.getText().toString().length() ==0) {
					Log.e("SKY","모두 보여주기");
					//모두 보여주기
					search_flag = false;
					for (int i = 0; i < arrData_copy.size(); i++) {
						if (arrData_copy.get(i).getCHECK() == 1) {
							Log.e("SKY","POSITION :: "  + arrData_copy.get(i).getCopy_position());
							arrData.set(arrData_copy.get(i).getCopy_position(), new MyPhoneListObj(arrData_copy.get(i).getKey(),arrData_copy.get(i).getNAME(), arrData_copy.get(i).getPHONE(), arrData_copy.get(i).getCHECK(),0));
						}
					}
					m_Adapter = new LMSMyPhoneList_Adapter( LMSMyPhoneDetailActivity.this , arrData , mAfterAccum);
					list_number.setAdapter(m_Adapter);
				}else {
					arrData_copy.clear();
					for (int i = 0; i < arrData.size(); i++) {
						if (arrData.get(i).getNAME().matches(".*" + e_lms.getText().toString() +".*") || arrData.get(i).getPHONE().matches(".*" + e_lms.getText().toString() +".*")) {
							Log.e("SKY", "같은 값! :: " + i);
							search_flag = true;
							arrData_copy.add(new MyPhoneListObj(arrData.get(i).getKey(),arrData.get(i).getNAME(), arrData.get(i).getPHONE(), arrData.get(i).getCHECK() , i));
						}
					}
					m_Adapter = new LMSMyPhoneList_Adapter( LMSMyPhoneDetailActivity.this , arrData_copy , mAfterAccum);
					list_number.setAdapter(m_Adapter);
				}
				break;
			case R.id.btn_sp3:	
				break;
			case R.id.btn_ok:
				customProgressPop();
				int j = 0;
				for (int i = 0; i < arrData.size(); i++) {
					if (arrData.get(i).getCHECK() == 1) {
						j++;
						dataSet.arrData_real.add(new MyPhoneListObj2(0 ,
								arrData.get(i).getNAME(),
								arrData.get(i).getPHONE(),
								arrData.get(i).getCHECK(),
								Integer.parseInt(obj.get_ID())));
					}
				}
				if (j == 0) {
					AlertDialog.Builder ab = new AlertDialog.Builder(LMSMyPhoneDetailActivity.this , AlertDialog.THEME_HOLO_LIGHT);
					//		.setTitle("부적결제 후 전화상담 서비스로 연결 되며 12시간 동안 재연결 무료 입니다.\n(운수대톡 )")
					ab.setMessage("1개 이상은 선택해야 합니다.");
					ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							return;
						}
					})
					.show();
					return;
				}
//				if (j > 499) {
//					//499개 보다 많으면.. 다음화면으로 못가게 맊음.!
//					AlertDialog.Builder ab = new AlertDialog.Builder(LMSMyPhoneDetailActivity.this , AlertDialog.THEME_HOLO_LIGHT);
//					//		.setTitle("부적결제 후 전화상담 서비스로 연결 되며 12시간 동안 재연결 무료 입니다.\n(운수대톡 )")
//					ab.setMessage("500개 이상 선택은 불가능합니다.");
//					ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int whichButton) {
//							return;
//						}
//					})
//					.show();
//				}else{
//				}
				LMSMainActivity.onresume_0 = 1;
				customProgressClose();
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
				//arrData.remove(Integer.parseInt(res));
				//m_Adapter.notifyDataSetChanged();
			} else if(msg.arg1  == 5000 ){//전체선택 
				for (int i = 0; i < arrData.size(); i++) {
					arrData.get(i).setCHECK(1);
				}
				m_Adapter.notifyDataSetChanged();
			}else if(msg.arg1  == 6000 ){//전체선택 해제
				for (int i = 0; i < arrData.size(); i++) {
					arrData.get(i).setCHECK(0);
				}
				m_Adapter.notifyDataSetChanged();

			}else if(msg.arg1  == 9001 ){//해당 삭제
				int del_position = (int)msg.arg2;
				DEL_Phone(del_position);
				SELECT_Phone(""+(key));
			}
			
			
		}
	};
	public void DEL_Phone(int del)		//디비 값 조회해서 저장하기
	{
		arrData.clear();
		try{
			//  db파일 읽어오기
			SQLiteDatabase db = openOrCreateDatabase("phonedb.db", Context.MODE_PRIVATE, null);
			// 쿼리로 db의 커서 획득

			String sql = "delete from `phone` where group_key = '" + key + "' AND key =" + del;
			Log.e("SKY","sql2  : "+ sql);
			db.execSQL(sql);
			db.close();
			all_count = all_count -1;
			UPDATE_GROUP_COUNT(all_count);
		}
		catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ",se.toString());
		}   

	}
	public void UPDATE_GROUP_COUNT(int count)
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
				all_count = 0;
			} catch (Exception e) {
				db.close();
				Log.e("MiniApp","sql error : "+ e.toString());
			}
			db.close();
		}catch (Exception e) {
			Log.e("SKY","onPostExecute error : "+ e.toString());
		}
	}
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView parent, View view, final int position,
				long id) {
			Log.e("SKY", "POSITION : " + position);
			/*
			if (!search_flag) {
				if (arrData.get(position).getCHECK() == 0) {
					arrData.get(position).setCHECK(1);
				}else{
					arrData.get(position).setCHECK(0);
				}
				m_Adapter.notifyDataSetChanged();
			}else{
				//검색시에 원본 데이터 셋팅! 
				Log.e("SKY", "else : " + arrData_copy.get(position).getCHECK());
				if (arrData_copy.get(position).getCHECK() == 0) {
					arrData_copy.get(position).setCHECK(1);
					int pp = arrData_copy.get(position).getCopy_position();
					Log.e("SKY", "1pp : " + pp);
					arrData.set(pp, new MyPhoneListObj(arrData.get(pp).getKey(),arrData.get(pp).getNAME(), arrData.get(pp).getPHONE(), 1,1));
				}else{
					arrData_copy.get(position).setCHECK(0);
					int pp = arrData_copy.get(position).getCopy_position();
					Log.e("SKY", "2pp : " + pp);
					arrData.set(pp, new MyPhoneListObj(arrData.get(pp).getKey(),arrData.get(pp).getNAME(), arrData.get(pp).getPHONE(), 0 ,0));
				}

				m_Adapter.notifyDataSetChanged();
			}
			*/
			//수정 팝업
			AlertDialog.Builder alert = new AlertDialog.Builder(LMSMyPhoneDetailActivity.this, AlertDialog.THEME_HOLO_LIGHT);
			alert.setTitle("알림");
			LinearLayout layout = new LinearLayout(LMSMyPhoneDetailActivity.this);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setGravity(Gravity.CENTER_HORIZONTAL);
			final EditText name = new EditText(LMSMyPhoneDetailActivity.this);
			final EditText phone = new EditText(LMSMyPhoneDetailActivity.this);
			name.setSingleLine(true);
			phone.setSingleLine(true);
			layout.setPadding(20, 0, 20, 0);
			name.setHint("추천인(휴대폰 번호)을 입력해주세요.");
			layout.addView(name);
			layout.addView(phone);
			
			name.setText(""+arrData.get(position).getNAME());
			phone.setText(""+arrData.get(position).getPHONE());
			
			alert.setView(layout);
			alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String user_phone = name.getText().toString();
					UPDATE_Phone(name.getText().toString(), phone.getText().toString(), arrData.get(position).getKey());
				}
			});
			alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Check_Preferences.setAppPreferences(LMSMyPhoneDetailActivity.this, "ch", "true");

				}
			});
			alert.show();
		}
	};
	public void UPDATE_Phone(String name , String phone , String key_index)
	{
		try{
			//인서트쿼리
			SQLiteDatabase db = openOrCreateDatabase("phonedb.db", Context.MODE_PRIVATE, null);
			String sql;
			try {
				sql = "UPDATE `phone` SET name = '" + name + "' , phone = '" + phone + "' WHERE key=" +  key_index + "";
				Log.e("SKY","sql2  : "+ sql);
				db.execSQL(sql);
				all_count = 0;
			} catch (Exception e) {
				db.close();
				Log.e("MiniApp","sql error : "+ e.toString());
			}
			db.close();
			SELECT_Phone(""+(key));
		}catch (Exception e) {
			Log.e("SKY","onPostExecute error : "+ e.toString());
		}
	}
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
