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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;
import co.kr.sky.hymnbible.adapter.LMSMyPhoneList_Adapter;
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

		int key = Integer.parseInt(obj.get_ID());
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
						arrData.set(arrData_copy.get(i).getCopy_position(), new MyPhoneListObj(arrData_copy.get(i).getNAME(), arrData_copy.get(i).getPHONE(), arrData_copy.get(i).getCHECK(),0));
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
						arrData_copy.add(new MyPhoneListObj(arrData.get(i).getNAME(), arrData.get(i).getPHONE(), arrData.get(i).getCHECK() , i));
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
				Log.i("MiniApp",cur.getString(0)+"/"+cur.getString(1)+"/"+cur.getString(2));
				arrData.add(new MyPhoneListObj(cur.getString(1), cur.getString(2), 0 ,0));
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
							arrData.set(arrData_copy.get(i).getCopy_position(), new MyPhoneListObj(arrData_copy.get(i).getNAME(), arrData_copy.get(i).getPHONE(), arrData_copy.get(i).getCHECK(),0));
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
							arrData_copy.add(new MyPhoneListObj(arrData.get(i).getNAME(), arrData.get(i).getPHONE(), arrData.get(i).getCHECK() , i));
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

			}
		}
	};
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			Log.e("SKY", "POSITION : " + position);
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
					arrData.set(pp, new MyPhoneListObj(arrData.get(pp).getNAME(), arrData.get(pp).getPHONE(), 1,1));
				}else{
					arrData_copy.get(position).setCHECK(0);
					int pp = arrData_copy.get(position).getCopy_position();
					Log.e("SKY", "2pp : " + pp);
					arrData.set(pp, new MyPhoneListObj(arrData.get(pp).getNAME(), arrData.get(pp).getPHONE(), 0 ,0));
				}

				m_Adapter.notifyDataSetChanged();
			}
		}
	};
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
