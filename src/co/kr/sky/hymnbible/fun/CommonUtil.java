package co.kr.sky.hymnbible.fun;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.Activity;


//AIzaSyApdCPSYnLhdlNMqQGJ3sJnFwUqIkWgKpY
@SuppressLint("SdCardPath")
public class CommonUtil {
	private static CommonUtil _instance;
	public int EA;
	public int HomeBtn;
	
	public ArrayList<Activity> av = new ArrayList<Activity>();					//Activity 를 담는다.

	public String Local_Path;
	public String PHONE;
	public String SERVER;
	public String SERVER_IMG;
	public String SERVER_ADIMG;
	public String SERVER_E_IMG;
	public String SERVER_F_IMG;
	public String SERVER_FITIMG;
	public String SERVER_MP3;
	public String SERVER_DB;
	public String SERVER_MY_IMG;
	public String COSS_FOOD;
	
	
	public String LOGIN_MEMBER_GEUST;
	
	public ArrayList<Boolean> mp3_flag;
	public ArrayList<Boolean> mp3_detail_flag;
	
	public boolean Activity_flag = false;
	public boolean Agree_Check;
	public boolean EXIT;
	public String REG_ID;
	public String PROJECT_ID;
	public String CHOO;
	public String MY_EA;
	public String POINT;
	public String MY_IMG;

	
	public String KEY_INDEX;
	public String NAME;
	public String USER_ID;
	public String USER_PW;
	public String USER_EMAIL;
	public String USER_PHONE;
	public String BIBLE25_URL1;
	public String BIBLE25_URL2;
	public String BIBLE25_URL3;
	public String BIBLE25_URL4;

	static {
		_instance = new CommonUtil();
		try {								 
			_instance.PROJECT_ID = 	   		"560326302496";
			_instance.REG_ID = 	   		"";
			_instance.LOGIN_MEMBER_GEUST = 	   		"";
			_instance.MY_IMG = 	   		"";
			_instance.Local_Path = 	   		"/data/data/co.kr.sky.hymnbible/databases/";
//			_instance.Local_Path = 	   		Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Bible/";
			
			_instance.mp3_flag = 	   		new ArrayList<Boolean>();
			_instance.mp3_detail_flag = 	   		new ArrayList<Boolean>();
			_instance.KEY_INDEX = 	   		"";
			_instance.NAME = 	   			"";
			_instance.USER_ID = 	   		"";
			_instance.USER_PW = 	   		"";
			_instance.USER_EMAIL = 	   		"";
			_instance.USER_PHONE = 	   		"";
			_instance.POINT = 	   		"";

			_instance.EXIT = 	   		false;
			_instance.PHONE = 	   		"";
			_instance.SERVER = 	   		"http://shqrp5200.cafe24.com/";
			_instance.SERVER_DB = 	   		"http://hoon86.cafe24.com/db/";			//DB 파일 경로
			_instance.BIBLE25_URL1 = 	   		"http://ch2ho.bible25.com/m/bbs/board2.php?bo_table=bible&t=tab2&key=NKRV&key2=&key3=&";			//BIBLE25 url
			_instance.BIBLE25_URL2 = 	   		"http://ch2ho.bible25.com/m/bbs/board2.php?bo_table=bible&t=tab5&key=NKRV&key2=&key3=&";			//BIBLE25 url
			_instance.BIBLE25_URL3 = 	   		"http://ch2ho.bible25.com/m/bbs/board2.php?bo_table=bible&t=tab3&key=NKRV&key2=&key3=&";			//BIBLE25 url
			_instance.BIBLE25_URL4 = 	   		"http://ch2ho.bible25.com/m/bbs/board2.php?bo_table=bible&t=tab4&key=NKRV&key2=&key3=&";			//BIBLE25 url

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static CommonUtil getInstance() {
		return _instance;
	}

	
	public ArrayList<String> Token_String(String url , String token){
		ArrayList<String> Obj = new ArrayList<String>();

		StringTokenizer st1 = new StringTokenizer( url , token);
		while(st1.hasMoreTokens()){
			Obj.add(st1.nextToken());
		}
		return Obj;
	}
}
