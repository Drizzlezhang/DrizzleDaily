package com.drizzle.drizzledaily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.CollectBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/10/13.
 */
public class SwipeAdapter extends BaseSwipeAdapter {

    private List<CollectBean> collectBeanList = new ArrayList<>();
    private Context mContext;
    CallDeleteBack callDeleteBack = null;

    public interface CallDeleteBack {
        public void onDeleteBtnclick(int pageid);
    }

    public void setOnDeleteClick(CallDeleteBack deleteClick) {
        callDeleteBack = deleteClick;
    }

    public SwipeAdapter(Context mContext, List<CollectBean> collectBeanList) {
        this.mContext = mContext;
        this.collectBeanList = collectBeanList;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView t = (TextView) convertView.findViewById(R.id.collect_item_title);
        t.setText(collectBeanList.get(position).getTitle());
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.swipe_listview_item, null);
        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        v.findViewById(R.id.delete_collect_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 callDeleteBack.onDeleteBtnclick(collectBeanList.get(position).getId());
            }
        });
        return v;
    }

    @Override
    public int getCount() {
        return collectBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return collectBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
