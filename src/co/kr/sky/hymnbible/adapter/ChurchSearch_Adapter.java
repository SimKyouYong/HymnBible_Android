package co.kr.sky.hymnbible.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import co.kr.sky.hymnbible.R;
import co.kr.sky.hymnbible.fun.CommonUtil;
import co.kr.sky.hymnbible.obj.ChurchObj;

public class ChurchSearch_Adapter extends BaseAdapter {
	CommonUtil dataSet = CommonUtil.getInstance();

	private Activity activity;
	private static LayoutInflater inflater=null;
	ArrayList<ChurchObj> items;
	private Typeface ttf;

	public ChurchSearch_Adapter(Activity a, ArrayList<ChurchObj> m_board  ) {
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
		TextView t_title , t_address , t_number;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ChurchObj board = items.get(position);
		ViewHolder vh = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_church_item,null);
			vh.t_title = (TextView) convertView.findViewById(R.id.t_title); 
			vh.t_address = (TextView) convertView.findViewById(R.id.t_address); 
			vh.t_number = (TextView) convertView.findViewById(R.id.t_number); 

			convertView.setTag(vh);
		}else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.t_title.setTypeface(ttf);
		vh.t_address.setTypeface(ttf);
		vh.t_number.setTypeface(ttf);
		
		vh.t_title.setText(board.getChurch_name());
		vh.t_address.setText(board.getChurch_address());
		vh.t_number.setText(board.getChurch_number());
		return convertView;
	}

}