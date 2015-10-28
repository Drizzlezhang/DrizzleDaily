package com.drizzle.drizzledaily.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.MyUser;
import com.drizzle.drizzledaily.bean.ShareBean;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 个人管理
 */
public class UserActivity extends AppCompatActivity {

    @Bind(R.id.user_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.user_name)
    TextView userName;

    @Bind(R.id.user_sex)
    TextView userSex;

    @Bind(R.id.user_loginout_btn)
    Button userLoginout;

    @Bind(R.id.user_change_btn)
    Button userChange;

    @Bind(R.id.user_touxiang)
    CircleImageView userTouxiang;

    private int[] touxiangs = new int[]{R.mipmap.touxiang1, R.mipmap.touxiang2, R.mipmap.touxiang3, R.mipmap.touxiang4, R.mipmap.touxiang5, R.mipmap.touxiang6, R.mipmap.touxiang,R.mipmap.touxiang7,R.mipmap.touxiang8};
    private String[] superheros = new String[]{"SpiderMan", "IronMan", "Hulk", "SuperMan", "GreenArrow", "BatMan"};
    private CommonAdapter<ShareBean> adapter;
    private List<ShareBean> touxiangList = new ArrayList<>();
    private List<ShareBean> yuetouxianglist=new ArrayList<>();
    private CommonAdapter<ShareBean> yueadapter;
    private DialogPlus yuedialogPlus;
    private DialogPlus dialogPlus;
    private ProgressDialog progressDialog;

    private int loginType = 1;//登录状态，1为未登录，2为登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        initDatas();
        initViews();
    }

    @Override
    protected void onResume() {
        initUser();
        super.onResume();
    }

    private void initViews() {
        mToolbar.setTitle("个人管理");
        setSupportActionBar(mToolbar);
        userTouxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginType == 1) {
                    //TODO
                } else {
                    dialogPlus.show();
                }
            }
        });
        userTouxiang.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (loginType == 1) {
                    //TODO
                } else {
                    yuedialogPlus.show();
                }
                return true;
            }
        });
        dialogPlus = DialogPlus.newDialog(UserActivity.this)
                .setAdapter(adapter)
                .setGravity(Gravity.CENTER)
                .setContentHolder(new GridHolder(3))
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, final int position) {
                        MyUser newUser = new MyUser();
                        newUser.setTouxiangId(position);
                        MyUser myUser = BmobUser.getCurrentUser(UserActivity.this, MyUser.class);
                        newUser.update(UserActivity.this, myUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                userTouxiang.setImageResource(touxiangs[position]);
                                TUtils.showShort(UserActivity.this, "头像更新成功");
                                dialogPlus.dismiss();
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                TUtils.showShort(UserActivity.this, "头像更新失败");
                                dialogPlus.dismiss();
                            }
                        });
                    }
                })
                .setCancelable(true)
                .setPadding(20, 20, 20, 20)
                .create();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("请稍等...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);

    }

    @OnClick({R.id.user_change_btn, R.id.user_loginout_btn})
    public void userClick(View view) {
        switch (view.getId()) {
            case R.id.user_change_btn:
                if (loginType == 1) {
                    startActivity(new Intent(UserActivity.this, RegisterActivity.class));
                } else {
                    startActivity(new Intent(UserActivity.this, ChangePasswordActivity.class));
                }
                break;
            case R.id.user_loginout_btn:
                if (loginType == 1) {
                    startActivity(new Intent(UserActivity.this, LogininActivity.class));
                } else {
                    new MaterialDialog.Builder(UserActivity.this)
                            .title("登出账号？").content("点击确定登出您的账号。")
                            .positiveText("确定").negativeText("取消")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    if (!NetUtils.isConnected(UserActivity.this)) {
                                        TUtils.showShort(UserActivity.this, "网络未连接");
                                    } else {
                                        MyUser.logOut(UserActivity.this);
                                        finish();
                                    }
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                break;
            default:
                break;
        }
    }

    private void initDatas() {
        for (int i = 0; i < 6; i++) {
            touxiangList.add(new ShareBean(touxiangs[i], superheros[i]));
        }
        adapter = new CommonAdapter<ShareBean>(this, touxiangList, R.layout.choose_touxiang_item) {
            @Override
            public void convert(ViewHolder helper, ShareBean item) {
                helper.setText(R.id.choose_superhero, item.getText());
                helper.setImgByid(R.id.choose_touxiang, item.getImgId());
            }
        };
        ShareBean bean1 = new ShareBean(R.mipmap.touxiang7, "忘了这个人名了");
        ShareBean bean2 = new ShareBean(R.mipmap.touxiang8, "这个不认识");
        yuetouxianglist.add(bean1);
        yuetouxianglist.add(bean2);
        yueadapter = new CommonAdapter<ShareBean>(this, yuetouxianglist, R.layout.share_list_item) {
            @Override
            public void convert(ViewHolder helper, ShareBean item) {
                helper.setText(R.id.share_item_text, item.getText());
                helper.setImgByid(R.id.share_item_img, item.getImgId());
            }
        };
        yuedialogPlus = DialogPlus.newDialog(UserActivity.this)
                .setAdapter(yueadapter)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, final int position) {
                        MyUser newUser = new MyUser();
                        newUser.setTouxiangId(position+7);
                        MyUser myUser = BmobUser.getCurrentUser(UserActivity.this, MyUser.class);
                        newUser.update(UserActivity.this, myUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                userTouxiang.setImageResource(touxiangs[position+7]);
                                TUtils.showShort(UserActivity.this, "头像更新成功");
                                yuedialogPlus.dismiss();
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                TUtils.showShort(UserActivity.this, "头像更新失败");
                                yuedialogPlus.dismiss();
                            }
                        });
                        yuedialogPlus.dismiss();
                    }
                })
                .setCancelable(true).setPadding(20, 30, 20, 20).create();
    }

    private void initUser() {
        MyUser userInfo = BmobUser.getCurrentUser(this, MyUser.class);
        if (userInfo != null) {
            loginType = 2;
            userChange.setText("修改密码");
            userLoginout.setText("登     出");
            userName.setText(userInfo.getUsername());
            userTouxiang.setImageResource(touxiangs[userInfo.getTouxiangId()]);
            int sex = 0;
            sex = userInfo.getSex();
            if (sex == 1) {
                userSex.setText("男同志");
            } else {
                userSex.setText("女同志");
            }
        } else {
            loginType = 1;
            userChange.setText("注      册");
            userLoginout.setText("登      录");
        }
    }
}
