package co.kr.sky.hymnbible;

import com.android.volley.toolbox.NetworkImageView;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import co.kr.sky.hymnbible.net.H5ImageLoader;
import co.kr.sky.hymnbible.obj.ChurchObj;

public class ChurchSearch_Detail extends Activity{
	ChurchObj m_board;

	TextView title , church_name , church_type , person_name , church_address , church_number;
	TextView church_fax , church_homepage , church_body , church_post;

	TextView a1 , a2 ,a3 , a4 , a5 ,a6 ,a7 , a8, a9;
	NetworkImageView i_img;
	private Typeface ttf;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_church_detail);
		ttf = Typeface.createFromAsset(getAssets(), "HANYGO230.TTF");

		Bundle bundle = getIntent().getExtras();
		m_board = bundle.getParcelable("Object");

		//버튼 리스너 설정 
		findViewById(R.id.btn_back).setOnClickListener(btnListener);

		i_img 			= (NetworkImageView)findViewById(R.id.i_img);
		title 			= (TextView)findViewById(R.id.title);
		church_name 	= (TextView)findViewById(R.id.church_name);
		church_type 	= (TextView)findViewById(R.id.church_type);
		person_name 	= (TextView)findViewById(R.id.person_name);
		church_address  = (TextView)findViewById(R.id.church_address);
		church_number 	= (TextView)findViewById(R.id.church_number);
		church_fax 		= (TextView)findViewById(R.id.church_fax);
		church_homepage = (TextView)findViewById(R.id.church_homepage);
		church_body 	= (TextView)findViewById(R.id.church_body);
		church_post 	= (TextView)findViewById(R.id.church_post);

		a1 	= (TextView)findViewById(R.id.a1);
		a2 	= (TextView)findViewById(R.id.a2);
		a3 	= (TextView)findViewById(R.id.a3);
		a4 	= (TextView)findViewById(R.id.a4);
		a5 	= (TextView)findViewById(R.id.a5);
		a6 	= (TextView)findViewById(R.id.a6);
		a7 	= (TextView)findViewById(R.id.a7);
		a8 	= (TextView)findViewById(R.id.a8);
		a9 	= (TextView)findViewById(R.id.a9);

		a1.setTypeface(ttf);
		a2.setTypeface(ttf);
		a3.setTypeface(ttf);
		a4.setTypeface(ttf);
		a5.setTypeface(ttf);
		a6.setTypeface(ttf);
		a7.setTypeface(ttf);
		a8.setTypeface(ttf);
		a9.setTypeface(ttf);



		title.setTypeface(ttf);
		church_name.setTypeface(ttf);
		church_type.setTypeface(ttf);
		person_name.setTypeface(ttf);
		church_address.setTypeface(ttf);
		church_number.setTypeface(ttf);
		church_fax.setTypeface(ttf);
		church_homepage.setTypeface(ttf);
		church_body.setTypeface(ttf);
		church_post.setTypeface(ttf);


		title.setText("" 				+ m_board.getChurch_name());
		church_name.setText("" 			+ m_board.getChurch_name());
		church_type.setText("" 			+ m_board.getChurch_type());
		person_name.setText("" 			+ m_board.getPerson_name());
		church_address.setText("" 		+ m_board.getChurch_address());
		church_number.setText("" 		+ m_board.getChurch_number());
		church_fax.setText("" 			+ m_board.getChurch_fax());
		church_homepage.setText("" 		+ m_board.getChurch_homepage());
		church_body.setText("" 			+ m_board.getChurch_body());
		//church_post.setText("" 			+ m_board.getChurch_post());

		H5ImageLoader.getInstance(this).set( m_board.getChurch_img(), i_img);


	}
	//버튼 리스너 구현 부분 
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.btn_back:	
				Log.e("SKY"  , "--btn_back--");
				finish();
				break;
			}
		}
	};
}
