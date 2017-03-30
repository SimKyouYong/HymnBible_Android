package co.kr.sky.hymnbible;



import com.google.android.gcm.GCMBaseIntentService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import co.kr.sky.hymnbible.fun.CommonUtil;

public class GCMIntentService extends GCMBaseIntentService {

	public static String re_message=null;

	CommonUtil dataSet = CommonUtil.getInstance();


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

		String title = context.getString(R.string.app_name);

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
	}
	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.e("(GCM INTENTSERVICE)","���ŵǾ����ϴ�.");
	}

}