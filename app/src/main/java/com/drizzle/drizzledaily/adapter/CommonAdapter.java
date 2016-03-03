package com.drizzle.drizzledaily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.gson.Gson;
import java.util.List;

/**
 * 通用adapter，保持一个泛型List
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

	protected LayoutInflater mInflater;
	protected Context mContext;
	protected List<T> mDatas;
	protected final int mItemLayoutId;

	public CommonAdapter(Context mContext, List<T> mDatas, int mItemLayoutId) {
		mInflater = LayoutInflater.from(mContext);
		this.mContext = mContext;
		this.mDatas = mDatas;
		this.mItemLayoutId = mItemLayoutId;
	}

	public String getJson() {
		Gson gson = new Gson();
		return gson.toJson(mDatas);
	}

	@Override public int getCount() {
		return mDatas.size();
	}

	@Override public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override public long getItemId(int position) {
		return position;
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = getViewHolder(position, convertView, parent);
		convert(viewHolder, getItem(position));
		return viewHolder.getConvertView();
	}

	public abstract void convert(ViewHolder helper, T item);

	private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
		return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
	}
}
