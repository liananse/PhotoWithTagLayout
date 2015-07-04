package com.liananse.phototag;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.liananse.photoview.PhotoViewAttacher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzenghui on 15/7/2.
 */
public class TagContainer extends RelativeLayout implements PhotoViewAttacher.OnMatrixChangedListener, PhotoViewAttacher.OnScaleChangeListener {
    private ImageView mImageView;
    private RelativeLayout mTagContainer;
    private List<TagViewLeft> mTagViewsLeft;
    private List<TagViewRight> mTagViewsRight;
    private int mTagViewPointSize;

    private boolean mCanScale = false;
    private PhotoViewAttacher mAttacher;

    private float scale = 1.0f;
    private int newPositionX;
    private int newPositionY;

    private int mTagModelLeftIndex = -1;
    private int mTagModelRightIndex = -1;

    public TagContainer(Context context) {
        this(context, null);
    }

    public TagContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_tag_image, this);
        mImageView = (ImageView) findViewById(R.id.image);
        mTagContainer = (RelativeLayout) findViewById(R.id.tag_container);
        mTagViewsLeft = new ArrayList<>();
        mTagViewsRight = new ArrayList<>();
        mTagViewPointSize = getResources().getDimensionPixelOffset(R.dimen.tag_view_point_size);
    }

    public void setCanScale(boolean canScale) {
        this.mCanScale = canScale;
        if (this.mCanScale) {
            mAttacher = new PhotoViewAttacher(mImageView);
            mAttacher.setOnMatrixChangeListener(this);
            mAttacher.setOnScaleChangeListener(this);
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
                    LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
                    LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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

    @Override
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

    @Override
    public void onScaleChange(float scaleFactor, float focusX, float focusY) {
        scale = scale * scaleFactor;
    }
}
