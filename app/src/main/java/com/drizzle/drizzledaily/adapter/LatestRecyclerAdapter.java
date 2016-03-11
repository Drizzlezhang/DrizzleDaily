package com.drizzle.drizzledaily.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.ui.fragments.HeadPagerFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by drizzle on 16/3/10.
 */
public class LatestRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<BaseListItem> baseListItemList = new ArrayList<>();
	private List<BaseListItem> headListItemList = new ArrayList<>();
	private FragmentStatePagerAdapter fragmentStatePagerAdapter;
	private FragmentManager mFragmentManager;

	private AdapterView.OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	private enum ITEM_TYPE {
		ITEM_TYPE_DATE,
		ITEM_TYPE_TEXT,
		ITEM_TYPE_PAGER
	}

	public LatestRecyclerAdapter(Context context, List<BaseListItem> baseListItemList,
		List<BaseListItem> headListItemList, FragmentManager fragmentManager) {
		mContext = context;
		this.baseListItemList = baseListItemList;
		this.headListItemList = headListItemList;
		this.mFragmentManager = fragmentManager;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override public int getItemCount() {
		return baseListItemList.size() + 1;
	}

	@Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
		if (holder instanceof LatestDateHolder) {
			((LatestDateHolder) holder).dateText.setText(baseListItemList.get(position - 1).getDate());
		} else if (holder instanceof LatestItemHolder) {
			Glide.with(mContext)
				.load(baseListItemList.get(position - 1).getImgUrl())
				.centerCrop()
				.error(R.mipmap.place_img)
				.crossFade()
				.into(((LatestItemHolder) holder).itemImg);
			((LatestItemHolder) holder).itemTitle.setText(baseListItemList.get(position - 1).getTitle());
			((LatestItemHolder) holder).itemCard.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					mOnItemClickListener.onItemClick(null, null, position, 0);
				}
			});
		} else {
			fragmentStatePagerAdapter = new FragmentStatePagerAdapter(mFragmentManager) {
				@Override public Fragment getItem(int position) {
					BaseListItem baseListItem = headListItemList.get(position);
					HeadPagerFragment pagerFragment =
						HeadPagerFragment.newInstance(baseListItem.getImgUrl(), baseListItem.getTitle(),
							baseListItem.getId());
					return pagerFragment;
				}

				@Override public int getCount() {
					return headListItemList.size();
				}
			};
			((LatestPagerHolder) holder).itemViewPager.setInterval(3000);
			((LatestPagerHolder) holder).itemViewPager.setStopScrollWhenTouch(true);
			((LatestPagerHolder) holder).itemViewPager.setAdapter(fragmentStatePagerAdapter);
			((LatestPagerHolder) holder).itemViewPager.startAutoScroll(5000);
		}
	}

	@Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == ITEM_TYPE.ITEM_TYPE_DATE.ordinal()) {
			return new LatestDateHolder(mLayoutInflater.inflate(R.layout.base_date_item, parent, false));
		} else if (viewType == ITEM_TYPE.ITEM_TYPE_TEXT.ordinal()) {
			return new LatestItemHolder(mLayoutInflater.inflate(R.layout.base_list_item, parent, false));
		} else {
			return new LatestPagerHolder(mLayoutInflater.inflate(R.layout.head_viewpager, parent, false));
		}
	}

	@Override public int getItemViewType(int position) {
		if (position == 0) {
			return ITEM_TYPE.ITEM_TYPE_PAGER.ordinal();
		}
		int type = baseListItemList.get(position - 1).getViewType();
		if (type == 0) {
			return ITEM_TYPE.ITEM_TYPE_DATE.ordinal();
		} else {
			return ITEM_TYPE.ITEM_TYPE_TEXT.ordinal();
		}
	}

	public static class LatestDateHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.base_date_item_text) TextView dateText;

		public LatestDateHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	public static class LatestItemHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.base_item_img) ImageView itemImg;
		@Bind(R.id.base_item_title) TextView itemTitle;
		@Bind(R.id.list_item_card) CardView itemCard;

		public LatestItemHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	public static class LatestPagerHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.head_viewpager) AutoScrollViewPager itemViewPager;

		public LatestPagerHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
