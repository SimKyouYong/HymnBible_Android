package co.kr.sky.hymnbible.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
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

	public LMSHistory_Adapter(Activity a, ArrayList<HistoryObj> m_board) {
		activity = a;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		items = m_board;
		ttf = Typeface.createFromAsset(activity.getAssets(), "HANYGO230.TTF");

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
		TextView t_date ,t_status , t_body ;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		final HistoryObj board = items.get(position);
		ViewHolder vh = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_lms_history_item,null);
			vh.t_date = (TextView) convertView.findViewById(R.id.t_date); 
			vh.t_status = (TextView) convertView.findViewById(R.id.t_status); 
			vh.t_body = (TextView) convertView.findViewById(R.id.t_body); 

			convertView.setTag(vh);
		}else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.t_date.setTypeface(ttf);
		vh.t_status.setTypeface(ttf);
		vh.t_body.setTypeface(ttf);
		
		vh.t_date.setText(board.getDate());
		//vh.t_status.setText(board.getName());
		vh.t_body.setText(board.getBody());
		return convertView;
	}

}