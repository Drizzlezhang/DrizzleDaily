package com.drizzle.drizzledaily.wxapi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.utils.TUtils;
import com.drizzle.drizzledaily.utils.ThemeUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by user on 2015/10/14.
 */
public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(Config.SKIN_NUMBER, Activity.MODE_PRIVATE);
        int themeid = preferences.getInt(Config.SKIN_NUMBER, 0);
        ThemeUtils.onActivityCreateSetTheme(this, themeid);
        api = WXAPIFactory.createWXAPI(this, "wxcdfd8ea3dceaf767", false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq arg0) {
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                TUtils.showShort(this, "success");
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                TUtils.showShort(this, "cancel");
                //分享取消
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                TUtils.showShort(this, "refuse");
                //分享拒绝
                finish();
                break;
            default:
                break;
        }
    }


}
