package co.kr.sky.hymnbible;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class LMSMainActivity extends Activity{

	public static WebView ImageWebView;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		setting_web();
		findViewById(R.id.close).setOnClickListener(btnListener);


	}
	//버튼 리스너 구현 부분 
		View.OnClickListener btnListener = new View.OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {

				case R.id.close:	
					finish();
					break;

				}
			}
		};
	//버튼 리스너 구현 부분 
	private void setting_web(){
		ImageWebView = (WebView)findViewById(R.id.web);
		//홍진:openWindow 사용
		//setWebViewOpenWindow();
		ImageWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//팝업(window.open) 권한
		ImageWebView.getSettings().setSupportMultipleWindows(true); //팝업을허용하고 setSupportMultipleWindows를 주지않으면 url이로딩 된다
		ImageWebView.getSettings().setJavaScriptEnabled(true); 
		ImageWebView.getSettings().setDomStorageEnabled(true);
		ImageWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		ImageWebView.getSettings().setBuiltInZoomControls(true);
		ImageWebView.getSettings().setSupportZoom(true);
		ImageWebView.setInitialScale(1);
		ImageWebView.getSettings().setLoadWithOverviewMode(true) ;
		ImageWebView.getSettings().setUseWideViewPort(true);
		ImageWebView.setBackgroundColor(Color.WHITE);

		if(android.os.Build.VERSION.SDK_INT >= 11)
		{
			getWindow().addFlags(16777216);
		}
		Log.e("SKY" , "url go :: " + getIntent().getStringExtra("url"));

		ImageWebView.loadUrl(getIntent().getStringExtra("url"));
	}
}
