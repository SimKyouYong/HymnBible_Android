package co.kr.sky.hymnbible;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import co.kr.sky.hymnbible.adapter.LMSMyPhone_Adapter;
import co.kr.sky.hymnbible.obj.MyPhoneObj;

public class LMSMyPhoneDetailActivity extends Activity{
	LMSMyPhone_Adapter           m_Adapter;
	ArrayList<MyPhoneObj> arrData = new ArrayList<MyPhoneObj>();
	ListView                list_number;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myphone);
		list_number = (ListView)findViewById(R.id.list_number);
		//list_number.setOnItemClickListener(mItemClickListener);

		
		
		
	}
//	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
//		public void onItemClick(AdapterView parent, View view, int position,
//				long id) {
//			Intent board = new Intent(LMSMyPhoneDetailActivity.this, HellowTalk_Detail_Comment.class);
//			board.putExtra("Object", arrData.get(position));
//			startActivity(board);
//		}
//	};
}
