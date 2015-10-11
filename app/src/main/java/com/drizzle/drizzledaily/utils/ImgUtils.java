package com.drizzle.drizzledaily.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;

/**
 * 图片操作类
 */
public class ImgUtils {

    private ImgUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static void glidePic(String url,ImageView imageView,Context context){
        Glide.with(context)
                .load(url)
                .centerCrop()
                .error(R.mipmap.default_pic)
                .crossFade()
                .into(imageView);
    }
}
