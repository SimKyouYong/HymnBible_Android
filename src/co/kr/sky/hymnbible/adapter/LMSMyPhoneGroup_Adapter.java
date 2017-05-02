package co.kr.sky.hymnbible.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import co.kr.sky.hymnbible.LMSMyPhoneActivity;
import co.kr.sky.hymnbible.R;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.MyPhoneGroupObj;

public class LMSMyPhoneGroup_Adapter extends BaseAdapter {
	CommonUtil dataSet = CommonUtil.getInstance();

	private Activity activity;
	private static LayoutInflater inflater=null;
	ArrayList<MyPhoneGroupObj> items;
	private Typeface ttf;
	private Handler mAfterAccum;

	public LMSMyPhoneGroup_Adapter(Activity a, ArrayList<MyPhoneGroupObj> m_board  , Handler mAfterAccum_) {
		activity = a;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		items = m_board;
		ttf = Typeface.createFromAsset(activity.getAssets(), "HANYGO230.TTF");
		mAfterAccum = mAfterAccum_;

	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView t_name;
		CheckBox check;
		Button del;

	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		final MyPhoneGroupObj board = items.get(position);
		final ViewHolder vh = new ViewHolder();
		convertView = inflater.inflate(R.layout.activity_lms_myphone_item,null);
		vh.t_name = (TextView) convertView.findViewById(R.id.t_name); 
		vh.check = (CheckBox) convertView.findViewById(R.id.check); 
		vh.del = (Button) convertView.findViewById(R.id.del); 

		convertView.setTag(vh);
		vh.t_name.setTypeface(ttf);

		vh.t_name.setText(board.getTITLE() + "(" + board.getSELECTED_COUNT() + " / " + board.getGROUP_COUNT() + ")");
		vh.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {

				if (buttonView.getId() == R.id.check) {
					if (isChecked) {
						Log.e("SKY" , "클릭");
						board.setSELECTED(1);
						vh.t_name.setText(board.getTITLE() + "(" + board.getGROUP_COUNT() + " / " + board.getGROUP_COUNT() + ")");
						String str = LMSMyPhoneActivity.check_count.getText().toString().replace("명", "").replace(" ", "");
						LMSMyPhoneActivity.check_count.setText(""+ (Integer.parseInt(str) + Integer.parseInt(board.getGROUP_COUNT())) );
					} else {
						Log.e("SKY" , "not 클릭" );
						board.setSELECTED(0);
						String str = LMSMyPhoneActivity.check_count.getText().toString().replace("명", "").replace(" ", "");
						LMSMyPhoneActivity.check_count.setText(""+ (Integer.parseInt(str) - Integer.parseInt(board.getGROUP_COUNT())) );

					}
					Allcheck();
					
				}
			}
		});
		vh.del.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg2 = mAfterAccum.obtainMessage();
				msg2.arg1 = 9001;
				msg2.arg2 = Integer.parseInt(board.get_ID());
				mAfterAccum.sendMessage(msg2);
			}
		});
		if (board.getSELECTED() == 0) {
			vh.check.setChecked(false);
		}else{
			vh.check.setChecked(true);
		}

		return convertView;
	}
	private void Allcheck(){
		int count = 0;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getSELECTED() == 1) {
				count++;
			}
		}
		Log.e("SKY" , "count :: " + count);
		Log.e("SKY" , "arrData.size() :: " + items.size());
		if (count == items.size()) {
			Message msg2 = mAfterAccum.obtainMessage();
			msg2.arg1 = 7000;
			mAfterAccum.sendMessage(msg2);
		}else if(count > 0){
			Message msg2 = mAfterAccum.obtainMessage();
			msg2.arg1 = 9000;
			mAfterAccum.sendMessage(msg2);
		}else{
			Message msg2 = mAfterAccum.obtainMessage();
			msg2.arg1 = 8000;
			mAfterAccum.sendMessage(msg2);
		}
	}

}