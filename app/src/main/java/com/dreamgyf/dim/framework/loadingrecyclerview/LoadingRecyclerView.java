package com.dreamgyf.dim.framework.loadingrecyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.dreamgyf.dim.R;

public class LoadingRecyclerView extends RecyclerView {

	static class LayoutManagerType {

		final static int LINEAR_LAYOUT = 0;
		final static int GRID_LAYOUT = 1;
		final static int STAGGERED_LAYOUT = 2;
	}

	public static class Direction {

		public final static int START = 0x01;
		public final static int END = 0x02;
		public final static int BOTH = 0x03;
	}

	private Context mContext;

	private boolean mCanLoad;

	private int mDirection;

	private boolean isLoading = false;

	private CircleImageView mCircleView;

	private CircularProgressDrawable mProgress;

	private RecyclerView.LayoutManager mLayoutManager;

	private int mLayoutManagerType;

	private int mOrientation;

	private RecyclerView.OnScrollListener mOnLoadMoreListener;

	private LoadingListener mLoadingListener;

	public interface LoadingListener {

		void onLoadMore(int direction, int offset);
	}

	public void setLoadingListener(LoadingListener listener) {
		mLoadingListener = listener;
	}

	public LoadingRecyclerView(@NonNull Context context) {
		super(context);
		init(context, null);
	}

