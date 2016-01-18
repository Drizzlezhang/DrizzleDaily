package com.drizzle.drizzledaily.utils;

import de.greenrobot.event.EventBus;

/**
 * Created by drizzle on 16/1/17.
 */
public class FabEvent {
	private boolean showUpFab;

	public FabEvent(boolean showUpFab) {
		this.showUpFab = showUpFab;
	}

	public boolean getIsShowFab() {
		return showUpFab;
	}
}
