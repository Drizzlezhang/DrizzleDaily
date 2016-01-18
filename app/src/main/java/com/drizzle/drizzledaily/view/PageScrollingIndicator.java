package com.drizzle.drizzledaily.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.drizzle.drizzledaily.R;

/**
 * Created by davinci42 on 2016/1/11.
 */
public class PageScrollingIndicator extends View {

	private Paint mTransPaint;
	private Paint mWhitePaint;
	int distance = 200;
	int radius = 30;
	int count = 5;
	int mCurrentIndex = 0;
	int mNewIndex = 0;
	float mPageOffset;
	int dotColor;

	float oldX;
	float newX;

	public PageScrollingIndicator(Context context) {
		this(context, null);
	}

	public PageScrollingIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PageScrollingIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray array =
			context.getTheme().obtainStyledAttributes(attrs, R.styleable.PageScrollingIndicator, defStyleAttr, 0);
		radius = array.getInt(R.styleable.PageScrollingIndicator_radius, 30);
		distance = array.getDimensionPixelSize(R.styleable.PageScrollingIndicator_distance,
			(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()));
		dotColor = array.getColor(R.styleable.AVLoadingIndicatorView_indicator_color, Color.WHITE);
		array.recycle();
		init();
	}

	public void setUpWithViewPager(ViewPager viewPager) {

		if (viewPager != null) {

			viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
					mNewIndex = position;
					mPageOffset = positionOffset;
					postInvalidate();
				}

				@Override public void onPageSelected(int position) {
				}

				@Override public void onPageScrollStateChanged(int state) {
				}
			});

			if (viewPager.getAdapter() != null) {
				count = viewPager.getAdapter().getCount();
			}
		}
	}

	private void init() {
		mTransPaint = new Paint();
		mTransPaint.setStyle(Paint.Style.FILL);
		mTransPaint.setAntiAlias(true);
		mTransPaint.setAlpha(30);

		mWhitePaint = new Paint();
		mWhitePaint.setStyle(Paint.Style.FILL);
		mWhitePaint.setAntiAlias(true);
		mWhitePaint.setColor(Color.WHITE);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = 2 * distance + radius * 2;
		int height = radius * 2;
		setMeasuredDimension(width, height);
	}

	@Override protected void onDraw(Canvas canvas) {

		for (int i = 0; i < count; i++) {
			canvas.drawCircle(radius + i * distance, radius, radius, mTransPaint);
		}

		float currentXCenter = radius + mCurrentIndex * distance;

		if (mPageOffset != 0) {
			if (mCurrentIndex == mNewIndex) {
				// sliding right
				oldX = currentXCenter + distance * (float) Math.pow((mPageOffset * 2), 3) / 8;
				newX = currentXCenter + distance * mPageOffset;
			} else {
				// sliding left
				oldX = currentXCenter - distance * (float) Math.pow(((1 - mPageOffset) * 2), 3) / 8;
				newX = currentXCenter - distance * (1 - mPageOffset);
			}
			canvas.drawCircle(oldX, radius, radius, mWhitePaint);
			canvas.drawCircle(newX, radius, radius, mWhitePaint);
			canvas.drawRect(Math.min(oldX, newX), 0, Math.max(oldX, newX), radius * 2, mWhitePaint);
		} else {
			// stil
			mCurrentIndex = mNewIndex;
			canvas.drawCircle(radius + mCurrentIndex * distance, radius, radius, mWhitePaint);
		}
	}
}
