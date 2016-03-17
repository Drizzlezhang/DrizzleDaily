package com.drizzle.drizzledaily.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;
import com.afollestad.materialdialogs.MaterialDialog;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.OnStartDragListener;
import com.drizzle.drizzledaily.adapter.SimpleCallback;
import com.drizzle.drizzledaily.adapter.SwipeRecyclerAdapter;
import com.drizzle.drizzledaily.bean.CollectBean;
import com.drizzle.drizzledaily.bean.MyUser;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.activities.ReadActivity;
import com.drizzle.drizzledaily.ui.activities.SectionReadActivity;
import com.drizzle.drizzledaily.utils.FabClickEvent;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * 收藏夹列表
 */
public class CollectListFragment extends BaseFragment {
	@Bind(R.id.collect_listview) RecyclerView mRecyclerView;

	@Bind(R.id.upload_fab) FloatingActionButton uploadfloatingActionButton;

	@Bind(R.id.sync_fab) FloatingActionButton syncfloatingActionButton;

	@Bind(R.id.collect_center_text) TextView centerText;

	private List<CollectBean> collectBeanList = new ArrayList<CollectBean>();
	private SwipeRecyclerAdapter adapter;
	private ProgressDialog progressDialog;
	final Gson gson = new Gson();
	private ItemTouchHelper mItemTouchHelper;

