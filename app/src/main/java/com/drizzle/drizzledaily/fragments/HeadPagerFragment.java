package com.drizzle.drizzledaily.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.ReadActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 顶部viewpager嵌套的fragment
 */
public class HeadPagerFragment extends Fragment {

    private static String IMGKEY = "imgkey";
    private static String TITLEKEY = "titlekey";
    private static String IDKEY = "idkey";

    @Bind(R.id.head_fragment_img)
    ImageView headImg;

    @Bind(R.id.head_fragment_title)
    TextView headTitle;

    private String imgUrl;
    private String titleText;
    private int id;

    public HeadPagerFragment() {
    }

    /**
     * 获取单个fargment的标题，图片地址，文章id
     *
     * @param imageurl
     * @param text
     * @param pagerid
     * @return
     */
    public static HeadPagerFragment newInstance(String imageurl, String text, int pagerid) {
        Bundle args = new Bundle();
        args.putString(IMGKEY, imageurl);
        args.putString(TITLEKEY, text);
        args.putInt(IDKEY, pagerid);
        HeadPagerFragment headPagerFragment = new HeadPagerFragment();
        headPagerFragment.setArguments(args);
        return headPagerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            imgUrl = savedInstanceState.getString(IMGKEY);
            titleText = savedInstanceState.getString(TITLEKEY);
            id = savedInstanceState.getInt(IDKEY);
        } else {
            imgUrl = getArguments().getString(IMGKEY);
            titleText = getArguments().getString(TITLEKEY);
            id = getArguments().getInt(IDKEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.header_pager_fragment, container, false);
        ButterKnife.bind(this, view);
        headTitle.setText(titleText);
        Glide.with(getActivity()).load(imgUrl)
                .centerCrop().error(R.mipmap.place_img)
                .crossFade().into(headImg);
        headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReadActivity.class);
                intent.putExtra(Config.READID, id);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(IMGKEY, imgUrl);
        outState.putString(TITLEKEY, titleText);
        outState.putInt(IDKEY, id);
        super.onSaveInstanceState(outState);
    }
}
