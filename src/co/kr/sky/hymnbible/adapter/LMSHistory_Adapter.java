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
import android.widget.TextView;
import co.kr.sky.hymnbible.R;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.HistoryObj;

public class LMSHistory_Adapter extends BaseAdapter {
	CommonUtil dataSet = CommonUtil.getInstance();

	private Activity activity;
	private static LayoutInflater inflater=null;
	ArrayList<HistoryObj> items;
	private Typeface ttf;
	private Handler mAfterAccum;

	public LMSHistory_Adapter(Activity a, ArrayList<HistoryObj> m_board, Handler mAfterAccum_) {
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
		TextView t_date , t_body ,t_total_count,t_send_count;
		Button del;

	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		final HistoryObj board = items.get(position);
		ViewHolder vh = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_lms_history_item,null);
			vh.t_date = (TextView) convertView.findViewById(R.id.t_date); 
			vh.t_total_count = (TextView) convertView.findViewById(R.id.t_total_count); 
			vh.t_body = (TextView) convertView.findViewById(R.id.t_body); 
			vh.t_send_count = (TextView) convertView.findViewById(R.id.t_send_count); 
			vh.del = (Button) convertView.findViewById(R.id.del); 

			convertView.setTag(vh);
		}else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.t_date.setTypeface(ttf);
		vh.t_total_count.setTypeface(ttf);
		vh.t_body.setTypeface(ttf);
		vh.t_send_count.setTypeface(ttf);
		
		vh.t_date.setText(board.getDate());
		vh.t_total_count.setText(board.getPhone());
		vh.t_body.setText(board.getBody());
		vh.t_send_count.setText(board.getPhone());
		
		vh.del.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("SKY", "KEY :: " + board.getKey_index());
				Message msg2 = mAfterAccum.obtainMessage();
				msg2.arg1 = 9001;
				msg2.arg2 = Integer.parseInt(board.getKey_index());
				mAfterAccum.sendMessage(msg2);
			}
		});
		return convertView;
	}

}