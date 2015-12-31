package com.drizzle.drizzledaily.ui.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.model.Config;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by drizzle on 15/12/23.
 */
public class BaseActivity extends AppCompatActivity {
	private IWXAPI wxApi;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wxApi = WXAPIFactory.createWXAPI(this, Config.WXAPPID);
		wxApi.registerApp(Config.WXAPPID);
	}

	/**
	 * 微信分享,分享内容
	 */
	public void wechatShare(int flag, String shareTitle, String shareUrl, Bitmap sBitmap) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = shareUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = shareTitle;
		msg.description = "来自知乎日报 By Drizzle";
		if (sBitmap == null) {
			sBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.labal_icon);
		}
		msg.setThumbImage(sBitmap);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}

	/**
	 * 微信分享,分享应用
	 *
	 * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
	 */
	public void wechatShare(int flag) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "http://fir.im/w7g1";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "知乎日报By Drizzle";
		msg.description = "from fir.im";
		//这里替换一张自己工程里的图片资源
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.labal_icon);
		msg.setThumbImage(thumb);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}
}
