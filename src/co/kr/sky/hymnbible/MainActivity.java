package co.kr.sky.hymnbible;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment.InstantiationException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import co.kr.sky.AccumThread;
import co.kr.sky.hymnbible.common.Check_Preferences;
import co.kr.sky.hymnbible.common.RealPathUtil;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.fun.MySQLiteOpenHelper;

public class MainActivity extends Activity implements OnInitListener{

	//업로드
	private static final String TYPE_IMAGE = "image/*";
	private static final int INPUT_FILE_REQUEST_CODE = 1;

	private ValueCallback<Uri[]> mFilePathCallback;
	private String mCameraPhotoPath;
	AccumThread mThread;



	private final static int FILECHOOSER_RESULTCODE = 1;

	public static WebView BibleWeb;
	public WebView BibleWeb_s = null;
	//String url = "http://hoon0319.cafe24.com/index.do";
	String url = "http://shqrp5200.cafe24.com/index.do?phone=";
	//	String url = "http://snap40.cafe24.com/Test/hy.html";
	MySQLiteOpenHelper vc;					//Data Base 복사 하기 위한 클래스! 
	CommonUtil dataSet = CommonUtil.getInstance();
	public static TextToSpeech myTTS;

	public String fix_url = "";
	LinearLayout bottomview;
	public static int mOriginalOrientation;
	public static CustomViewCallback mCustomViewCollback;
	String regId ;
	static Map<String, String> map = new HashMap<String, String>();
	String Before_URL , URL_NOW;
	public static String TTS_str = "";

	public static final int REQ_CODE_SPEECH_INPUT = 100;
	public static Intent i;
	SpeechRecognizer mRecognizer;

	public static String return_fun = "";

	private ValueCallback<Uri> mUploadMessage;
	private ValueCallback<Uri> filePathCallbackNormal;
	private ValueCallback<Uri[]> filePathCallbackLollipop;
	private final static int FILECHOOSER_NORMAL_REQ_CODE = 1;
	private final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 2;
	private final static int PICK_IMAGE_REQ_CODE = 3;
	@Override
	public void onResume(){
		super.onResume();
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//vc = new MySQLiteOpenHelper(this);
		bottomview = (LinearLayout)findViewById(R.id.bottomview);
		bottomview.setVisibility(View.GONE);

		//getGroup();
		/*
		//폰번호 알아오기
		Cursor cursor3 = getContentResolver().query(
				ContactsContract.Data.CONTENT_URI, 
				null, 
				ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=" + 5, 
				null, 
				null);

		try {
			while (cursor3.moveToNext()) {
				String id = cursor3.getString(cursor3.getColumnIndex(ContactsContract.Data._ID));
				showMember(id);
			}
		} finally {
			// TODO: handle finally clause
			cursor3.close();
		}
		*/

		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);// 사용자 전화번호로 ID값 가져옴
		try {
			dataSet.PHONE = telManager.getLine1Number().toString().trim().replace("+82", "0").replace("82", "0"); //폰번호를 가져옴
			Log.e("SKY" , "폰번호 :: " + dataSet.PHONE);
			//dataSet.PHONE = telManager.getDeviceId();
		} catch (Exception e) {
			// TODO: handle exception
			dataSet.PHONE = "01027065915";
			//confirmDialog("휴대폰 번호가 없는 기기는 가입할수 없습니다.");
			//return;
		}
		setting_web();
		setting_button();
		myTTS = new TextToSpeech(this, this);




		//push
		if(GCMIntentService.re_message!=null){
			Log.e("CHECK" , "PUSH DATA!!!----> " +GCMIntentService.re_message );
		}else{
			Log.e("CHECK" , "dataSet.PROJECT_ID11122!----> " +dataSet.PROJECT_ID );
			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);

			dataSet.REG_ID = GCMRegistrar.getRegistrationId(this);
			Log.e("reg_id11", dataSet.REG_ID);

