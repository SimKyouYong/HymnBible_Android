package co.kr.sky.hymnbible.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import co.kr.sky.hymnbible.R;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.LMSMainObj;

public class LMSMain_Adapter extends BaseAdapter {
	CommonUtil dataSet = CommonUtil.getInstance();

	private Activity activity;
	private static LayoutInflater inflater=null;
	ArrayList<LMSMainObj> items;
	private Typeface ttf;
	private Handler mAfterAccum;

	public LMSMain_Adapter(Activity a, ArrayList<LMSMainObj> m_board  , Handler mAfterAccum_) {
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
		TextView t_name , t_phone;
		Button del;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		final LMSMainObj board = items.get(position);
		ViewHolder vh = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_lms_main_item,null);
			vh.t_name = (TextView) convertView.findViewById(R.id.t_name); 
			vh.t_phone = (TextView) convertView.findViewById(R.id.t_phone); 
			vh.del = (Button) convertView.findViewById(R.id.del); 

			convertView.setTag(vh);
		}else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.t_name.setTypeface(ttf);
		vh.t_phone.setTypeface(ttf);
		
		vh.t_name.setText(board.getName());
		vh.t_phone.setText(board.getNumber());
		vh.del.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Message msg2 = mAfterAccum.obtainMessage();
				msg2.obj = position;
				msg2.arg1 = 0;
				mAfterAccum.sendMessage(msg2);
			}
		});
		return convertView;
	}

}