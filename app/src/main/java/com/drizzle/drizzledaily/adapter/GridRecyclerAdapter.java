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
import java.util.List;

/**
 * Created by drizzle on 16/3/9.
 */
public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.GridViewHolder> {
	private List<BaseListItem> mBaseListItemList;
	private Context mContext;
	private LayoutInflater mLayoutInflater;

	private AdapterView.OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	public GridRecyclerAdapter(List<BaseListItem> baseListItemList, Context context) {
		mBaseListItemList = baseListItemList;
		mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override public long getItemId(int position) {
		return super.getItemId(position);
	}

	@Override public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new GridViewHolder(mLayoutInflater.inflate(R.layout.base_grid_item, parent, false));
	}

	@Override public void onBindViewHolder(final GridViewHolder holder, final int position) {
		ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();//得到item的LayoutParams布局参数
		params.height = (int) (800 + Math.random() * 300);//把随机的高度赋予itemView布局
		holder.itemView.setLayoutParams(params);//把params设置给itemView布局
		holder.gridItemTitle.setText(mBaseListItemList.get(position).getTitle());
		holder.gridItemDescribe.setText(mBaseListItemList.get(position).getDescribe());
		Glide.with(mContext)
			.load(mBaseListItemList.get(position).getImgUrl())
			.centerCrop()
			.error(R.mipmap.place_img)
			.crossFade()
			.into(holder.gridItemImg);
		holder.mCardView.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(null, holder.itemView, position, 0);
				}
			}
		});
	}

	@Override public int getItemCount() {
		return mBaseListItemList.size();
	}

	public static class GridViewHolder extends RecyclerView.ViewHolder {

		@Bind(R.id.grid_item_card) CardView mCardView;
		@Bind(R.id.grid_item_title) TextView gridItemTitle;
		@Bind(R.id.grid_item_describe) TextView gridItemDescribe;
		@Bind(R.id.grid_item_img) ImageView gridItemImg;

		public GridViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