	public LoadingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		mContext = context;
		initAttrs(attrs);
		initLoadListener();
		initProgressView();
		initView();
		initAnimator();
	}

	/**
	 * 初始化参数
	 *
	 * @param attrs
	 */
	private void initAttrs(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.LoadingRecyclerView, 0, 0);
			mCanLoad = typedArray.getBoolean(R.styleable.LoadingRecyclerView_enableLoad, true);
			mDirection = typedArray.getInt(R.styleable.LoadingRecyclerView_direction, Direction.END);
			typedArray.recycle();
		} else {
			enableLoad();
			mDirection = Direction.END;
		}
	}

	public void enableLoad() {
		mCanLoad = true;
	}

	public void disableLoad() {
		mCanLoad = false;
	}

	private void initLoadListener() {
		mOnLoadMoreListener = new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (mCanLoad) {
					if ((mDirection == Direction.START || mDirection == Direction.BOTH) && !isLoading && mLayoutManager != null && isScrollToStart()) {
						if (mLoadingListener != null) {
							isLoading = true;
							mCircleView.setVisibility(View.VISIBLE);
							mLoadingListener.onLoadMore(Direction.START, mLayoutManager.getItemCount());
						}
					}
					if ((mDirection == Direction.END || mDirection == Direction.BOTH) && !isLoading && mLayoutManager != null && isScrollToEnd()) {
						if (mLoadingListener != null) {
							isLoading = true;
							mLoadingListener.onLoadMore(Direction.END, mLayoutManager.getItemCount());
						}
					}
				}
			}
		};

		super.addOnScrollListener(mOnLoadMoreListener);
	}

	private void initProgressView() {
		mCircleView = new CircleImageView(getContext(), 0xFFFAFAFA);
		mProgress = new CircularProgressDrawable(getContext());
		mProgress.setStyle(CircularProgressDrawable.DEFAULT);
		mCircleView.setImageDrawable(mProgress);
		mCircleView.setVisibility(View.GONE);
	}

	private boolean isScrollToStart() {
		if (mLayoutManager instanceof GridLayoutManager) {
			GridLayoutManager layoutManager = (GridLayoutManager) mLayoutManager;
			int position = layoutManager.findFirstCompletelyVisibleItemPosition();
			return position == 0;
		} else if (mLayoutManager instanceof LinearLayoutManager) {
			LinearLayoutManager layoutManager = (LinearLayoutManager) mLayoutManager;
			int position = layoutManager.findFirstCompletelyVisibleItemPosition();
			return position == 0;
		} else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
			StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) mLayoutManager;
			int column = layoutManager.getColumnCountForAccessibility(null, null);
			int[] positions = new int[column];
			layoutManager.findLastCompletelyVisibleItemPositions(positions);
			for (int position : positions) {
				if (position == column) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isScrollToEnd() {
		if (mLayoutManager instanceof GridLayoutManager) {
			GridLayoutManager layoutManager = (GridLayoutManager) mLayoutManager;
			int position = layoutManager.findLastCompletelyVisibleItemPosition();
			return position == layoutManager.getItemCount() - 1;
		} else if (mLayoutManager instanceof LinearLayoutManager) {
			LinearLayoutManager layoutManager = (LinearLayoutManager) mLayoutManager;
			int position = layoutManager.findLastCompletelyVisibleItemPosition();
			return position == layoutManager.getItemCount() - 1;
		} else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
			StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) mLayoutManager;
			int column = layoutManager.getColumnCountForAccessibility(null, null);
			int[] positions = new int[column];
			layoutManager.findLastCompletelyVisibleItemPositions(positions);
			for (int position : positions) {
				if (position == layoutManager.getItemCount() - column) {
					return true;
				}
			}
		}
		return false;
	}

	public void setLayoutManager(@Nullable RecyclerView.LayoutManager layout) {
		mLayoutManager = layout;
		if (mLayoutManager instanceof GridLayoutManager) {
			mLayoutManagerType = LayoutManagerType.GRID_LAYOUT;
			mOrientation = ((GridLayoutManager) mLayoutManager).getOrientation();
		} else if (mLayoutManager instanceof LinearLayoutManager) {
			mLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT;
			mOrientation = ((LinearLayoutManager) mLayoutManager).getOrientation();
		} else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
			mLayoutManagerType = LayoutManagerType.STAGGERED_LAYOUT;
			mOrientation = ((StaggeredGridLayoutManager) mLayoutManager).getOrientation();
		}
		super.setLayoutManager(mLayoutManager);
	}

	public void onLoadFinish() {
		isLoading = false;
	}

	public void setDirection(int direction) {
		mDirection = direction;
	}

	private static final int KEY_SHADOW_COLOR = 0x1E000000;
	private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

	private Matrix mLoadingMatrix;
	private Canvas mLoadingIconCanvas;
	private Bitmap mLoadingBitmap;
	private Paint mLoadingPaint;

	private Matrix mCircularProgressMatrix;
	private Bitmap mCircularProgressBitmap;
	private Canvas mCircularProgressCanvas;
	private Paint mCircularProgressPaint;
	private static final float CIRCULAR_PROGRESS_SCALE = 0.65f;
	private RectF mCircularProgressRectF;

	private int mLoadingCanvasRadius;
	private int mLoadingIconRadius = 50;

	private int mStartAngle = -90;
	private int mEndAngle = -70;
	private int mSweepAngle = 300;
	private int mMinSweepAngle = 30;
	private int mMaxSweepAngle = 300;
	private boolean mSweepAngleAdd = false;
	private int mProgressSpeed = 6;
	private int mAngleOfCycle = 900;

	private void initView() {
		mLoadingCanvasRadius = (int) (mLoadingIconRadius * 1.5f);
		mLoadingMatrix = new Matrix();
		mLoadingBitmap = Bitmap.createBitmap(mLoadingCanvasRadius * 2, mLoadingCanvasRadius * 2, Bitmap.Config.ARGB_8888);
		mLoadingIconCanvas = new Canvas(mLoadingBitmap);
		//绘制圆形背景
		mLoadingPaint = new Paint();
		mLoadingPaint.setColor(CIRCLE_BG_LIGHT);
		mLoadingPaint.setStyle(Paint.Style.FILL);
		mLoadingPaint.setShadowLayer(mLoadingIconRadius / 3, 0, 0, KEY_SHADOW_COLOR);
		mLoadingIconCanvas.drawCircle(mLoadingCanvasRadius, mLoadingCanvasRadius, mLoadingIconRadius, mLoadingPaint);
		//绘制Loading圆弧
		mCircularProgressMatrix = new Matrix();
		mCircularProgressBitmap = Bitmap.createBitmap(mLoadingBitmap.getWidth(), mLoadingBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		mCircularProgressCanvas = new Canvas(mCircularProgressBitmap);
		mCircularProgressPaint = new Paint();
		mCircularProgressPaint.setColor(Color.BLACK);
		mCircularProgressPaint.setStyle(Paint.Style.STROKE);
		mCircularProgressPaint.setStrokeWidth(mLoadingIconRadius / 7);
		mCircularProgressPaint.setAntiAlias(true);
		mCircularProgressRectF = new RectF(mLoadingCanvasRadius * CIRCULAR_PROGRESS_SCALE
				, mLoadingCanvasRadius * CIRCULAR_PROGRESS_SCALE
				, mCircularProgressCanvas.getWidth() - mLoadingCanvasRadius * CIRCULAR_PROGRESS_SCALE
				, mCircularProgressCanvas.getHeight() - mLoadingCanvasRadius * CIRCULAR_PROGRESS_SCALE);

	}

	private ValueAnimator mLoadingAnimator;
	private static final int ANIMATION_DURATION = 1332;
	private static final float SHRINK_OFFSET = 0.5f;
	float mStartTrim = 0f;
	float mEndTrim = 0f;
	float mRotation = 0f;

	float mStartingStartTrim = 0;
	float mStartingEndTrim = 0;
	float mStartingRotation = 0;
	private static final float MAX_PROGRESS_ARC = .8f;
	private static final float MIN_PROGRESS_ARC = .01f;
	private static final Interpolator MATERIAL_INTERPOLATOR = new FastOutSlowInInterpolator();

	private static final float RING_ROTATION = 1f - (MAX_PROGRESS_ARC - MIN_PROGRESS_ARC);

	private void initAnimator() {
		mLoadingAnimator = ValueAnimator.ofFloat(0f, 1f);
		mLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
		mLoadingAnimator.setRepeatMode(ValueAnimator.RESTART);
		mLoadingAnimator.addUpdateListener((animator) -> {
			float animatorValue = (float) animator.getAnimatedValue();
			if (animatorValue != 1f) {

				if (animatorValue < SHRINK_OFFSET) { // Expansion occurs on first half of animation
					final float scaledTime = animatorValue / SHRINK_OFFSET;
					mStartTrim = mStartingStartTrim;
					mEndTrim = mStartTrim + ((MAX_PROGRESS_ARC - MIN_PROGRESS_ARC)
							* MATERIAL_INTERPOLATOR.getInterpolation(scaledTime) + MIN_PROGRESS_ARC);
				} else { // Shrinking occurs on second half of animation
					float scaledTime = (animatorValue - SHRINK_OFFSET) / (1f - SHRINK_OFFSET);
					mEndTrim = mStartingStartTrim + (MAX_PROGRESS_ARC - MIN_PROGRESS_ARC);
					mStartTrim = mEndTrim - ((MAX_PROGRESS_ARC - MIN_PROGRESS_ARC)
							* (1f - MATERIAL_INTERPOLATOR.getInterpolation(scaledTime))
							+ MIN_PROGRESS_ARC);
				}
				final float rotation = mStartingRotation + (RING_ROTATION * animatorValue);
			}
		});
		mLoadingAnimator.start();
	}

	@Override
	public void onDraw(Canvas c) {
		super.onDraw(c);

		final float startAngle = (mStartTrim + mRotation) * 360;
		final float endAngle = (mEndTrim + mRotation) * 360;
		float sweepAngle = endAngle - startAngle;

		Log.e("Value","startAngle = " + startAngle + " endAngle = " + endAngle);

		mLoadingMatrix.setTranslate((float) getWidth() / 2 - mLoadingCanvasRadius, mLoadingCanvasRadius);
		c.drawBitmap(mLoadingBitmap, mLoadingMatrix, null);
		c.save();
		c.translate((float) getWidth() / 2 - mLoadingCanvasRadius, mLoadingCanvasRadius);
		c.drawArc(mCircularProgressRectF, startAngle, sweepAngle, false, mCircularProgressPaint);
		c.restore();

		if (mSweepAngleAdd) {
			mSweepAngle += 10;
		} else {
			mSweepAngle -= 10;
		}

		mStartAngle += Math.sqrt(mSweepAngle);
		if (mStartAngle >= -90 + 360) {
			mStartAngle = mStartAngle - 360;
		}

		if (mSweepAngle <= mMinSweepAngle) {
			mSweepAngleAdd = true;
		} else if (mSweepAngle >= mMaxSweepAngle) {
			mSweepAngleAdd = false;
		}
		invalidate();
	}
}
