package com.michael.main;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListBookTopAdapter extends BaseAdapter {
	private ArrayList<String> mList;
	private Context mContext;
	public ListBookTopAdapter(ArrayList<String> list,
			Context mContext) {
		super();
		this.mList = list;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		if (mList == null) {
			return 0;
		} else {
			return this.mList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (mList == null) {
			return null;
		} else {
			return this.mList.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.list_item_booktop, null, false);
			holder.textView = (TextView) convertView.findViewById(R.id.textView2);
			holder.textIndex = (TextView) convertView.findViewById(R.id.textView1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.textView.setText(mList.get(position));
		holder.textIndex.setText(""+(position+1));

		return convertView;
	}

	private class ViewHolder {
		TextView textView, textIndex;
	}
}
