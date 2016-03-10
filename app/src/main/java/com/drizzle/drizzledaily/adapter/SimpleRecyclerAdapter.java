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
import java.util.List;

/**
 * Created by drizzle on 16/3/10.
 */
public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.SimpleRecyclerHolder> {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<BaseListItem> mBaseListItemList;
	private AdapterView.OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	public SimpleRecyclerAdapter(Context context, List<BaseListItem> baseListItemList) {
		mContext = context;
		mBaseListItemList = baseListItemList;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override public void onBindViewHolder(SimpleRecyclerHolder holder, final int position) {
		holder.mItemTitle.setText(mBaseListItemList.get(position).getTitle());
		holder.mItemDate.setText(mBaseListItemList.get(position).getDate());
		holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(null, null, position, 0);
				}
			}
		});
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
