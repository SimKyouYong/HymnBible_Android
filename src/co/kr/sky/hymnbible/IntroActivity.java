package co.kr.sky.hymnbible;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


public class IntroActivity extends Activity {

	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_CONTACTS,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.SEND_SMS,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_PHONE_STATE
	};

	Handler mHandler = new Handler();
	Runnable r= new Runnable() {
		@Override
		public void run() {
			startActivity(new Intent(IntroActivity.this, MainActivity.class));
			finish();


		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

		if(permissionCheck== PackageManager.PERMISSION_DENIED){
			// 권한 없음
			Log.e("SKY", "권한 없음");
			ActivityCompat.requestPermissions(this,
					PERMISSIONS_STORAGE,
					1);
		}else{
			// 권한 있음
			Log.e("SKY", "권한 있음");
			mHandler.postDelayed(r, 2000);
		}

	}

	public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
		switch (requestCode) {
		case 1: {
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.e("SKY" , "성공");
				Log.e("SKY" , "permissions SIZE :: " + permissions.length);
				if (permissions.length == 5) {
					mHandler.postDelayed(r, 2000);
				}else{
					AlertDialog.Builder alert = new AlertDialog.Builder(IntroActivity.this, AlertDialog.THEME_HOLO_LIGHT);
					alert.setTitle("알림");
					alert.setMessage("모두 허용 하지 않으면 어플리케이션이 정상 작동 하지 않을수 있습니다.\n그래도 계속 하시겠습니까?");
					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							mHandler.postDelayed(r, 2000);
						}
					});
					// Cancel 버튼 이벤트
					alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							finish();
						}
					});
					alert.show();
				}

			} else {
				Log.e("SKY" , "실패");
			}
			return;
		}
		}
	}
}