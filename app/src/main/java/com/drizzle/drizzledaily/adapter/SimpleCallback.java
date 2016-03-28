package com.drizzle.drizzledaily.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by drizzle on 16/3/17.
 */
public class SimpleCallback extends ItemTouchHelper.Callback {
	private final ItemTouchHelperAdapter mItemTouchHelperAdapter;

	public SimpleCallback(ItemTouchHelperAdapter itemTouchHelperAdapter) {
		mItemTouchHelperAdapter = itemTouchHelperAdapter;
	}

	@Override public boolean isLongPressDragEnabled() {
		return true;
	}

	@Override public boolean isItemViewSwipeEnabled() {
		return true;
	}

	@Override public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
		final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
		return makeMovementFlags(dragFlags, swipeFlags);
	}

	@Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
		RecyclerView.ViewHolder target) {
		if (viewHolder.getItemViewType() != target.getItemViewType()) {
			return false;
		}
		mItemTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
		return true;
	}

	@Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
	}

	@Override public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
		if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
			if (viewHolder instanceof ItemTouchHelperViewHolder) {
				ItemTouchHelperViewHolder viewHolder1 = (ItemTouchHelperViewHolder) viewHolder;
				viewHolder1.onItemSelected();
			}
		}
		super.onSelectedChanged(viewHolder, actionState);
	}

	@Override public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		super.clearView(recyclerView, viewHolder);
		if (viewHolder instanceof ItemTouchHelperViewHolder) {
			ItemTouchHelperViewHolder viewHolder1 = (ItemTouchHelperViewHolder) viewHolder;
			viewHolder1.onItemClear();
		}
	}
}
