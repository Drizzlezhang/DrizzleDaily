package com.drizzle.drizzledaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.utils.CheckUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by drizzle on 16/3/10.
 */
public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.SimpleRecyclerHolder> {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<BaseListItem> mBaseListItemList;
	private AdapterView.OnItemClickListener mOnItemClickListener;
	private Gson gson;
	private List<Integer> alreadyList;

	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	public SimpleRecyclerAdapter(Context context, List<BaseListItem> baseListItemList) {
		mContext = context;
		mBaseListItemList = baseListItemList;
		mLayoutInflater = LayoutInflater.from(mContext);
		gson = new Gson();
		alreadyList = new ArrayList<>();
	}

	@Override public void onBindViewHolder(final SimpleRecyclerHolder holder, final int position) {
		final int itemId = mBaseListItemList.get(position).getId();
		if (CheckUtils.checkIsAlreadyClick(itemId) || alreadyList.contains(itemId)) {
			holder.mItemTitle.setTextColor(mContext.getResources().getColor(R.color.textgrey));
			holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(null, null, position, 0);
					}
				}
			});
		} else {
			holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(null, null, position, 0);
						holder.mItemTitle.setTextColor(mContext.getResources().getColor(R.color.textgrey));
						String alreadyclick = PerferUtils.getStringList(Config.ALREADY_CLICK);
						Set<Integer> alreadySet = gson.fromJson(alreadyclick, new TypeToken<Set<Integer>>() {
						}.getType());
						alreadySet.add(itemId);
						alreadyList.add(itemId);
						PerferUtils.saveSth(Config.ALREADY_CLICK, gson.toJson(alreadySet));
					}
				}
			});
			holder.mItemTitle.setTextColor(mContext.getResources().getColor(R.color.textblack));
		}
		holder.mItemTitle.setText(mBaseListItemList.get(position).getTitle());
		holder.mItemDate.setText(mBaseListItemList.get(position).getDate());
	}

	@Override public int getItemCount() {
		return mBaseListItemList.size();
	}

	@Override public SimpleRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new SimpleRecyclerHolder(mLayoutInflater.inflate(R.layout.simple_list_item, parent, false));
	}

	@Override public long getItemId(int position) {
		return super.getItemId(position);
	}

	public static class SimpleRecyclerHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.simple_item_title) TextView mItemTitle;
		@Bind(R.id.simple_item_date) TextView mItemDate;
		@Bind(R.id.simple_item_layout) FrameLayout mItemLayout;

		public SimpleRecyclerHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
