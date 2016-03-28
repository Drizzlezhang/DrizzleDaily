package com.drizzle.drizzledaily.adapter;

import android.content.Context;
import android.graphics.Color;
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
public class SwipeRecyclerAdapter extends RecyclerSwipeAdapter<SwipeRecyclerAdapter.SimpleViewHolder>
	implements ItemTouchHelperAdapter {
	private List<CollectBean> collectBeanList;
	private AdapterView.OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	public void setOnStartDragListener(OnStartDragListener onStartDragListener) {
		OnStartDragListener onStartDragListener1 = onStartDragListener;
	}

	CallChangeBack mCallChangeBack = null;

	public interface CallChangeBack {
		void onDeleteBtnclick(int position);

		void onItemMove(int from, int to);
	}

	public void setOnDeleteClick(CallChangeBack callChangeBack) {
		mCallChangeBack = callChangeBack;
	}

	public SwipeRecyclerAdapter(Context context, List<CollectBean> collectBeanList) {
		Context context1 = context;
		this.collectBeanList = collectBeanList;
	}

	@Override public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
		viewHolder.swipeTitle.setText(collectBeanList.get(position).getTitle());
		viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(null, null, position, 0);
			}
		});
		//viewHolder.itemLayout.setOnTouchListener(new View.OnTouchListener() {
		//	@Override public boolean onTouch(View v, MotionEvent event) {
		//		if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
		//			mOnStartDragListener.onStartDrag(viewHolder);
		//		}
		//		return false;
		//	}
		//});
		viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mCallChangeBack.onDeleteBtnclick(position);
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

	@Override public boolean onItemMove(int from, int to) {
		mCallChangeBack.onItemMove(from, to);
		return true;
	}

	public static class SimpleViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
		@Bind(R.id.swipe_item_layout) LinearLayout itemLayout;
		@Bind(R.id.collect_item_title) TextView swipeTitle;
		@Bind(R.id.delete_collect_item) Button buttonDelete;

		public SimpleViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		@Override public void onItemClear() {
			itemLayout.setBackgroundColor(0);
		}

		@Override public void onItemSelected() {
			itemLayout.setBackgroundColor(Color.LTGRAY);
		}
	}
}
