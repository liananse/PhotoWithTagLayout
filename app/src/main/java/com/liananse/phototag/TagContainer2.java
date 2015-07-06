package com.liananse.phototag;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzenghui on 15/7/6.
 */
public class TagContainer2 extends ViewGroup {
    private View mContentView;
    private RelativeLayout mTagContainer;
    private List<TagViewLeft> mTagViewsLeft;
    private List<TagViewRight> mTagViewsRight;
    private int mTagViewPointSize;

    private float scale = 1.0f;
    private int newPositionX;
    private int newPositionY;

    private int mTagModelLeftIndex = -1;
    private int mTagModelRightIndex = -1;
    public TagContainer2(Context context) {
        this(context, null);
    }

    public TagContainer2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagContainer2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTagViewsLeft = new ArrayList<>();
        mTagViewsRight = new ArrayList<>();
        mTagViewPointSize = getResources().getDimensionPixelOffset(R.dimen.tag_view_point_size);

        mTagContainer = new RelativeLayout(getContext());
        mTagContainer.setBackgroundResource(android.R.color.transparent);
        addView(mTagContainer);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
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

        mTagContainer.measure(View.MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        mTagContainer.bringToFront();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        if (getChildCount() == 0) {
            return;
        }
        if (mContentView == null) {
            ensureTarget();
        }
        if (mContentView == null) {
            return;
        }

        mContentView.layout(0, 0, width, height);

        this.mTagContainer.layout(0, 0, width, height);
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mContentView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mTagContainer)) {
                    mContentView = child;
                    break;
                }
            }
        }
    }

    public void setTagList(List<TagModel> tagModels) {
        mTagModelLeftIndex = -1;
        mTagModelRightIndex = -1;
        for (TagModel tagModel : tagModels) {
            if (tagModel.direction == TagModel.Direction.LEFT) {
                mTagModelLeftIndex++;
                // 有TagViewLeft可以直接使用
                if (mTagViewsLeft.size() > mTagModelLeftIndex) {
                    TagViewLeft tagViewLeft = mTagViewsLeft.get(mTagModelLeftIndex);
                    tagViewLeft.setTagData(tagModel);
                    tagViewLeft.measure(0, 0);
                    tagViewLeft.setTranslationX(tagModel.position.x - mTagViewPointSize / 2);
                    tagViewLeft.setTranslationY(tagModel.position.y - tagViewLeft.getMeasuredHeight() / 2);
                    tagViewLeft.setTagVisibility(TagView.VISIBLE);
                } else {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    TagViewLeft tagViewLeft = new TagViewLeft(getContext());
                    tagViewLeft.setTagData(tagModel);
                    mTagContainer.addView(tagViewLeft, layoutParams);
                    tagViewLeft.measure(0, 0);
                    tagViewLeft.setTranslationX(tagModel.position.x - mTagViewPointSize / 2);
                    tagViewLeft.setTranslationY(tagModel.position.y - tagViewLeft.getMeasuredHeight() / 2);
                    tagViewLeft.setTagVisibility(TagView.VISIBLE);
                    mTagViewsLeft.add(tagViewLeft);
                }

            } else {
                mTagModelRightIndex++;
                if (mTagViewsRight.size() > mTagModelRightIndex) {
                    TagViewRight tagViewRight = mTagViewsRight.get(mTagModelRightIndex);
                    tagViewRight.setTagData(tagModel);
                    tagViewRight.measure(0, 0);
                    tagViewRight.setTranslationX(tagModel.position.x + mTagViewPointSize / 2 - tagViewRight.getMeasuredWidth());
                    tagViewRight.setTranslationY(tagModel.position.y - tagViewRight.getMeasuredHeight() / 2);
                    tagViewRight.setTagVisibility(TagView.VISIBLE);
                } else {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    TagViewRight tagViewRight = new TagViewRight(getContext());
                    tagViewRight.setTagData(tagModel);
                    mTagContainer.addView(tagViewRight, layoutParams);
                    tagViewRight.measure(0, 0);
                    tagViewRight.setTranslationX(tagModel.position.x + mTagViewPointSize / 2 - tagViewRight.getMeasuredWidth());
                    tagViewRight.setTranslationY(tagModel.position.y - tagViewRight.getMeasuredHeight() / 2);
                    tagViewRight.setTagVisibility(TagView.VISIBLE);
                    mTagViewsRight.add(tagViewRight);
                }
            }
        }

        if (mTagViewsLeft.size() > (mTagModelLeftIndex + 1)) {
            for (int i = mTagModelLeftIndex + 1; i < mTagViewsLeft.size(); i++) {
                mTagViewsLeft.get(i).setTagVisibility(TagView.GONE);
            }
        }

        if (mTagViewsRight.size() > (mTagModelRightIndex + 1)) {
            for (int i = mTagModelRightIndex + 1; i < mTagViewsRight.size(); i++) {
                mTagViewsRight.get(i).setTagVisibility(TagView.GONE);
            }
        }
    }

    /**
     * 缩放时tag跟随
     * @param rect
     */
    public void onMatrixChanged(RectF rect) {
        if (mTagContainer.getChildCount() > 0) {
            View childView;
            for (int i = 0; i < mTagContainer.getChildCount(); i++) {
                childView = mTagContainer.getChildAt(i);
                childView.measure(0, 0);
                if (childView instanceof TagView) {
                    newPositionX = (int) (((TagView) childView).getTagModel().position.x * scale) + (int) rect.left;
                    newPositionY = (int) (((TagView) childView).getTagModel().position.y * scale) + (int) rect.top;

                    // 如果相对ImageView的边界
                    if (((TagView) childView).getTagModel().positionOfImage == null) {
                        Point positionOfImage = new Point((int) rect.left, (int) rect.top);
                        ((TagView) childView).getTagModel().positionOfImage = positionOfImage;
                    }
                    newPositionX = newPositionX - (int) (((TagView) childView).getTagModel().positionOfImage.x * scale);
                    newPositionY = newPositionY - (int) (((TagView) childView).getTagModel().positionOfImage.y * scale);

                    if (((TagView) childView).getTagModel().direction == TagModel.Direction.LEFT) {
                        childView.setTranslationX(newPositionX - mTagViewPointSize / 2);
                        childView.setTranslationY(newPositionY - childView.getMeasuredHeight() / 2);
                    } else if (((TagView) childView).getTagModel().direction == TagModel.Direction.RIGHT) {
                        childView.setTranslationX(newPositionX + mTagViewPointSize / 2 - childView.getMeasuredWidth());
                        childView.setTranslationY(newPositionY - childView.getMeasuredHeight() / 2);
                    }
                }
            }

        }
    }

    public void onScaleChange(float scaleFactor, float focusX, float focusY) {
        scale = scale * scaleFactor;
    }
}
