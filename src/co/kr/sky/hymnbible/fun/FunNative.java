package co.kr.sky.hymnbible.fun;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;
import co.kr.sky.AccumThread;
import co.kr.sky.hymnbible.ChurchSearch;
import co.kr.sky.hymnbible.ImageViewActivity;
import co.kr.sky.hymnbible.LMSMainActivity;
import co.kr.sky.hymnbible.MainActivity;
import co.kr.sky.hymnbible.common.Check_Preferences;

public class FunNative  {

	CommonUtil dataSet = CommonUtil.getInstance();
	private TextToSpeech myTTS;
	static Map<String, String> map = new HashMap<String, String>();
	AccumThread mThread;

	private WebView Webview_copy;

	/*  추천인 입력
	 * param 
	 * url :: 안씀 
	 * str :: 안씀
	 * return :: 안씀 
	 * window.location.href = "js2ios://AppVersion?url=안씀&str=안씀&return=안씀";
	 * */
	public void AppVersion(String url , final Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "-AppVersion-- :: ");
		//팝업으로 앱버전 띄우기(개발사 , 앱 버전 정보)
		AlertDialog.Builder alert = new AlertDialog.Builder(ac, AlertDialog.THEME_HOLO_LIGHT);
		alert.setTitle("개발 및 버전 정보");
		alert.setMessage("dd");
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		alert.show();

	}
	
	
	/*  추천인 입력
	 * param 
	 * url :: 안씀 
	 * str :: 안씀
	 * return :: 안씀 
	 * window.location.href = "js2ios://GetPhone?url=안씀&str=안씀&return=리턴함수";
	 * */
	public void GetPhone(String url , final Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "-GetPhone-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		vc.loadUrl("javascript:"+return_fun + "('" +dataSet.PHONE + "')");

	}
	
	/*  추천인 입력
	 * param 
	 * url :: 안씀 
	 * str :: 안씀
	 * return :: 안씀 
	 * window.location.href = "js2ios://InputAlert?url=안씀&str=안씀&return=안씀";
	 * */
	public void InputAlert(String url , final Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "-InputAlert-- :: ");
		AlertDialog.Builder alert = new AlertDialog.Builder(ac, AlertDialog.THEME_HOLO_LIGHT);
		alert.setTitle("알림");
		final EditText name = new EditText(ac);
		name.setHint("추천인(휴대폰 번호)을 입력해주세요.");
		alert.setView(name);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String user_phone = name.getText().toString();
				Check_Preferences.setAppPreferences(ac, "ch", "true");

				//post 발송
				map.put("url", dataSet.SERVER+"recommender-proc.do");
				map.put("my_id",dataSet.PHONE);
				map.put("user_id",user_phone);
				mThread = new AccumThread(ac , mAfterAccum , map , 0 , 0 , null);
				mThread.start();		//스레드 시작!!

			}
		});
		alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		alert.show();
	}
	Handler mAfterAccum = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.arg1  == 0 ) {
				String res = (String)msg.obj;
				Log.e("CHECK" , "RESULT  -> " + res);
			}
		}
	};
	/*
	 * param 
	 * url :: 안씀 
	 * str :: 안씀
	 * return :: 안씀 
	 * window.location.href = "js2ios://LMSMainActivity?url=안씀&str=안씀&return=안씀";
	 * */
	public void LMSMainActivity(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "-LMSMainActivity-- :: ");
		Intent intent = new Intent(ac , LMSMainActivity.class);
		ac.startActivity(intent);
	}


	/*
	 * param 
	 * url :: 안씀 
	 * str :: 안씀
	 * return :: 안씀 
	 * window.location.href = "js2ios://ChurchSearch?url=안씀&str=안씀&return=안씀";
	 * */
	public void ChurchSearch(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "-ChurchSearch-- :: ");
		Intent intent = new Intent(ac , ChurchSearch.class);
		ac.startActivity(intent);
	}


	/*
	 * param 
	 * url :: val(on or off) 
	 * str :: string : 1.PUSH(푸시 on or off)  / 2.PUSH SOUND(사운드 on or off) / 3.PUSH 진동으로 알림(진동 on or off)
	 * return :: 리턴 함수에 string(on or off) 반환 
	 * window.location.href = "js2ios://SetPush?url=on or off&str=url 주소&return=안씀";
	 * */
	public void SetPush(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "-SetPush-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		Check_Preferences.setAppPreferences(ac, val[1] , val[0]);
		Log.e("SKY" , "SetPush -> RETURN :: " + "javascript:"+return_fun + "('" + val[1] + "','" + Check_Preferences.getAppPreferenceson(ac, val[1]) +  "')");
		vc.loadUrl("javascript:"+return_fun + "('" + val[1] + "','" + Check_Preferences.getAppPreferenceson(ac, val[1]) +  "')");
	}
	/*
	 * param 
	 * url :: 안씀 
	 * str :: string : 1.PUSH(푸시 on or off)  / 2.PUSH SOUND(사운드 on or off) / 3.PUSH 진동으로 알림(진동 on or off)
	 * return :: 리턴 함수에 string(on or off) 반환 
	 * window.location.href = "js2ios://GetPush?url=안씀&str=url 주소&return=안씀";
	 * */
	public void GetPush(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "-GetPushSwitch-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		if (val[1].equals("ALL")) {
			String a1 = Check_Preferences.getAppPreferenceson(ac, "PUSH");
			String a2 = Check_Preferences.getAppPreferenceson(ac, "PUSHSOUND");
			String a3 = Check_Preferences.getAppPreferenceson(ac, "PUSHVALIT");
			Log.e("SKY" , "javascript:"+return_fun + "('" + a1 + "','" + a2 + "','" + a3 +"')");
			vc.loadUrl("javascript:"+return_fun + "('" + a1 + "','" + a2 + "','" + a3 +"')");

		}else{
			String return_str = Check_Preferences.getAppPreferenceson(ac, val[1]);
			if (return_str.equals("")) { 	return_str = "off";		}
			Log.e("SKY" , "javascript:"+return_fun + "('" + return_str + "')");
			vc.loadUrl("javascript:"+return_fun + "('" + return_str + "')");
		}



	}
	/*
	 * param 
	 * url :: 안씀 
	 * str :: url 주소
	 * return :: 안씀 
	 * window.location.href = "js2ios://ImageView?url=안씀&str=url 주소&return=안씀";
	 * */
	public void ImageView(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "-이미지뷰-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		Intent intent = new Intent(ac , ImageViewActivity.class);
		intent.putExtra("url", val[1]);
		ac.startActivity(intent);
	}


	/*
	 * param 
	 * url :: 안씀 
	 * str :: 안씀
	 * return :: 재생후 리턴 함수에 string(sst 로 변환한 string) 반환 
	 * window.location.href = "js2ios://SearchSst?url=not&str=not&return=리턴함수(val:sst로 변환한 값)";
	 * */
	public void SearchSst(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "--SearchSst-- :: ");
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speech to Text");
		intent.putExtra("return_fun", return_fun);
		MainActivity.return_fun = return_fun;
		try {
			ac.startActivityForResult(intent, 999);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(ac,"다시 시도해주세요.",
					Toast.LENGTH_SHORT).show();
		}

		//		Intent intent = new Intent(ac , ImageViewActivity.class);
		//		intent.putExtra("url","http://cfile25.uf.tistory.com/image/2755D13555387B793293DC");
		//		ac.startActivity(intent);

		//MainActivity.myTTS.stop();
		//vc.loadUrl("javascript:"+return_fun + "('false')");
	}


	/*
	 * param 
	 * url :: 안씀 
	 * str :: 성경 구문
	 * return :: 재생후 리턴 함수에 반환 없음 
	 * window.location.href = "js2ios://Share?url=not&str=not&return=not";
	 * */
	public void Share(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "--공유하기-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}

		Intent msg = new Intent(Intent.ACTION_SEND);
		msg.addCategory(Intent.CATEGORY_DEFAULT);
		msg.putExtra(Intent.EXTRA_SUBJECT, "-성경 찬송-");
		msg.putExtra(Intent.EXTRA_TEXT, val[1]);
		//		msg.putExtra(Intent.EXTRA_TITLE, "제목");
		msg.setType("text/plain");    
		ac.startActivity(Intent.createChooser(msg, "공유"));

		//MainActivity.myTTS.stop();
		//vc.loadUrl("javascript:"+return_fun + "('false')");
	}
	/*
	 * param 
	 * url :: 안씀 
	 * str :: 안씀
	 * return :: 재생후 리턴 함수에 false 반환 
	 * window.location.href = "js2ios://TTS_Stop?url=not&str=not&return=not";
	 * */
	public void TTS_Stop(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "--TTS_Stop-- :: ");
		MainActivity.myTTS.stop();


		vc.loadUrl("javascript:"+return_fun + "('false')");

	}
	/*
	 * param 
	 * url :: 안씀 
	 * str :: tts 문구
	 * return :: 재생후 리턴 함수에 true 반환 
	 * window.location.href = "js2ios://TTS_Start?url=not&str=tts 문구 스트&return=리턴함수(재생중:true)";
	 * */
	public void TTS_Start(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "--TTS_Start-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		String word = val[val.length-1];
		try {
			word = new String(word.getBytes("x-windows-949"), "ksc5601");
			word = word.replace("0", "").replace("1", "").replace("2", "").replace("3", "").replace("4", "")
					.replace("5", "").replace("6", "").replace("7", "").replace("8", "").replace("9", "").replace(".", "");


			MainActivity.myTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null);
			Log.e("SKY" , "TTS 성공");
			vc.loadUrl("javascript:"+return_fun + "('true')");

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Log.e("SKY" , "TTS 실패");
			vc.loadUrl("javascript:"+return_fun + "('false')");

			e.printStackTrace();
		}

	}
	/*
	 * param 
	 * url :: 안씀 
	 * name :: 다운받은 db 파일명(파일명은 ftp와 동일함)
	 * return :: file 이 있으면 true  , 없으면 false  (인자 하나 받음)
	 * window.location.href = "js2ios://CheckDB?url=not&db=DB파일명(예:bible_kr.db)&return=리턴함수";
	 * */
	public void CheckDB(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "--CheckDB-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}

		String file_path = dataSet.Local_Path + val[1].split("/")[0];
		String file_path2 = dataSet.Local_Path + val[1].split("/")[1];

		File file_eng = new File(file_path);
		File file_cha = new File(file_path2);

		/*
		if( file.exists() ){  // 원하는 경로에 폴더가 있는지 확인
			Log.e("SKY" , "파일 있음11");
			//vc.loadUrl("javascript:"+return_fun + "('true')");
			return;
		}else{
			Log.e("SKY" , "파일 있음22");
			//vc.loadUrl("javascript:"+return_fun + "('false')");
			return;
		}
		 */
		vc.loadUrl("javascript:"+return_fun + "('"+file_eng.exists()+"', '"+file_cha.exists()+"')");
		return;
	}
	/*
	 * param 
	 * url :: 안씀 
	 * name :: 복사할 문구
	 * return :: 안씀
	 * window.location.href = "js2ios://ClipboardCopy?url=not&name=문구&return=not";
	 * */
	@SuppressLint("ShowToast")
	@SuppressWarnings("deprecation")
	public void ClipboardCopy(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "--ClipboardCopy-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		ClipboardManager clipboardManager = (ClipboardManager) ac.getSystemService(Context.CLIPBOARD_SERVICE);
		String word = val[val.length-1];
		try {
			word = new String(word.getBytes("x-windows-949"), "ksc5601");
			clipboardManager.setText(word);
			//			MainActivity.myTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null);
			//			MainActivity.myTTS.stop();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(ac, "복사되었습니다.", 0).show();
	}
	/*
	 * param 
	 * url :: 안씀 
	 * txt :: 공유하기 문구
	 * return :: 안씀
	 * window.location.href = "js2ios://ShareStr?url=not&txt=문구&return=not";
	 * */
	public void ShareStr(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "--ShareStr-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		Intent msg = new Intent(Intent.ACTION_SEND);
		msg.addCategory(Intent.CATEGORY_DEFAULT);
		//msg.putExtra(Intent.EXTRA_SUBJECT, "주제");
		//msg.putExtra(Intent.EXTRA_TITLE, "제목");
		msg.putExtra(Intent.EXTRA_TEXT, val[val.length]);
		msg.setType("text/plain");    
		ac.startActivity(Intent.createChooser(msg, "공유"));
	}
	/*
	 * param 
	 * url :: 다운경로 
	 * name :: 한글 & 중국 기타 등
	 * return :: 리턴 함수
	 * window.location.href = "js2ios://DBSQL?db=bible_kr.db&sql=select * from krv&return=retrundown";
	 * */
	public void DBSQL(String url , Activity ac , WebView vc , String return_fun){
		Log.e("SKY" , "--DBSQL-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}

		//파일 있는지 유무 체크
		String file_path = dataSet.Local_Path + val[0]; 
		File file = new File(file_path); 
		if( !file.exists() ){  
			Log.e("SKY" , "파일 없음");
			vc.loadUrl("javascript:"+return_fun + "('false')");
			return;
		}
		//		Log.e("SKY" , "javascript:"+return_fun + "()");
		new QuerySelAsync(ac  , vc, return_fun).execute(val[0] , val[1] ,val[val.length - 1]);

		//vc.loadUrl("javascript:"+return_fun + "("+SELECT_DB(val[0] , val[1] , ac) + ",'"+ val[val.length - 1] + "')");


	}
	public class QuerySelAsync extends AsyncTask<String, String, String> {

		private ProgressDialog mDlg;
		private Context mContext;
		private WebView Webview_copy;
		private String return_fun;

		public QuerySelAsync(Context context , WebView vc , String return_fun_) {
			mContext = context;
			Webview_copy = vc;
			return_fun = return_fun_;
		}

		@Override
		protected void onPreExecute() {
			//mDlg = new ProgressDialog(mContext,AlertDialog.THEME_HOLO_LIGHT);
			//mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			//mDlg.setMessage("DATA Loding..");
			//mDlg.show();
			//mDlg = ProgressDialog.show(mContext,"", "잠시만 기다려 주세요.",true);

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			JSONArray jArr = new JSONArray();
			try{
				//  db파일 읽어오기
				SQLiteDatabase db = mContext.openOrCreateDatabase(params[0], Context.MODE_PRIVATE, null);
				// 쿼리로 db의 커서 획득
				Cursor cur = db.rawQuery(params[1]+";", null);
				// 처음 레코드로 이동
				int i = 0;
				while(cur.moveToNext()){
					// 읽은값 출력
					JSONObject jsonObj = new JSONObject();
					//Log.e("SKY",cur.getString(0)+"/"+cur.getString(1)+"/"+cur.getString(2)
					//+"/"+cur.getString(3)+"/"+cur.getString(4));
					try {
						if (i == 0) {
							Log.e("SKY" , "BOOK :: " + cur.getString(2)  + "//JANG :: " + cur.getString(3));
							Check_Preferences.setAppPreferences(mContext, "book", cur.getString(2));
							Check_Preferences.setAppPreferences(mContext, "jang", cur.getString(3));
						}
						jsonObj.put("col_1", cur.getString(0));
						jsonObj.put("col_2", cur.getString(1));
						jsonObj.put("col_3", cur.getString(2));//
						jsonObj.put("col_4", cur.getString(3));//
						jsonObj.put("col_5", cur.getString(4));
						jsonObj.put("col_6", cur.getString(5));
						jArr.put(jsonObj);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					i++;
				}
				cur.close();
				db.close();
				String sendMsg = jArr.toString();
				Log.e("SKY" , "params2 :: " + params[2]);

				return "javascript:"+return_fun + "("+ sendMsg + ",'"+ params[2] + "')";
				//				Webview_copy.loadUrl("javascript:"+return_fun + "("+ sendMsg + ",'"+ params[2] + "')");
			}
			catch (SQLException se) {
				// TODO: handle exception
				Log.e("selectData()Error! : ","selectData + " +se.toString());
			}   
			return "";

		}

		@Override
		protected void onProgressUpdate(String... progress) {
			//Log.e("SKY" , "progress size :: " + progress.length);
			//Log.e("SKY" , "progress 0 :: " + progress[0]);
			//mDlg.setProgress(Integer.parseInt(progress[0]));
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(final String return_fun) {
			Log.e("SKY" , "return_fun :: " + return_fun);
			Webview_copy.loadUrl(return_fun);
			Log.e("SKY" , "다운로드 완료");
			/*
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					//여기에 딜레이 후 시작할 작업들을 입력
					//mDlg.dismiss();
					Webview_copy.loadUrl(return_fun);
					Log.e("SKY" , "다운로드 완료");

				}
			}, 500);// 0.5초 정도 딜레이를 준 후 시작
			 */
		}
	}
	private String SELECT_DB(String db_name ,String sql ,  Context ct)		//디비 값 조회해서 저장하기
	{
		JSONArray jArr = new JSONArray();
		try{
			//  db파일 읽어오기
			SQLiteDatabase db = ct.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
			// 쿼리로 db의 커서 획득
			Cursor cur = db.rawQuery(sql+";", null);
			// 처음 레코드로 이동
			int i = 0;
			while(cur.moveToNext()){
				// 읽은값 출력
				JSONObject jsonObj = new JSONObject();
				//Log.e("SKY",cur.getString(0)+"/"+cur.getString(1)+"/"+cur.getString(2)
				//+"/"+cur.getString(3)+"/"+cur.getString(4));
				try {
					if (i == 0) {
						Log.e("SKY" , "BOOK :: " + cur.getString(2)  + "//JANG :: " + cur.getString(3));
						Check_Preferences.setAppPreferences(ct, "book", cur.getString(2));
						Check_Preferences.setAppPreferences(ct, "jang", cur.getString(3));
					}
					jsonObj.put("col_1", cur.getString(0));
					jsonObj.put("col_2", cur.getString(1));
					jsonObj.put("col_3", cur.getString(2));//
					jsonObj.put("col_4", cur.getString(3));//
					jsonObj.put("col_5", cur.getString(4));
					jsonObj.put("col_6", cur.getString(5));
					jArr.put(jsonObj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			cur.close();
			db.close();
		}
		catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ",se.toString());
		}   


		String sendMsg = jArr.toString();

		//Log.e("SKY","sendMsg :: " + sendMsg);
		return sendMsg;
	}

	/*
	 * param 
	 * url :: 다운경로 
	 * name :: 한글 & 중국 기타 등
	 * return :: 리턴 함수
	 * */
	public void DownLoadDB(String url , Activity ac , WebView vc , String return_fun){
		//		08-12 00:17:53.840: E/SKY(11517): url :: =112344ff,=2aaaaac,=3bbbbc
		Log.e("SKY" , "--DownLoadDB-- :: ");
		Webview_copy = vc;
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}

		String file_path = dataSet.Local_Path + val[0]; 
		File file = new File(file_path); 
		if( file.exists() ){  // 원하는 경로에 폴더가 있는지 확인
			Log.e("SKY" , "파일 있음");
			vc.loadUrl("javascript:"+return_fun + "('true')");
			return;
		}

		File file1 = new File(dataSet.Local_Path); 


		if (file1.exists()) {
		}else{
			file1.mkdirs();
		}

		Log.e("SKY" , "DownloadFileAsync");

		//download
		new DownloadFileAsync(ac , return_fun , vc).execute(val[0] , val[val.length-1] );

		//Log.e("SKY" , "javascript:"+return_fun + "()");
		//vc.loadUrl("javascript:"+return_fun + "("+  val[val.length] +")");

	}
	public class DownloadFileAsync extends AsyncTask<String, String, String> {

		private ProgressDialog mDlg;
		private Context mContext;
		private String  return_fun;
		private WebView Webview_copy;


		public DownloadFileAsync(Context context  , String str , WebView vc_) {
			mContext = context;
			return_fun = str;
			Webview_copy = vc_;
		}

		@Override
		protected void onPreExecute() {
			mDlg = new ProgressDialog(mContext,AlertDialog.THEME_HOLO_LIGHT);
			mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDlg.setMessage("DATA DownLoding..");
			mDlg.show();

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			int count = 0;
			try {
				String str = dataSet.SERVER_DB + params[0];
				Log.e("SKY","STR :: " + str);
				URL url = new URL(str);
				URLConnection conexion = url.openConnection();
				conexion.setRequestProperty("Accept-Encoding", "identity"); // <--- Add this line

				conexion.connect();

				int lenghtOfFile = conexion.getContentLength();
				Log.e("SKY", "Lenght of file: " + lenghtOfFile);

				//				String DEFAULT_FILE_PATH1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bible";
				String dirPath = dataSet.Local_Path;
				File file = new File(dirPath); 
				if( !file.exists() ){  // 원하는 경로에 폴더가 있는지 확인
					Log.e("SKY" , "폴더 생성");
					file.mkdirs();
				}
				String DEFAULT_FILE_PATH = dataSet.Local_Path + "/" + params[0];

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(DEFAULT_FILE_PATH);

				byte data[] = new byte[1024];

				long total = 0;


				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();

			} catch (IOException e) {
				e.printStackTrace();
				Log.e("SKY" , "FAIL :: " + e.toString());
			}

			return "javascript:"+return_fun + "("+  params[1] +")";
		}

		@Override
		protected void onProgressUpdate(String... progress) {
			//			Log.e("SKY" , "progress size :: " + progress.length);
			//			Log.e("SKY" , "progress 0 :: " + progress[0]);
			mDlg.setProgress(Integer.parseInt(progress[0]));
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String return_fun) {
			mDlg.dismiss();
			Log.e("SKY" , "return_fun::"+return_fun);
			Webview_copy.loadUrl(return_fun);
			Webview_copy.reload();
			//			Webview_copy.loadUrl("javascript:"+return_fun + "('true')");
			Log.e("SKY" , "다운로드 완료");
		}
	}
}
