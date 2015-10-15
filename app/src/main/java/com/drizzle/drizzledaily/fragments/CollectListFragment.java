package com.drizzle.drizzledaily.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.SwipeAdapter;
import com.drizzle.drizzledaily.bean.CollectBean;
import com.drizzle.drizzledaily.db.CollectDB;
import com.drizzle.drizzledaily.ui.MainActivity;
import com.drizzle.drizzledaily.ui.ReadActivity;
import com.drizzle.drizzledaily.ui.SectionReadActivity;
import com.drizzle.drizzledaily.utils.TUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 收藏夹列表
 */
public class CollectListFragment extends android.support.v4.app.Fragment implements MainActivity.OnToolbarCilckListener{
    @Bind(R.id.collect_listview)
    ListView listView;

    @Bind(R.id.collect_fab)
    FloatingActionButton floatingActionButton;

    @Bind(R.id.collect_center_text)
    TextView centerText;

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
                    isTextVisible();
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
        ((MainActivity)getActivity()).setToolbarClick(this);
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
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title("删除整个收藏夹？")
                        .content("点击确定将将删除您收藏的所有文章。")
                        .positiveText("确定")
                        .negativeText("取消")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                collectDB.wipeCollect();
                                handler.sendEmptyMessage(1);
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                            }
                        })
                        .show();
            }
        });
        return view;
    }

    @Override
    public void onClickToolbar() {
        listView.smoothScrollToPosition(0);
    }

    private void isTextVisible() {
        int size = collectBeanList.size();
        if (size == 0) {
            centerText.setVisibility(View.VISIBLE);
        } else {
            centerText.setVisibility(View.GONE);
        }
    }
}
