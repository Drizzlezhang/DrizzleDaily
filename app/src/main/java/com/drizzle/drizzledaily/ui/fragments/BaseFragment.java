package com.drizzle.drizzledaily.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import de.greenrobot.event.EventBus;

/**
 * Created by drizzle on 15/12/21.
 */
public class BaseFragment extends Fragment {

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
		Animation animation;
		if (enter) {
			animation =
				AnimationUtils.loadAnimation(getActivity(), android.support.design.R.anim.abc_grow_fade_in_from_bottom);
		} else {
			animation = AnimationUtils.loadAnimation(getActivity(),
				android.support.design.R.anim.abc_shrink_fade_out_from_bottom);
		}
		return animation;
	}

	@Override public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
