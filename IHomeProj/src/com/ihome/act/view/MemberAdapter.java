package com.ihome.act.view;

import java.util.List;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihome.R;
import com.ihome.serv.MemberInfor;

public class MemberAdapter extends BaseAdapter {

	private List<MemberInfor> members;
	private ViewHolder mHolder;
	private LayoutInflater inflater;

	public MemberAdapter(Activity act, List<MemberInfor> mems) {

		inflater = act.getLayoutInflater();
		members = mems;
	}

	@Override
	public int getCount() {
		return members == null ? 0 : members.size();
	}

	@Override
	public Object getItem(int position) {
		return members.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mHolder = new ViewHolder();

			convertView = inflater.inflate(R.layout.item_member_infor, null);

			mHolder.img = (ImageView) convertView
					.findViewById(R.id.img_search_grid_item);
			mHolder.txt = (TextView) convertView
					.findViewById(R.id.txt_search_grid_item);

			convertView.setTag(mHolder);
		} else
			mHolder = (ViewHolder) convertView.getTag();
		
		final String title = members.get(position).getTitle();
		final byte[] icon = members.get(position).getIcon();
		
		mHolder.txt.setText(title);
		mHolder.img.setImageBitmap(BitmapFactory.decodeByteArray(icon, 0, icon.length));
		
		return convertView;
	}

	public static class ViewHolder {

		public TextView txt;
		public ImageView img;

	}
}
