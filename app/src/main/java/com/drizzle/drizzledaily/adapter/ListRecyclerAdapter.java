package com.drizzle.drizzledaily.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
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
public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ListRecyclerViewHolder> {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<BaseListItem> mBaseListItemList;
	private Gson gson;
	private List<Integer> alreadyList;

	private AdapterView.OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	public ListRecyclerAdapter(Context context, List<BaseListItem> baseListItemList) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mBaseListItemList = baseListItemList;
		gson = new Gson();
		alreadyList = new ArrayList<>();
	}

	@Override public ListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ListRecyclerViewHolder(mLayoutInflater.inflate(R.layout.base_list_item, parent, false));
	}

	@Override public int getItemCount() {
		return mBaseListItemList.size();
	}

	@Override public void onBindViewHolder(final ListRecyclerViewHolder holder, final int position) {
		final int itemId = mBaseListItemList.get(position).getId();
		if (CheckUtils.checkIsAlreadyClick(itemId) || alreadyList.contains(itemId)) {
			holder.mItemTitle.setTextColor(mContext.getResources().getColor(R.color.textgrey));
			holder.mItemCard.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(null, holder.itemView, position, 0);
					}
				}
			});
		} else {
			holder.mItemCard.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(null, holder.itemView, position, 0);
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
		Glide.with(mContext)
			.load(mBaseListItemList.get(position).getImgUrl())
			.centerCrop()
			.error(R.mipmap.place_img)
			.crossFade()
			.into(holder.mItemImg);
	}

	@Override public long getItemId(int position) {
		return super.getItemId(position);
	}

	public static class ListRecyclerViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.list_item_card) CardView mItemCard;
		@Bind(R.id.base_item_img) ImageView mItemImg;
		@Bind(R.id.base_item_title) TextView mItemTitle;
		@Bind(R.id.base_item_date) TextView mItemDate;

		public ListRecyclerViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
