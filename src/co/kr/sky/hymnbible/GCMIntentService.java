package co.kr.sky.hymnbible;



import java.util.HashMap;
import java.util.Map;

import com.google.android.gcm.GCMBaseIntentService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import co.kr.sky.AccumThread;
import co.kr.sky.hymnbible.fun.CommonUtil;

public class GCMIntentService extends GCMBaseIntentService {

	public static String re_message=null;
	static Map<String, String> map = new HashMap<String, String>();

	CommonUtil dataSet = CommonUtil.getInstance();
	AccumThread mThread;


	private static void generateNotification(Context context, String message) {
  
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		
		//진동 
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		long milliseconds = 1000;
		vibrator.vibrate(milliseconds);
		
		
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		String title = "성경과찬송-뉴";

		Intent notificationIntent = new Intent(context,MainActivity.class);
		re_message= message	;

		Log.e("SKY" , "message --->" + message);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


		PendingIntent intent = PendingIntent.getActivity(context, 0,notificationIntent, 0);

		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);


	}

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.e("SKY", "onError:" + arg1);
	}
	@Override
	protected void onMessage(Context context, Intent intent) {
		String msg = intent.getStringExtra("msg");
		Log.e("SKY", "getmessage:" + msg);
		generateNotification(context,msg);
	}
	@Override
	protected void onRegistered(Context context, String reg_id) {
		dataSet.REG_ID = reg_id;
		Log.e("(GCM INTENTSERVICE)", reg_id);
		
		map.clear();
		map.put("url", dataSet.SERVER+"json/updateRegid.do");
		map.put("phone",dataSet.PHONE);
		map.put("reg_id",dataSet.REG_ID);
		map.put("type","android");
		mThread = new AccumThread(context , mAfterAccum , map , 0 , 0 , null);
		mThread.start();		//스레드 시작!!
	}
	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.e("(GCM INTENTSERVICE)","���ŵǾ����ϴ�.");
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
}