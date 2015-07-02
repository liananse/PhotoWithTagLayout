package com.liananse.phototag;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by wangzenghui on 15/7/2.
 */
public class TagViewLeft extends TagView {
    public TagViewLeft(Context context) {
        this(context, null);
    }

    public TagViewLeft(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagViewLeft(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.view_tag_left, this);
        this.mTagContent = ((TextView) findViewById(R.id.tag_content));
        this.mTagContent.getBackground().setAlpha(178);
        this.mTagContent.setVisibility(View.VISIBLE);
        this.mTagShadowIcon1 = ((ImageView) findViewById(R.id.tag_shadow_icon1));
        this.mTagShadowIcon2 = ((ImageView) findViewById(R.id.tag_shadow_icon2));
        this.mTagPointerIcon = ((ImageView) findViewById(R.id.tag_pointer_icon));
        setTagVisibility(VISIBLE);


        TagModel tagModel = new TagModel();
        tagModel.type = TagModel.Type.NORMAL;
        tagModel.position = new Point(50, 50);
        tagModel.name = "tag left";

        setTagData(tagModel);
    }
}
