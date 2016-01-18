package com.drizzle.drizzledaily.utils;

/**
 * Created by drizzle on 16/1/18.
 */
public class FabClickEvent {
	private int fragmentId;

	public FabClickEvent(int fragmentId) {
		this.fragmentId = fragmentId;
	}

	public int getFragmentId() {
		return fragmentId;
	}
}
