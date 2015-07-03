package com.liananse.ptrefreshlayout;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

/**
 * Created by wangzenghui on 15/6/30.
 */
public class PTRefreshLayout extends ViewGroup {

    private static final String LOG_TAG = PTRefreshLayout.class.getSimpleName();

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final int ANIMATE_TO_START_DURATION = 200;
    private static final int DEFAULT_HEIGHT = dpToPx(256);
    private static final int DEFAULT_REFRESH_HEIGHT = dpToPx(128);

    public static final int STATE_STATIONARY = 0;
    public static final int STATE_REFRESHING = 1;
    public static final int STATE_MOVING = 2;

    private View mContentView;
    private View mHeaderView;

    private int mHeaderViewHeight;
    private int mHeaderViewRefreshHeight;
    private int mCurrentTargetOffsetTop;

    private OnRefreshListener mListener;
    private int mRefreshState = STATE_STATIONARY;
    private int mTouchSlop;

    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;

    public PTRefreshLayout(Context context) {
        this(context, null);
    }

    public PTRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PTRefreshLayout);
            mHeaderViewHeight = arr.getDimensionPixelOffset(R.styleable.PTRefreshLayout_header_height,
                    DEFAULT_HEIGHT);
            mHeaderViewRefreshHeight = arr.getDimensionPixelOffset(R.styleable.PTRefreshLayout_header_refresh_height, DEFAULT_REFRESH_HEIGHT);

            arr.recycle();
        }

        mCurrentTargetOffsetTop = -mHeaderViewHeight;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    private void setRefreshing(int refreshState) {
        if (refreshState == STATE_REFRESHING && mRefreshState != refreshState) {
            // scale and show
            mRefreshState = refreshState;
            int endTarget = mHeaderViewRefreshHeight - mHeaderViewHeight;
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop);
        } else {
            setRefreshing(refreshState, false /* notify */);
        }
    }

    private void setRefreshing(int refreshState, boolean notify) {
        if (mRefreshState != refreshState) {
            ensureTarget();
            if (refreshState == STATE_REFRESHING) {
                mRefreshState = STATE_MOVING;
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop);
            } else if (refreshState == STATE_STATIONARY) {
                mRefreshState = STATE_MOVING;
                animateOffsetToStartPosition(mCurrentTargetOffsetTop);
            }
        }
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mContentView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mHeaderView)) {
                    mContentView = child;
                    break;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mContentView == null) {
            ensureTarget();
        }
        if (mContentView == null) {
            return;
        }

        left = 0;
        top = 0;
        mContentView.layout(left, top, left + width, top + height);

        if (mHeaderView == null) {
            return;
        }

        int headerViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.layout(left, top - headerViewHeight, left + width, top);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mContentView == null) {
            ensureTarget();
        }
        if (mContentView == null) {
            return;
        }

        mContentView.measure(MeasureSpec.makeMeasureSpec(
                        getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(
                        getMeasuredHeight(), MeasureSpec.EXACTLY));

        if (mHeaderView == null) {
            return;
        }

        mHeaderView.measure(MeasureSpec.makeMeasureSpec(
                        getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(
                        mHeaderViewHeight, MeasureSpec.EXACTLY));
    }

    public void setHeaderView(View headerView) {
        if (mHeaderView != null) {
            removeView(mHeaderView);
        }
        addView(headerView);
        mHeaderView = headerView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        final int action = MotionEventCompat.getActionMasked(ev);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp() || mHeaderView == null) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        if (mRefreshState == STATE_REFRESHING) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTopAndBottom(-mHeaderViewHeight - mHeaderView.getTop());
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialDownY = getMotionEventY(ev, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }
                mInitialDownY = initialDownY;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialDownY;
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    mIsBeingDragged = true;
                    mRefreshState = STATE_MOVING;
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // Nope.
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp() || mHeaderView == null) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
                if (mIsBeingDragged) {
                    float originalDragPercent = overScrollTop / mHeaderViewRefreshHeight;
                    if (originalDragPercent < 0) {
                        return false;
                    }
                    float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
                    float extraOS = Math.abs(overScrollTop) - mHeaderViewRefreshHeight;
                    float slingshotDist = mHeaderViewRefreshHeight;
                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                            (tensionSlingshotPercent / 4), 2)) * 2f;
                    float extraMove = (slingshotDist) * tensionPercent * 2;

                    int targetY = (int) ((slingshotDist * dragPercent) + extraMove) - mHeaderViewHeight;

                    Log.e(LOG_TAG, "onMove targetY" + targetY);
                    setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop);
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    if (action == MotionEvent.ACTION_UP) {
                        Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    }
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;
                if (overScrollTop > mHeaderViewRefreshHeight) {
                    setRefreshing(STATE_REFRESHING, true);
                } else {
                    setRefreshing(STATE_STATIONARY, true);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mRefreshState = STATE_REFRESHING;
            // Make sure the progress view is fully visible
            if (mListener != null) {
                mListener.onRefresh();
            }
        }
    };

    /**
     * 动画回到刷新时停留的位置
     *
     * @param from
     */
    private void animateOffsetToCorrectPosition(final int from) {
        if (mHeaderView == null) {
            return;
        }
        final Animation mAnimateToCorrectPosition = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                int endTarget = mHeaderViewRefreshHeight - mHeaderViewHeight;
                int targetTop = (from + (int) ((endTarget - from) * interpolatedTime));
                int offset = targetTop - mHeaderView.getTop();
                setTargetOffsetTopAndBottom(offset);
            }
        };
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToCorrectPosition.setAnimationListener(mRefreshListener);
        mHeaderView.clearAnimation();
        mHeaderView.startAnimation(mAnimateToCorrectPosition);
    }

    private Animation.AnimationListener mMoveToStartPositionListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mRefreshState = STATE_STATIONARY;
        }
    };

    /**
     * 动画回到初始位置
     *
     * @param from
     */
    private void animateOffsetToStartPosition(final int from) {
        if (mHeaderView == null) {
            return;
        }
        final Animation mAnimateToStartPosition = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                int targetTop = (from + (int) ((-mHeaderViewHeight - from) * interpolatedTime));
                int offset = targetTop - mHeaderView.getTop();
                setTargetOffsetTopAndBottom(offset);
            }
        };
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToStartPosition.setAnimationListener(mMoveToStartPositionListener);
        mHeaderView.clearAnimation();
        mHeaderView.startAnimation(mAnimateToStartPosition);
    }


    private void setTargetOffsetTopAndBottom(int offset) {
        if (mHeaderView == null) {
            return;
        }
        mHeaderView.bringToFront();
        mHeaderView.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = mHeaderView.getTop();

        if (mHeaderView instanceof PTRefreshBaseHeaderView) {
            ((PTRefreshBaseHeaderView) mHeaderView).onPullProgress(mRefreshState, (mCurrentTargetOffsetTop + mHeaderViewHeight) / mHeaderViewHeight);
        }
        if (mContentView != null) {
            mContentView.offsetTopAndBottom(offset);
        }
        invalidate();
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mContentView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mContentView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mContentView, -1) || mContentView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mContentView, -1);
        }
    }

    private static final int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        void onRefresh();
    }

    public int getRefreshState() {
        return mRefreshState;
    }

    public void stopRefreshing() {
        setRefreshing(STATE_STATIONARY);
    }

    public void startRefreshing() {
        setRefreshing(STATE_REFRESHING);
    }
}
