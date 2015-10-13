package com.drizzle.drizzledaily.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.SwipeAdapter;
import com.drizzle.drizzledaily.bean.CollectBean;
import com.drizzle.drizzledaily.db.CollectDB;
import com.drizzle.drizzledaily.ui.ReadActivity;
import com.drizzle.drizzledaily.ui.SectionReadActivity;
import com.drizzle.drizzledaily.utils.TUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 收藏夹列表
 */
public class CollectListFragment extends android.support.v4.app.Fragment {
    @Bind(R.id.collect_listview)
    ListView listView;

    private CollectDB collectDB;
    private List<CollectBean> collectBeanList;
    private SwipeAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    collectBeanList.clear();
                    collectBeanList = collectDB.findCollects();
                    adapter = new SwipeAdapter(getActivity(), collectBeanList);
                    adapter.setOnDeleteClick(new SwipeAdapter.CallDeleteBack() {
                        @Override
                        public void onDeleteBtnclick(int pageid) {
                            collectDB.deleteCollect(pageid);
                            handler.sendEmptyMessage(1);
                        }
                    });
                    listView.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };


    public CollectListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collect_list, container, false);
        ButterKnife.bind(this, view);
        collectDB = CollectDB.getInstance(getActivity());
        collectBeanList = collectDB.findCollects();
        handler.sendEmptyMessage(1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CollectBean bean = collectBeanList.get(position);
                int type = bean.getType();
                if (type == 1) {
                    Intent intent = new Intent(getActivity(), ReadActivity.class);
                    intent.putExtra("readid", bean.getId());
                    startActivity(intent);
                } else if (type == 2) {
                    Intent intent = new Intent(getActivity(), SectionReadActivity.class);
                    intent.putExtra("readid", bean.getId());
                    startActivity(intent);
                } else {
                    TUtils.showShort(getActivity(), "error");
                }
            }
        });
        return view;
    }
}
