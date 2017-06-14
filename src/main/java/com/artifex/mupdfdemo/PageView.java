package com.artifex.mupdfdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class PageView extends ViewGroup implements PageScrollListener {

	private static final int BACKGROUND_COLOR = 0xFFFFFFFF;
	private static final int PROGRESS_DIALOG_DELAY = 200;
	protected final Context   mContext;
	protected     int       mPageNumber;
	private       Point     mParentSize;
	protected     Point     mSize;   // Size of page at minimum zoom
	protected     float     mSourceScale;

	private       ImageView mEntire; // Image rendered at minimum zoom
	private AsyncTask<Void,Void,Bitmap> mDrawEntire;

	private       ProgressBar mBusyIndicator;
	private final Handler   mHandler = new Handler();
	//当前页面下标
	private TextView textView;

	public PageView(Context c, Point parentSize) {
		super(c);
		mContext    = c;
		mParentSize = parentSize;
		setBackgroundColor(BACKGROUND_COLOR);
	}

	protected abstract Bitmap drawPage(int sizeX, int sizeY, int patchX, int patchY, int patchWidth, int patchHeight);
	private void reinit() {
		// Cancel pending render task
		if (mDrawEntire != null) {
			mDrawEntire.cancel(true);
			mDrawEntire = null;
		}
		mPageNumber = 0;

		if (mSize == null)
			mSize = mParentSize;

		if (mEntire != null) {
			mEntire.setImageBitmap(null);
		}
	}

	public void releaseResources() {
		reinit();
		if (mBusyIndicator != null) {
			removeView(mBusyIndicator);
			mBusyIndicator = null;
		}
	}

	public void blank(int page){
		reinit();
		mPageNumber = page;
		if (mBusyIndicator == null) {
			mBusyIndicator = new ProgressBar(mContext);
			mBusyIndicator.setIndeterminate(true);
			addView(mBusyIndicator);
		}
		setBackgroundColor(BACKGROUND_COLOR);
	}

	public void setPage(int page, int pageSize, PointF size) {
		// Cancel pending render task
		if (mDrawEntire != null) {
			mDrawEntire.cancel(true);
			mDrawEntire = null;
		}

		mPageNumber = page;
		if (mEntire == null) {
			mEntire = new OpaqueImageView(mContext);
			mEntire.setScaleType(ImageView.ScaleType.FIT_CENTER);
			addView(mEntire);
		}

		if (textView == null) {
			textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_page_index, null);
			addView(textView);
		}
		textView.setText(++page + "/" + pageSize);
		// Calculate scaled size that fits within the screen limits
		// This is the size at minimum zoom
		mSourceScale = Math.min(mParentSize.x/size.x, mParentSize.y/size.y);
		mEntire.setImageBitmap(null);
		Point newSize = new Point((int)(size.x*mSourceScale), (int)(size.y*mSourceScale));
		mSize = newSize;

		// Render the page in the background
		mDrawEntire = new AsyncTask<Void,Void,Bitmap>() {

			protected Bitmap doInBackground(Void... v) {
			return drawPage(mSize.x, mSize.y, 0, 0, mSize.x, mSize.y);
		}

		protected void onPreExecute() {
			setBackgroundColor(BACKGROUND_COLOR);
			mEntire.setImageBitmap(null);

			if (mBusyIndicator == null) {
				mBusyIndicator = new ProgressBar(mContext);
				mBusyIndicator.setIndeterminate(true);
				//mBusyIndicator.setBackgroundResource(R.drawable.busy);
				addView(mBusyIndicator);
				mBusyIndicator.setVisibility(INVISIBLE);
				mHandler.postDelayed(new Runnable() {
					public void run() {
						if (mBusyIndicator != null)
							mBusyIndicator.setVisibility(VISIBLE);
					}
				}, PROGRESS_DIALOG_DELAY);
			}
		}

		protected void onPostExecute(Bitmap bm) {
			removeView(mBusyIndicator);
			mBusyIndicator = null;
			mEntire.setImageBitmap(bm);
			setBackgroundColor(Color.TRANSPARENT);
		}
	};

	mDrawEntire.execute();
	requestLayout();
	}

	@Override
	public void onScroll() {
		if (textView != null) {
			textView.clearAnimation();
		}
	}

	@Override
	public void onStop() {
		if (textView != null) {
			Animation an = AnimationUtils.loadAnimation(mContext, R.anim.index_out);
			an.setFillAfter(true);
			textView.startAnimation(an);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);// 测量子控件高和宽
		int x, y;
		switch(MeasureSpec.getMode(widthMeasureSpec)) {
		case MeasureSpec.UNSPECIFIED:
			x = mSize.x;
			break;
		default:
			x = MeasureSpec.getSize(widthMeasureSpec);
		}
		switch(MeasureSpec.getMode(heightMeasureSpec)) {
		case MeasureSpec.UNSPECIFIED:
			y = mSize.y;
			break;
		default:
			y = MeasureSpec.getSize(heightMeasureSpec);
		}

		setMeasuredDimension(x, y);

		if (mBusyIndicator != null) {
			int limit = Math.min(mParentSize.x, mParentSize.y)/2;
			mBusyIndicator.measure(MeasureSpec.AT_MOST | limit, MeasureSpec.AT_MOST | limit);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int w  = right-left;
		int h = bottom-top;

		if (mEntire != null) {
			mEntire.layout(0, 0, w, h);
		}

		if (textView != null) {
			// 需配合measureChildren使用
			textView.layout(10, 10, textView.getMeasuredWidth() + 10, textView.getMeasuredHeight() + 10);
		}

		if (mBusyIndicator != null) {
			int bw = mBusyIndicator.getMeasuredWidth();
			int bh = mBusyIndicator.getMeasuredHeight();
			mBusyIndicator.layout((w-bw)/2, (h-bh)/2, (w+bw)/2, (h+bh)/2);
		}
	}

	public int getPage() {
		return mPageNumber;
	}

	@Override
	public boolean isOpaque() {
		return true;
	}
}
