package com.liananse.phototag;

import android.graphics.Point;

import java.io.Serializable;

/**
 * Created by wangzenghui on 15/7/2.
 */
public class TagModel implements Serializable {

    public String name;
    public Type type = Type.NORMAL;
    public Direction direction = Direction.LEFT;
    public Point position;
    public Point positionOfImage;
    public TagModel () {

    }

    public enum Type {
        NORMAL, LOCATION, BRAND, TOPIC
    }

    public enum Direction {
        LEFT, RIGHT
    }

}
