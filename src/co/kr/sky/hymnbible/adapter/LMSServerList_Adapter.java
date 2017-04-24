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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import co.kr.sky.hymnbible.R;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.MyServerListObj;

public class LMSServerList_Adapter extends BaseAdapter {
	CommonUtil dataSet = CommonUtil.getInstance();

	private Activity activity;
	private static LayoutInflater inflater=null;
	ArrayList<MyServerListObj> items;
	private Typeface ttf;
	private Handler mAfterAccum;

	public LMSServerList_Adapter(Activity a, ArrayList<MyServerListObj> arrData  , Handler mAfterAccum_) {
		activity = a;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		items = arrData;
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
		TextView t_name, t_phone;
		CheckBox check;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		final MyServerListObj board = items.get(position);
		ViewHolder vh = new ViewHolder();
		convertView = inflater.inflate(R.layout.activity_lms_server_list_item,null);
		vh.t_name = (TextView) convertView.findViewById(R.id.t_name); 
		vh.t_phone = (TextView) convertView.findViewById(R.id.t_phone); 
		vh.check = (CheckBox) convertView.findViewById(R.id.check); 

		convertView.setTag(vh);
		vh.t_name.setTypeface(ttf);
		vh.t_phone.setTypeface(ttf);

		vh.t_name.setText("" +board.getName());
		vh.t_phone.setText("" +board.getPhone().replace("-", ""));
		vh.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {

				if (buttonView.getId() == R.id.check) {
					Log.e("SKY" , "클릭 :: " + isChecked);
					if (isChecked) {
						Log.e("SKY" , "클릭");
						items.get(position).setCheck(1);
					} else {
						Log.e("SKY" , "not 클릭" );
						items.get(position).setCheck(0);
					}
					Allcheck();
				}
			}
		});

		if (items.get(position).getCheck() == 0) {
			vh.check.setChecked(false);
		}else{
			vh.check.setChecked(true);
		}
		return convertView;
	}
	public void setCheck(int position){

	}
	private void Allcheck(){
		int count = 0;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getCheck() == 1) {
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