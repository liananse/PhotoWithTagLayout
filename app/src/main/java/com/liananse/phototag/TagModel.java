package com.liananse.phototag;

import android.graphics.Point;

import java.io.Serializable;

/**
 * Created by wangzenghui on 15/7/2.
 */
public class TagModel implements Serializable {

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_LOCATION = 2;
    public static final int TYPE_BRAND = 3;
    public static final int TYPE_TOPIC = 4;

    public int type;
    public Point position;
    public String name;

    public TagModel () {

    }
    public TagModel(int type, Point position, String name) {
        this.type = type;
        this.position = position;
        this.name = name;
    }
}