	@Override public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_collect_list, container, false);
		ButterKnife.bind(this, view);
		initData();
		initViews();
		return view;
	}

	private void initData() {
		final SharedPreferences sharedPreferences =
			getActivity().getSharedPreferences(Config.CACHE_DATA, Activity.MODE_PRIVATE);
		String collectcache = sharedPreferences.getString(Config.COLLECTCACHE, "[]");
		List<CollectBean> collectBeans = gson.fromJson(collectcache, new TypeToken<List<CollectBean>>() {
		}.getType());
		collectBeanList.clear();
		collectBeanList.addAll(collectBeans);
		//为了实现拖动不再排序
		//	Collections.sort(collectBeanList);
		isTextVisible();
	}

	private Handler mHandler = new Handler() {
		@Override public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					isTextVisible();
					break;
				default:
					break;
			}
		}
	};

	private void initViews() {
		mRecyclerView.setItemAnimator(new FadeInLeftAnimator());
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecyclerView.addItemDecoration(
			new HorizontalDividerItemDecoration.Builder(getActivity()).size(1).color(Color.GRAY).build());
		adapter = new SwipeRecyclerAdapter(getActivity(), collectBeanList);
		adapter.setOnDeleteClick(new SwipeRecyclerAdapter.CallChangeBack() {
			@Override public void onDeleteBtnclick(int position) {
				adapter.notifyItemRemoved(position);
				collectBeanList.remove(new CollectBean(collectBeanList.get(position).getId(), "", 1, 0));
				PerferUtils.saveSth(Config.COLLECTCACHE, gson.toJson(collectBeanList));
				mHandler.sendEmptyMessageDelayed(1, 100);
			}

			@Override public void onItemMove(int from, int to) {
				Collections.swap(collectBeanList, from, to);
				adapter.notifyItemMoved(from, to);
				PerferUtils.saveSth(Config.COLLECTCACHE, gson.toJson(collectBeanList));
				mHandler.sendEmptyMessageDelayed(1, 100);
			}
		});
		//根据收藏文章的type决定跳转的阅读页面activity
		adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CollectBean bean = collectBeanList.get(position);
				int type = bean.getType();
				if (type == 1) {
					Intent intent = new Intent(getActivity(), ReadActivity.class);
					intent.putExtra(Config.READID, bean.getId());
					startActivity(intent);
				} else if (type == 2) {
					Intent intent = new Intent(getActivity(), SectionReadActivity.class);
					intent.putExtra(Config.READID, bean.getId());
					startActivity(intent);
				} else {
					TUtils.showShort(getActivity(), "error");
				}
			}
		});
		mRecyclerView.setAdapter(adapter);
		ItemTouchHelper.Callback callback = new SimpleCallback(adapter);
		mItemTouchHelper = new ItemTouchHelper(callback);
		mItemTouchHelper.attachToRecyclerView(mRecyclerView);
		adapter.setOnStartDragListener(new OnStartDragListener() {
			@Override public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
				mItemTouchHelper.startDrag(viewHolder);
			}
		});
		isTextVisible();
		uploadfloatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				uploadCollects();
			}
		});
		syncfloatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				syncCollects();
			}
		});
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage("请稍等...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(true);
	}

	/**
	 * 将本地收藏夹上传到云,并覆盖
	 */
	private void uploadCollects() {
		final MyUser myUser = BmobUser.getCurrentUser(getActivity(), MyUser.class);
		if (myUser == null) {
			TUtils.showShort(getActivity(), "未登录");
		} else {
			new MaterialDialog.Builder(getActivity()).title("上传收藏夹？")
				.content("点击确定将上传本地收藏到云。")
				.positiveText("确定")
				.negativeText("取消")
				.callback(new MaterialDialog.ButtonCallback() {
					@Override public void onPositive(final MaterialDialog dialog) {
						progressDialog.show();
						final SharedPreferences sharedPreferences =
							getActivity().getSharedPreferences(Config.CACHE_DATA, Activity.MODE_PRIVATE);
						String collectcache = sharedPreferences.getString(Config.COLLECTCACHE, "[]");
						MyUser newUser = new MyUser();
						newUser.setCollectJson(collectcache);
						newUser.update(getActivity(), myUser.getObjectId(), new UpdateListener() {
							@Override public void onSuccess() {
								// TODO Auto-generated method stub
								TUtils.showShort(getActivity(), "收藏夹上传成功");
								progressDialog.dismiss();
							}

							@Override public void onFailure(int code, String msg) {
								// TODO Auto-generated method stub
								TUtils.showShort(getActivity(), "收藏夹上传失败");
								progressDialog.dismiss();
							}
						});
					}

					@Override public void onNegative(MaterialDialog dialog) {
						super.onNegative(dialog);
					}
				})
				.show();
		}
	}

	/**
	 * 同步网络数据及本地数据,使用set集合合并数据,重写equals和hashcode方法,避免数据重复
	 */
	private void syncCollects() {
		final MyUser myUser = BmobUser.getCurrentUser(getActivity(), MyUser.class);
		if (myUser == null) {
			TUtils.showShort(getActivity(), "未登录");
		} else {
			new MaterialDialog.Builder(getActivity()).title("同步收藏夹？")
				.content("点击确定将同步云到本地。")
				.positiveText("确定")
				.negativeText("取消")
				.callback(new MaterialDialog.ButtonCallback() {
					@Override public void onPositive(final MaterialDialog dialog) {
						progressDialog.show();
						//获取本地数据
						SharedPreferences sharedPreferences =
							getActivity().getSharedPreferences(Config.CACHE_DATA, Activity.MODE_PRIVATE);
						String collectcache = sharedPreferences.getString(Config.COLLECTCACHE, "[]");
						Gson gson = new Gson();
						Set<CollectBean> oldcollectset = new HashSet<CollectBean>();
						oldcollectset = gson.fromJson(collectcache, new TypeToken<Set<CollectBean>>() {
						}.getType());
						//获取网络数据
						String cloudcollect = myUser.getCollectJson();
						Set<CollectBean> cloudcollectset = new HashSet<CollectBean>();
						cloudcollectset = gson.fromJson(cloudcollect, new TypeToken<Set<CollectBean>>() {
						}.getType());
						//数据合并转换为json存储到本地
						cloudcollectset.addAll(oldcollectset);
						PerferUtils.saveSth(Config.COLLECTCACHE, gson.toJson(cloudcollectset));
						adapter.notifyDataSetChanged();
						TUtils.showShort(getActivity(), "同步成功");
						progressDialog.dismiss();
						isTextVisible();
					}

					@Override public void onNegative(MaterialDialog dialog) {
						super.onNegative(dialog);
					}
				})
				.show();
		}
	}

	/**
	 * fragment在show和hide时调用的方法
	 */
	@Override public void onHiddenChanged(boolean hidden) {
		if (hidden == false) {
			initData();
			adapter.notifyDataSetChanged();
			isTextVisible();
		}
		super.onHiddenChanged(hidden);
	}

	/**
	 * 根据现有收藏列表长度判断是否显示背景上的字
	 */
	private void isTextVisible() {
		int size = collectBeanList.size();
		if (size == 0) {
			centerText.setVisibility(View.VISIBLE);
		} else {
			centerText.setVisibility(View.GONE);
		}
	}

	public void onEvent(FabClickEvent event) {
		//TODO
	}
}
