package com.drizzle.drizzledaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.CollectBean;
import java.util.List;

/**
 * Created by drizzle on 16/3/10.
 */
public class SwipeRecyclerAdapter extends RecyclerSwipeAdapter<SwipeRecyclerAdapter.SimpleViewHolder> {
	private Context mContext;
	private List<CollectBean> collectBeanList;
	private AdapterView.OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	CallDeleteBack callDeleteBack = null;

	public interface CallDeleteBack {
		void onDeleteBtnclick(int position);
	}

	public void setOnDeleteClick(CallDeleteBack deleteClick) {
		callDeleteBack = deleteClick;
	}

	public SwipeRecyclerAdapter(Context context, List<CollectBean> collectBeanList) {
		mContext = context;
		this.collectBeanList = collectBeanList;
	}

	@Override public void onBindViewHolder(SimpleViewHolder viewHolder, final int position) {
		viewHolder.swipeTitle.setText(collectBeanList.get(position).getTitle());
		viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(null, null, position, 0);
			}
		});
		viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				callDeleteBack.onDeleteBtnclick(position);
			}
		});
	}

	@Override public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_listview_item, parent, false);
		return new SimpleViewHolder(view);
	}

	@Override public int getItemCount() {
		return collectBeanList.size();
	}

	@Override public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	public static class SimpleViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.swipe_item_layout) LinearLayout itemLayout;
		@Bind(R.id.collect_item_title) TextView swipeTitle;
		@Bind(R.id.delete_collect_item) Button buttonDelete;

		public SimpleViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
