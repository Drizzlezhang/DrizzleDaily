package com.drizzle.drizzledaily.ui.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.DatePicker;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.MyUser;
import com.drizzle.drizzledaily.bean.ShareBean;
import com.drizzle.drizzledaily.ui.UserActivity;
import com.drizzle.drizzledaily.ui.fragments.AboutDeveloperFragment;
import com.drizzle.drizzledaily.ui.fragments.CollectListFragment;
import com.drizzle.drizzledaily.ui.fragments.HotListFragment;
import com.drizzle.drizzledaily.ui.fragments.LatestListFragment;
import com.drizzle.drizzledaily.ui.fragments.SearchFragment;
import com.drizzle.drizzledaily.ui.fragments.SectionsListFragment;
import com.drizzle.drizzledaily.ui.fragments.ThemeListFragment;
import com.drizzle.drizzledaily.utils.DateUtils;
import com.drizzle.drizzledaily.utils.FabClickEvent;
import com.drizzle.drizzledaily.utils.FabEvent;
import com.drizzle.drizzledaily.utils.PermissionUtil;
import com.drizzle.drizzledaily.utils.TUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 主界面，管理多个列表fragment
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
	@Bind(R.id.main_toolbar) Toolbar mToolbar;

	@Bind(R.id.main_drawer_layout) DrawerLayout mDrawerLayout;

	@Bind(R.id.main_nav_view) NavigationView navigationView;

	@Bind(R.id.up_fab) FloatingActionButton upFab;

	private TextView nameText;
	private CircleImageView mainTouxiang;
	private static boolean isFabShowing = false;

	private DialogPlus dialogPlus;
	private CommonAdapter<ShareBean> adapter;
	private List<ShareBean> shareBeanList = new ArrayList<>();
	private int[] touxiangs = new int[] {
		R.mipmap.touxiang1, R.mipmap.touxiang2, R.mipmap.touxiang3, R.mipmap.touxiang4, R.mipmap.touxiang5,
		R.mipmap.touxiang6, R.mipmap.touxiang, R.mipmap.touxiang7, R.mipmap.touxiang8
	};

	private Calendar calendar;
	//主activity的fragmentid，默认为首页，1
	private int fragmentID = 1;

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private LatestListFragment latestListFragment;
	private HotListFragment hotListFragment;
	private ThemeListFragment themeListFragment;
	private SectionsListFragment sectionsListFragment;
	private CollectListFragment collectListFragment;
	private SearchFragment searchFragment;
	private AboutDeveloperFragment developerFragment;

	private static String[] PERMISSION_LIST = new String[] {
		Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS,
		Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	private static final int PERMISSION_REQUEST = 1;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		EventBus.getDefault().register(this);
		initData();
		initViews();
		BmobUpdateAgent.update(this);
		calendar = Calendar.getInstance();
		fragmentManager = getSupportFragmentManager();
		latestListFragment = new LatestListFragment();
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.main_frg_container, latestListFragment, 1 + "").commit();
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
				!= PackageManager.PERMISSION_GRANTED
				|| ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
				!= PackageManager.PERMISSION_GRANTED
				|| ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED
				|| ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
				showRuquestDialog();
			}
		}
	}

	private void initData() {
		ShareBean bean1 = new ShareBean(R.mipmap.frends, "朋友圈");
		ShareBean bean2 = new ShareBean(R.mipmap.weixin, "微信好友");
		shareBeanList.add(bean1);
		shareBeanList.add(bean2);
		adapter = new CommonAdapter<ShareBean>(this, shareBeanList, R.layout.share_list_item) {
			@Override public void convert(ViewHolder helper, ShareBean item) {
				helper.setText(R.id.share_item_text, item.getText());
				helper.setImgByid(R.id.share_item_img, item.getImgId());
			}
		};
	}

	private void initViews() {
		mToolbar.setTitle("知乎日报");
		setSupportActionBar(mToolbar);
		////获取控件的高度
		//int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		//int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		//mToolbar.measure(w, h);
		//int height = mToolbar.getMeasuredHeight();
		//int width = mToolbar.getMeasuredWidth();
		//mToolbar.setTranslationY(-height);
		//mToolbar.animate().translationY(0).setDuration(600).setStartDelay(600);
		nameText = (TextView) findViewById(R.id.drawer_name1);
		mainTouxiang = (CircleImageView) findViewById(R.id.drawer_touxiang1);
		mainTouxiang.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mDrawerLayout.closeDrawer(GravityCompat.START);
				startActivity(new Intent(MainActivity.this, UserActivity.class));
			}
		});
		ActionBarDrawerToggle toggle =
			new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open,
				R.string.navigation_drawer_close);
		mDrawerLayout.setDrawerListener(toggle);
		toggle.syncState();
		navigationView.setNavigationItemSelectedListener(this);
		dialogPlus = DialogPlus.newDialog(MainActivity.this)
			.setAdapter(adapter)
			.setHeader(R.layout.share_head)
			.setOnItemClickListener(new OnItemClickListener() {
				@Override public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
					if (position == 1) {
						wechatShare(1);//分享到朋友圈
					} else if (position == 2) {
						wechatShare(0); //分享给微信好友
					}
					dialogPlus.dismiss();
				}
			})
			.setCancelable(true)
			.setPadding(20, 30, 20, 20)
			.create();
		upFab.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				EventBus.getDefault().post(new FabClickEvent(fragmentID));
			}
		});
		upFab.setScaleX(0f);
		upFab.setScaleY(0f);
	}

	public void showUpFab() {
		if (isFabShowing) {
			//TODO
		} else {
			isFabShowing = true;
			upFab.animate()
				.scaleX(1f)
				.scaleY(1f)
				.setDuration(200)
				.setInterpolator(new AccelerateInterpolator())
				.setStartDelay(100)
				.start();
		}
	}

	public void hideUpFab() {
		if (!isFabShowing) {
			//TODO
		} else {
			isFabShowing = false;
			upFab.animate()
				.scaleX(0f)
				.scaleY(0f)
				.setDuration(200)
				.setInterpolator(new AccelerateInterpolator())
				.setStartDelay(100)
				.start();
		}
	}

	/**
	 * 拿到缓存的用户信息
	 */
	private void initUser() {
		MyUser userInfo = BmobUser.getCurrentUser(this, MyUser.class);
		if (userInfo != null) {
			nameText.setText(userInfo.getUsername());
			mainTouxiang.setImageResource(touxiangs[userInfo.getTouxiangId()]);
		} else {
			nameText.setText("未登录");
			mainTouxiang.setImageResource(touxiangs[6]);
		}
	}

	@Override public boolean onNavigationItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.drawer_menu_home:
				if (fragmentID == 1) {
					//TODO
				} else {
					if (latestListFragment == null) {
						latestListFragment = new LatestListFragment();
					}
					hideFragment(fragmentID);
					if (latestListFragment.isAdded()) {
						fragmentTransaction.show(getSupportFragmentManager().findFragmentByTag(1 + "")).commit();
					} else {
						fragmentTransaction.add(R.id.main_frg_container, latestListFragment, 1 + "").commit();
					}
					mToolbar.setTitle("知乎日报");
					fragmentID = 1;
					showUpFab();
				}
				break;
			case R.id.drawer_menu_hot:
				if (fragmentID == 2) {
					//TODO
				} else {
					if (hotListFragment == null) {
						hotListFragment = new HotListFragment();
					}
					hideFragment(fragmentID);
					if (hotListFragment.isAdded()) {
						fragmentTransaction.show(getSupportFragmentManager().findFragmentByTag(2 + "")).commit();
					} else {
						fragmentTransaction.add(R.id.main_frg_container, hotListFragment, 2 + "").commit();
					}
					mToolbar.setTitle("大家都在看");
					fragmentID = 2;
					showUpFab();
				}
				break;
			case R.id.drawer_menu_theme:
				if (fragmentID == 3) {
					//TODO
				} else {
					if (themeListFragment == null) {
						themeListFragment = new ThemeListFragment();
					}
					hideFragment(fragmentID);
					if (themeListFragment.isAdded()) {
						fragmentTransaction.show(getSupportFragmentManager().findFragmentByTag(3 + "")).commit();
					} else {
						fragmentTransaction.add(R.id.main_frg_container, themeListFragment, 3 + "").commit();
					}
					mToolbar.setTitle("主题日报");
					fragmentID = 3;
					showUpFab();
				}
				break;
			case R.id.drawer_menu_section:
				if (fragmentID == 4) {
					//TODO
				} else {
					if (sectionsListFragment == null) {
						sectionsListFragment = new SectionsListFragment();
					}
					hideFragment(fragmentID);
					if (sectionsListFragment.isAdded()) {
						fragmentTransaction.show(getSupportFragmentManager().findFragmentByTag(4 + "")).commit();
					} else {
						fragmentTransaction.add(R.id.main_frg_container, sectionsListFragment, 4 + "").commit();
					}
					mToolbar.setTitle("专栏");
					fragmentID = 4;
					showUpFab();
				}
				break;
			case R.id.drawer_menu_like:
				hideUpFab();
				if (fragmentID == 5) {
					//TODO
				} else {
					if (collectListFragment == null) {
						collectListFragment = new CollectListFragment();
					}
					hideFragment(fragmentID);
					if (collectListFragment.isAdded()) {
						fragmentTransaction.show(getSupportFragmentManager().findFragmentByTag(5 + "")).commit();
					} else {
						fragmentTransaction.add(R.id.main_frg_container, collectListFragment, 5 + "").commit();
					}
					mToolbar.setTitle("我的收藏");
					fragmentID = 5;
				}
				break;
			case R.id.drawer_menu_choose:
				DatePickerDialog datePickerDialog =
					new DatePickerDialog(MainActivity.this, dateSetListener, calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.setCancelable(true);
				datePickerDialog.setTitle("请选择日报日期");
				datePickerDialog.setCanceledOnTouchOutside(true);
				datePickerDialog.show();
				break;
			case R.id.drawer_menu_drizzle:
				hideUpFab();
				if (fragmentID == 7) {
					//TODO
				} else {
					if (developerFragment == null) {
						developerFragment = new AboutDeveloperFragment();
					}
					hideFragment(fragmentID);
					if (developerFragment.isAdded()) {
						fragmentTransaction.show(getSupportFragmentManager().findFragmentByTag(7 + "")).commit();
					} else {
						fragmentTransaction.add(R.id.main_frg_container, developerFragment, 7 + "").commit();
					}
					mToolbar.setTitle("关于开发者");
					fragmentID = 7;
				}
				break;
			default:
				break;
		}
		mDrawerLayout.closeDrawer(GravityCompat.START);
		return true;
	}

	/**
	 * 重写save方法,不保存fragment状态
	 */
	@Override public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
	}

	/**
	 * 根据fragment tag隐藏对应的fragment
	 */
	private void hideFragment(int frgid) {
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.hide(getSupportFragmentManager().findFragmentByTag(String.valueOf(frgid)));
	}

	/**
	 * 根据选择的日期获取当天的日报内容
	 */
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			Calendar c = DateUtils.setCalendar(year, monthOfYear + 1, dayOfMonth);
			String titleTime = DateUtils.printDate(c);
			c = DateUtils.getAfterDay(c);
			String time = DateUtils.printCalendar(c);
			searchFragment = SearchFragment.newInstance(time);
			hideFragment(fragmentID);
			if (searchFragment.isAdded()) {
				fragmentTransaction.show(getSupportFragmentManager().findFragmentByTag(6 + "")).commit();
			} else {
				fragmentTransaction.add(R.id.main_frg_container, searchFragment, 6 + "").commit();
			}
			mToolbar.setTitle(titleTime);
			fragmentID = 6;
			showUpFab();
		}
	};

	private static boolean isExit = false;
	Handler mHandler = new Handler() {

		@Override public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	@Override public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 按退出先关闭侧边栏,再关分享dialog,再回到主fragment,再按两次退出
	 */
	private void exit() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else if (dialogPlus.isShowing()) {
			dialogPlus.dismiss();
		} else if (fragmentID != 1) {
			showUpFab();
			if (latestListFragment == null) {
				latestListFragment = new LatestListFragment();
			}
			hideFragment(fragmentID);
			if (latestListFragment.isAdded()) {
				fragmentTransaction.show(getSupportFragmentManager().findFragmentByTag(1 + "")).commit();
			} else {
				fragmentTransaction.add(R.id.main_frg_container, latestListFragment, 1 + "").commit();
			}
			mToolbar.setTitle("知乎日报");
			fragmentID = 1;
		} else if (!isExit) {
			isExit = true;
			TUtils.showShort(MainActivity.this, "再按一次退出~");
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}

	@Override protected void onResume() {
		initUser();
		super.onResume();
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEvent(FabEvent fabEvent) {
		boolean flag = fabEvent.getIsShowFab();
		if (flag) {
			showUpFab();
		} else {
			hideUpFab();
		}
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.action_share:
				dialogPlus.show();
				break;
			case R.id.action_settings:
				startActivity(new Intent(MainActivity.this, SettingsActivity.class));
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 请求权限
	 */
	private void showRuquestDialog() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
			|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
			|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
			|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			Snackbar.make(mToolbar, "运行程序需要部分权限,是否授权", Snackbar.LENGTH_LONG)
				.setAction("OK", new View.OnClickListener() {
					@Override public void onClick(View v) {
						ActivityCompat.requestPermissions(MainActivity.this, PERMISSION_LIST, PERMISSION_REQUEST);
					}
				})
				.show();
		} else {
			ActivityCompat.requestPermissions(this, PERMISSION_LIST, PERMISSION_REQUEST);
		}
	}

	@Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST) {
			if (PermissionUtil.verifyPermissions(grantResults)) {
				TUtils.showShort(MainActivity.this, "已授权");
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
