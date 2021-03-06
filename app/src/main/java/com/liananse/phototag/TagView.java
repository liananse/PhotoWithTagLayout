package com.liananse.phototag;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by wangzenghui on 15/7/2.
 */
public class TagView extends LinearLayout {
    public static final int VISIBLE = 0;
    public static final int GONE = 8;
    /**
     * 标签内容
     */
    public TextView mTagContent;

    /**
     * 标签点
     */
    public ImageView mTagShadowIcon1;
    public ImageView mTagShadowIcon2;
    public ImageView mTagPointerIcon;

    /**
     * 标签点动画
     */
    private Animation mTagShadowIcon1Anim;
    private Animation mTagShadowIcon2Anim;
    private Animation mTagPointerIconAnim;

    private Handler mHandler = new Handler();
    /**
     * 标签内容
     */
    private TagModel mTagModel;

    public TagView(Context context) {
        this(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        initAnimation(context);
    }

    /**
     * 初始化需要的动画
     *
     * @param context
     */
    private void initAnimation(Context context) {
        mTagShadowIcon1Anim = AnimationUtils.loadAnimation(context, R.anim.tag_shadow_anim);
        mTagShadowIcon1Anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTagShadowIcon1.clearAnimation();
                        mTagShadowIcon1Anim.reset();
                        startTagShadowIcon2Animation();
                    }
                }, 10);
            }
        });

        mTagShadowIcon2Anim = AnimationUtils.loadAnimation(context, R.anim.tag_shadow_anim);
        mTagShadowIcon2Anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mTagShadowIcon2.clearAnimation();
                        mTagShadowIcon2Anim.reset();
                        startTagPointerIconAnimation();
                    }
                }, 10);
            }
        });

        mTagPointerIconAnim = AnimationUtils.loadAnimation(context, R.anim.tag_white_anim);
        mTagPointerIconAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTagPointerIcon.clearAnimation();
                        mTagPointerIconAnim.reset();
                        startTagShadowIcon1Animation();
                    }
                }, 10);
            }
        });
    }

    public void clearTagAnimation() {
        this.mTagShadowIcon1.clearAnimation();
        this.mTagShadowIcon2.clearAnimation();
        this.mTagPointerIcon.clearAnimation();
    }

    public void startTagShadowIcon1Animation() {
//        mTagShadowIcon1.clearAnimation();
        mTagShadowIcon1.startAnimation(mTagShadowIcon1Anim);
    }

    public void startTagShadowIcon2Animation() {
//        mTagShadowIcon2.clearAnimation();
        mTagShadowIcon2.startAnimation(mTagShadowIcon2Anim);
    }

    public void startTagPointerIconAnimation() {
//        mTagPointerIcon.clearAnimation();
        mTagPointerIcon.startAnimation(mTagPointerIconAnim);
    }

    public void setTagVisibility(int visibility) {
        if (visibility == TagView.VISIBLE) {
            setVisibility(View.VISIBLE);
        } else if (visibility == TagView.GONE) {
            setVisibility(View.GONE);
        }
    }

    public void setTagData(TagModel tagModel) {
        mTagModel = tagModel;
        if (mTagModel != null && mTagContent != null) {
            this.mTagContent.setText(tagModel.name);
        }
//        clearTagAnimation();
        startTagPointerIconAnimation();
    }

    public TagModel getTagModel() {
        return mTagModel;
    }

}
