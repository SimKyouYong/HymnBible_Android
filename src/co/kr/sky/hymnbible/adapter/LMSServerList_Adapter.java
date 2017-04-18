package co.kr.sky.hymnbible.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
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
		TextView t_name;
		CheckBox check;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		final MyServerListObj board = items.get(position);
		ViewHolder vh = new ViewHolder();
		convertView = inflater.inflate(R.layout.activity_lms_server_list_item,null);
		vh.t_name = (TextView) convertView.findViewById(R.id.t_name); 
		vh.check = (CheckBox) convertView.findViewById(R.id.check); 

		convertView.setTag(vh);
		vh.t_name.setTypeface(ttf);
		vh.t_name.setText("" + position + ". " + board.getName() + "(" + board.getPhone().replace("-", "") + ")");
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

}