			if (dataSet.REG_ID.equals("")) {
				Log.e("SKY", "in");
				GCMRegistrar.register(this, dataSet.PROJECT_ID);
			} else {
				Log.e("SKY", "푸시 등록 :: " + dataSet.REG_ID);
				map.put("url", dataSet.SERVER+"json/updateRegid.do");
				map.put("phone",dataSet.PHONE);
				map.put("reg_id",dataSet.REG_ID);
				map.put("type","android");
				mThread = new AccumThread(MainActivity.this , mAfterAccum , map , 0 , 0 , null);
				mThread.start();		//스레드 시작!!
			}
		}
		//최초 설치시 추천인 입력
		if("".equals(Check_Preferences.getAppPreferences(MainActivity.this, "ch"))){
			InputAlert();
		}
	}

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

				Log.e("SKY" , "_ID :: " + cursor.getInt(0));
				Log.e("SKY" , "TITLE :: " + cursor.getString(1));
				Log.e("SKY" , "ACCOUNT_NAME :: " + cursor.getString(2));
				Log.e("SKY" , "ACCOUNT_TYPE :: " + cursor.getString(3));
				Log.e("SKY" , "DELETED :: " + cursor.getString(4));
				Log.e("SKY" , "GROUP_VISIBLE :: " + cursor.getString(5));
				Log.e("SKY" , "GROUP_COUNT :: " + getGroupSummaryCount(cursor.getString(0)));
				
				getSampleContactList(cursor.getInt(0));
			}
		} finally {
			cursor.close();
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

	        }

	        pCur.close();
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

	public void confirmDialog(String message) {
		AlertDialog.Builder ab = new AlertDialog.Builder(this , AlertDialog.THEME_HOLO_LIGHT);
		//		.setTitle("부적결제 후 전화상담 서비스로 연결 되며 12시간 동안 재연결 무료 입니다.\n(운수대톡 )")
		ab.setMessage(message);
		ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
				return;
			}
		})
		.show();
	}
	private void InputAlert(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		alert.setTitle("알림");
		final EditText name = new EditText(this);
		name.setHint("추천인(휴대폰 번호)을 입력해주세요.");
		alert.setView(name);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String user_phone = name.getText().toString();
				Check_Preferences.setAppPreferences(MainActivity.this, "ch", "true");

				//post 발송
				//http://shqrp5200.cafe24.com/json/recommender-proc.do?my_id=01012341234&user_id=01043214321
				map.put("url", dataSet.SERVER+"json/recommender-proc.do");
				map.put("my_id",dataSet.PHONE);
				map.put("user_id",user_phone);
				mThread = new AccumThread(MainActivity.this , mAfterAccum , map , 0 , 0 , null);
				mThread.start();		//스레드 시작!!

			}
		});
		alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Check_Preferences.setAppPreferences(MainActivity.this, "ch", "true");
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
	@Override
	public void onInit(int str) {
		String myText1 =  TTS_str;
		//String myText2 = "말하는 스피치 입니다.";
		myTTS.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);
		//myTTS.speak(myText2, TextToSpeech.QUEUE_ADD, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myTTS.shutdown();
	}
	private void setting_button(){
		findViewById(R.id.b_bible1).setOnClickListener(btnListener);
		findViewById(R.id.b_bible2).setOnClickListener(btnListener);
		findViewById(R.id.b_bible3).setOnClickListener(btnListener);
		findViewById(R.id.b_bible4).setOnClickListener(btnListener);
		findViewById(R.id.b_bible5).setOnClickListener(btnListener);
		findViewById(R.id.b_bible6).setOnClickListener(btnListener);
		findViewById(R.id.btnsample).setOnClickListener(btnListener);

	}

	private RecognitionListener listener = new RecognitionListener() {

		@Override
		public void onReadyForSpeech(Bundle params) {
		}
		@Override
		public void onBeginningOfSpeech() {
		}

		@Override
		public void onRmsChanged(float rmsdB) {
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
		}

		@Override
		public void onEndOfSpeech() {
		}

		@Override
		public void onError(int error) {
		}

		@Override
		public void onResults(Bundle results) {
			String key= "";
			key = SpeechRecognizer.RESULTS_RECOGNITION;
			ArrayList<String> mResult = results.getStringArrayList(key);
			String[] rs = new String[mResult.size()];
			mResult.toArray(rs);
			mRecognizer.startListening(i);
			Log.e("SKY" , "STR :: " + rs[0]);
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
		}
	};
	/**
	 * Showing google speech input dialog
	 * */
	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"말하세요");
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),"말하세요2",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Receiving speech input
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("SKY" , "RESULT :: " + requestCode);
		switch (requestCode) {
		case 1:
			if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					if (mFilePathCallback == null) {
						super.onActivityResult(requestCode, resultCode, data);
						return;
					}
					Uri[] results = new Uri[]{getResultUri(data)};

					mFilePathCallback.onReceiveValue(results);
					mFilePathCallback = null;
				} else {
					if (mUploadMessage == null) {
						super.onActivityResult(requestCode, resultCode, data);
						return;
					}
					Uri result = getResultUri(data);

					Log.d(getClass().getName(), "openFileChooser : "+result);
					mUploadMessage.onReceiveValue(result);
					mUploadMessage = null;
				}
			} 
			break;
		case 999:
			if (resultCode == RESULT_OK && null != data) {
				
				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				Log.e("SKY" , "RESULT :: " + result.get(0));
				Log.e("SKY" , "return_fun :: " + return_fun);
				BibleWeb.loadUrl("javascript:"+return_fun + "('" + result.get(0) + "')");
			}
			break;
		case REQ_CODE_SPEECH_INPUT: {
			if (requestCode == FILECHOOSER_NORMAL_REQ_CODE) {
				if (filePathCallbackNormal == null) return ;
				Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
				filePathCallbackNormal.onReceiveValue(result);
				filePathCallbackNormal = null;
			} else if (requestCode == FILECHOOSER_LOLLIPOP_REQ_CODE) {
				if (filePathCallbackLollipop == null) return ;
				filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
				filePathCallbackLollipop = null;
			} else if (requestCode == PICK_IMAGE_REQ_CODE) {
				//            if (resultCode == Activity.RESULT_OK) {
				//                if (data != null) {
				//                    Uri uri = data.getData();
				//                    new AsyncTask<Uri, Void, String>() {
				//                        @Override
				//                        protected String doInBackground(Uri... params) {
				//                            String mimeType = getMimeType(params[0]);
				//                            File file = uriToFile(params[0]);
				//                            String base64EncodedImage = fileToString(file);
				//                            return "javascript:updateImage('" + mimeType + "', '" + base64EncodedImage + "');";
				//                        }
				//                        @Override
				//                        protected void onPostExecute(String result) {
				//                        	PremomWebview.loadUrl(result);
				//                        }
				//                    }.execute(uri);
				//                }
				//            }
			}
			break;
			
		}

		}
	}
	private Uri getResultUri(Intent data) {
		Uri result = null;
		if(data == null || TextUtils.isEmpty(data.getDataString())) {
			// If there is not data, then we may have taken a photo
			if(mCameraPhotoPath != null) {
				result = Uri.parse(mCameraPhotoPath);
			}
		} else {
			String filePath = "";
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				filePath = data.getDataString();
			} else {
				filePath = "file:" + RealPathUtil.getRealPath(this, data.getData());
			}
			result = Uri.parse(filePath);
		}

		return result;
	}

	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			String book = Check_Preferences.getAppPreferences(MainActivity.this, "book");
			String jang = Check_Preferences.getAppPreferences(MainActivity.this, "jang");
			switch (v.getId()) {

			case R.id.btnsample:	
				Log.e("SKY"  , "--btnsample--");
				//공유하기
				/*
					Intent msg = new Intent(Intent.ACTION_SEND);
					msg.addCategory(Intent.CATEGORY_DEFAULT);
					msg.putExtra(Intent.EXTRA_SUBJECT, "주제");
					msg.putExtra(Intent.EXTRA_TEXT, "내용");
					msg.putExtra(Intent.EXTRA_TITLE, "제목");
					msg.setType("text/plain");    
					startActivity(Intent.createChooser(msg, "공유"));
				 */
				//sst 기능
				//				promptSpeechInput();

				break;
			case R.id.b_bible1:	
				Log.e("SKY"  , "--b_bible1--");
				BibleWeb.loadUrl(Before_URL);
				break;
			case R.id.b_bible2:							//공유하기
				Log.e("SKY"  , "--b_bible2--");
				Before_URL = URL_NOW; 
				//book=1&jang=1&tab_title=해설
				BibleWeb.loadUrl(dataSet.BIBLE25_URL1 + "book=" + jang + "&jang=" + book + "&tab_title=해설");
				break;
			case R.id.b_bible3:	
				Log.e("SKY"  , "--b_bible3--");
				Before_URL = URL_NOW;
				BibleWeb.loadUrl(dataSet.BIBLE25_URL2 + "book=" + jang + "&jang=" + book + "&tab_title=핵심");
				break;
			case R.id.b_bible4:	
				Log.e("SKY"  , "--b_bible4--");
				Before_URL = URL_NOW;
				BibleWeb.loadUrl(dataSet.BIBLE25_URL3 + "book=" + jang + "&jang=" + book + "&tab_title=묵상");
				break;
			case R.id.b_bible5:	
				Log.e("SKY"  , "--b_bible5--");
				Before_URL = URL_NOW;
				BibleWeb.loadUrl(dataSet.BIBLE25_URL4 + "book=" + jang + "&jang=" + book + "&tab_title=Q&A");
				break;
			case R.id.b_bible6:	
				Log.e("SKY"  , "--b_bible6--");
				Before_URL = URL_NOW;
				break;

			}
		}
	};
	private void setting_web(){
		BibleWeb = (WebView)findViewById(R.id.web);
		BibleWeb.setWebViewClient(new ITGOWebChromeClient());
		BibleWeb.setWebChromeClient(new SMOWebChromeClient(this));
		//홍진:openWindow 사용
		//setWebViewOpenWindow();
		BibleWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//팝업(window.open) 권한
		BibleWeb.getSettings().setSupportMultipleWindows(true); //팝업을허용하고 setSupportMultipleWindows를 주지않으면 url이로딩 된다
		BibleWeb.getSettings().setJavaScriptEnabled(true); 
		BibleWeb.addJavascriptInterface(new AndroidBridge(), "android");
		BibleWeb.getSettings().setDomStorageEnabled(true);
		BibleWeb.getSettings().setBuiltInZoomControls(true);
		BibleWeb.getSettings().setSupportZoom(true);
		//		BibleWeb.setDownloadListener(new DownloadListener() {
		//			public void onDownloadStart(String url, String userAgent,
		//					String contentDisposition, String mimetype,
		//					long contentLength) {
		//
		//				Uri uri = Uri.parse(url);
		//				Intent intent = new Intent(Intent.ACTION_VIEW,uri);
		//				startActivity(intent);
		//			}
		//		});

		BibleWeb.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
				Log.e("SKY" , "-AAABB-");
				DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

				request.setMimeType(mimeType);
				//------------------------COOKIE!!------------------------
				String cookies = CookieManager.getInstance().getCookie(url);
				request.addRequestHeader("cookie", cookies);
				//------------------------COOKIE!!------------------------
				request.addRequestHeader("User-Agent", userAgent);
				request.setDescription("Downloading file...");
				request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
				request.allowScanningByMediaScanner();
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
				DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
				dm.enqueue(request);
				Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();

			}

		});
		Log.e("SKY" , "URL :: "  + url+dataSet.PHONE);
		BibleWeb.loadUrl(url+dataSet.PHONE);
		if(android.os.Build.VERSION.SDK_INT >= 11)
		{
			getWindow().addFlags(16777216);
		}

	}

	/*****************
	 * @Class WebViewClient
	 *****************/
	class ITGOWebChromeClient extends WebViewClient {

		@Override //SSL
		public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
			Log.e("SKY", "error = = = = = = = "+error);
			handler.proceed();
		}
		@Override
		public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.e("SKY", "errorCode = = = = = = = "+errorCode);

			switch(errorCode) {
			case ERROR_AUTHENTICATION: break;               // 서버에서 사용자 인증 실패
			case ERROR_BAD_URL: break;                           // 잘못된 URL
			case ERROR_CONNECT: break;                          // 서버로 연결 실패
			case ERROR_FAILED_SSL_HANDSHAKE: break;    // SSL handshake 수행 실패
			case ERROR_FILE: break;                                  // 일반 파일 오류
			case ERROR_FILE_NOT_FOUND: break;               // 파일을 찾을 수 없습니다
			case ERROR_HOST_LOOKUP: break;           // 서버 또는 프록시 호스트 이름 조회 실패
			case ERROR_IO: break;                              // 서버에서 읽거나 서버로 쓰기 실패
			case ERROR_PROXY_AUTHENTICATION: break;   // 프록시에서 사용자 인증 실패
			case ERROR_REDIRECT_LOOP: break;               // 너무 많은 리디렉션
			case ERROR_TIMEOUT: break;                          // 연결 시간 초과
			case ERROR_TOO_MANY_REQUESTS: break;     // 페이지 로드중 너무 많은 요청 발생
			case ERROR_UNKNOWN: break;                        // 일반 오류
			case ERROR_UNSUPPORTED_AUTH_SCHEME: break; // 지원되지 않는 인증 체계
			case ERROR_UNSUPPORTED_SCHEME: break;          // URI가 지원되지 않는 방식
			}
		}
		@Override 
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.e("SKY", "shouldOverrideUrlLoading = = = = = = = "+url);
			myTTS.stop();
			if( url.startsWith("http:") || url.startsWith("https:") ) {
				return false;

			}else {
				boolean override = false;
				Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(url));
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
				if( url.startsWith("sms:")){
					Intent i = new Intent( Intent.ACTION_SENDTO, Uri.parse(url));
					startActivity(i);
					return true;
				} else if( url.startsWith("tel:")){
					Intent i = new Intent( Intent.ACTION_CALL, Uri.parse(url));
					startActivity(i);
					return true;
				} else if( url.startsWith("mailto:")){
					Intent i = new Intent( Intent.ACTION_SENDTO, Uri.parse(url));
					startActivity(i);
					return true;
				} else if (url != null && url.startsWith("intent://")) {
					try {
						Intent intent2 = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
						Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent2.getPackage());
						if (existPackage != null) {
							startActivity(intent2);
						} else {
							Intent marketIntent = new Intent(Intent.ACTION_VIEW);
							marketIntent.setData(Uri.parse("market://details?id="+intent2.getPackage()));
							startActivity(marketIntent);
						}
						return true;
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					startActivity(intent);
					override = true;
				}
				catch (ActivityNotFoundException e) {
					return override;
				}
			}
			view.loadUrl(url);

			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override 
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.e("SKY", "onPageStarted = = = = = = = "+url);
			fix_url = url;
			if (url.matches(".*sharp5200.*")) {
				URL_NOW = url;
			}


		}
		@Override
		public void onPageFinished(WebView view, String url){
			//			CookieSyncManager.getInstance().sync();
			super.onPageFinished(view, url); 
			Log.e("SKY", "onPageFinished = = = = = = = "+url);
			//하단 bottomView visible
			if (url.matches("http://sharp5200.cafe24.com/bible/bible_view.do.*") ||
					url.matches("http://ch2ho.bible25.com/m/bbs.*") || 
					url.matches(".*DBSQL.*")) {
				bottomview.setVisibility(View.GONE);
			}else{
				bottomview.setVisibility(View.GONE);
			}
			//BibleWeb.loadUrl("javascript:abc()");

			if (url.indexOf("js2ios://") != -1) {
				view.stopLoading();
				try{
					url = URLDecoder.decode(url, "UTF-8"); 
				}catch(Exception e){
				} 
				SplitFun(url);
				/*
				if (url.matches(".*DBSQL.*")) {
					bottomview.setVisibility(View.GONE);
				}else{
					bottomview.setVisibility(View.GONE);
				}
				 */
				Log.e("SKY", "함수 시작");
				//view.stopLoading();
			}
			if(url.matches(".*listExcel.do")){
				Log.e("SKY" , "다운 고고");
				Date d = new Date();
				String s = d.toString();
				System.out.println("현재날짜 : "+ s);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				System.out.println("현재날짜 : "+ sdf.format(d));
				String date = sdf.format(d);


				Uri source = Uri.parse(url);
				// Make a new request pointing to the .apk url
				DownloadManager.Request request = new DownloadManager.Request(source);
				// appears the same in Notification bar while downloading
				request.setDescription("Description for the DownloadManager Bar");
				request.setTitle("event_" + date +  ".xls");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					request.allowScanningByMediaScanner();
					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				}
				// save the file in the "Downloads" folder of SDCARD
				request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "event_" + date +  ".xls");
				// get download service and enqueue file
				DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				manager.enqueue(request);
				//return;
			}
		}
	}
	/*
	 * 안드로이드 브릿지 연결
	 * */
	public class AndroidBridge {
		@SuppressWarnings("unused")
		public void setMessage(String arg) {
			Log.e("SKY" , "setMessage :: " + arg);
			/*
			try{
				url = URLDecoder.decode(url, "UTF-8"); 
			}catch(Exception e){
			} 
			SplitFun(url);
			 */
		}
	}
	/*****************
	 * @Class WebChromeClient
	 *****************/
	class SMOWebChromeClient extends WebChromeClient{
		private View mCustomView;
		private Activity mActivity;

		public SMOWebChromeClient(Activity activity) {
			this.mActivity = activity;
		}

		public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
			//Toast.makeText(getApplicationContext(), "message :  " + message, 0).show();
			Log.e("SKY" , "MESSAGE :: " + message);
			if (message.indexOf("js2ios://") != -1) {
				Log.e("SKY" ,"고");
				try{
					message = URLDecoder.decode(message, "UTF-8"); 
				}catch(Exception e){
				} 
				SplitFun(message);
				result.confirm();
				return true;
			}

			new AlertDialog.Builder(MainActivity.this).setTitle("확인").setMessage(message).setPositiveButton(
					android.R.string.ok, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog,int which) {
							result.confirm();
						}
					}).setCancelable(false).create().show();

			return true;

		};
		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			// TODO Auto-generated method stub
			//return super.onJsConfirm(view, url, message, result);
			new AlertDialog.Builder(view.getContext())
			.setTitle("알림")
			.setMessage(message)
			.setPositiveButton("네",
					new AlertDialog.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					result.confirm();
				}
			})
			.setNegativeButton("아니오", 
					new AlertDialog.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					result.cancel();
				}
			})
			.setCancelable(false)
			.create()
			.show();
			return true;
		}

		// For Android 3.0+
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {

			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("image/*");
			startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
		}

		// For Android 3.0+
		public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("*/*");
			startActivityForResult(
					Intent.createChooser(i, "File Browser"),
					FILECHOOSER_RESULTCODE);
		}

		//For Android 4.1
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("image/*");
			startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

		}
		// For Android 5.0+
		public boolean onShowFileChooser(
				WebView webView, ValueCallback<Uri[]> filePathCallback,
				WebChromeClient.FileChooserParams fileChooserParams) {System.out.println("WebViewActivity A>5, OS Version : " + Build.VERSION.SDK_INT + "\t onSFC(WV,VCUB,FCP), n=3");
				if (mFilePathCallback != null) {
					mFilePathCallback.onReceiveValue(null);
				}
				mFilePathCallback = filePathCallback;
				imageChooser();
				return true;


		}
		private void imageChooser() {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
				// Create the File where the photo should go
				File photoFile = null;
				try {
					photoFile = createImageFile();
					takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
				} catch (IOException ex) {
					// Error occurred while creating the File
					Log.e(getClass().getName(), "Unable to create Image File", ex);
				}

				// Continue only if the File was successfully created
				if (photoFile != null) {
					mCameraPhotoPath = "file:"+photoFile.getAbsolutePath();
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(photoFile));
				} else {
					takePictureIntent = null;
				}
			}

			Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
			contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
			contentSelectionIntent.setType(TYPE_IMAGE);

			Intent[] intentArray;
			if(takePictureIntent != null) {
				intentArray = new Intent[]{takePictureIntent};
			} else {
				intentArray = new Intent[0];
			}

			Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
			chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
			chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

			startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File imageFile = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
				);
		return imageFile;
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//홍진:openWindow로 추가 된 뷰가있으면 메인웹뷰에서 삭제해준다.
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(BibleWeb_s!=null){
				BibleWeb.removeView(BibleWeb_s);
				return true;
			}
		}
		if ((keyCode == KeyEvent.KEYCODE_BACK) && BibleWeb.canGoBack()) {
			if ("http://sharp5200.cafe24.com/index.do".equals(fix_url)) {
				return true;
			}
			myTTS.stop();
			WebBackForwardList webBackForwardList = BibleWeb.copyBackForwardList();
			String backUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();
			if (backUrl.matches(".*js2ios.*")) {
				BibleWeb.goBackOrForward(-2);
			}else{
				BibleWeb.goBack();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private void SplitFun(String url){
		url = url.replace("js2ios://", "");
		String Fun = url.substring(0, url.indexOf("?"));
		Log.e("SKY", "Fun :: "+Fun);
		String param[] = url.split("&");
		String val[]  = new String[param.length];
		Log.e("SKY", "parameter ea :: "+param.length);
		String par = "" , return_fun = "";
		for (int i = 0; i < param.length; i++) {
			//Log.e("SKY", "parameter ea :: " + "i :: " + i + " ::" +param[i]);
			val[i] = param[i].substring(param[i].indexOf("=")+1, param[i].length());
			Log.e("SKY", "parameter ea :: " + "i :: " + i + " ::" +val[i]);
			if (i == 0) {
				par = val[i];
			}else if( i == (param.length-1)){
				return_fun = val[i];
			}else{
				par += "," +val[i];
			}
		}
		try {
			//String parameter
			Class[] paramString = new Class[4];
			paramString[0] = String.class;
			paramString[1] = Activity.class;
			paramString[2] = WebView.class;
			paramString[3] = String.class;
			@SuppressWarnings("rawtypes")
			Class cls = Class.forName("co.kr.sky.hymnbible.fun.FunNative");
			Object obj = null;
			try {
				obj = cls.newInstance();
			} catch (java.lang.InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//call the printIt method
			Method method = cls.getDeclaredMethod(Fun, paramString);
			method.invoke(obj, new String(par) , MainActivity.this , BibleWeb , new String(return_fun));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
