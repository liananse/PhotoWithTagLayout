package com.liananse.phototag;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.liananse.photoview.PhotoViewAttacher;

import java.util.List;

/**
 * Created by wangzenghui on 15/7/2.
 */
public class TagImageView extends RelativeLayout implements PhotoViewAttacher.OnMatrixChangedListener, PhotoViewAttacher.OnScaleChangeListener {
    private ImageView mImageView;
    private RelativeLayout mTagBox;
    private int mTagViewPointSize;
    private PhotoViewAttacher mAttacher;

    private float scale = 1.0f;
    private int newPositionX;
    private int newPositionY;
    public TagImageView(Context context) {
        this(context, null);
    }

    public TagImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_tag_image, this);
        mImageView = (ImageView) findViewById(R.id.image);
        mTagBox = (RelativeLayout) findViewById(R.id.tag_box);

        mTagViewPointSize = getResources().getDimensionPixelOffset(R.dimen.tag_view_point_size);

        Drawable bitmap = getResources().getDrawable(R.drawable.wallpaper);
        mImageView.setImageDrawable(bitmap);

        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setOnMatrixChangeListener(this);
        mAttacher.setOnScaleChangeListener(this);
    }

    public void setTagList(List<TagModel> tagModels) {
        mTagBox.removeAllViews();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        for (TagModel tagModel : tagModels) {
            TagView tagView;
            if (tagModel.direction == TagModel.Direction.LEFT) {
                tagView = new TagViewLeft(getContext());
            } else {
                tagView = new TagViewRight(getContext());
            }
            tagView.setTagData(tagModel);
            mTagBox.addView(tagView, layoutParams);
        }
    }

    @Override
    public void onMatrixChanged(RectF rect) {
        if (mTagBox.getChildCount() > 0) {
            View childView;
            for (int i = 0; i < mTagBox.getChildCount(); i++) {
                childView = mTagBox.getChildAt(i);
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
