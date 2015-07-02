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
    public TagModel () {

    }

    public TagModel(String name, Type type, Direction direction, Point position) {
        this.name = name;
        this.type = type;
        this.direction = direction;
        this.position = position;
    }

    public enum Type {
        NORMAL, LOCATION, BRAND, TOPIC
    }

    public enum Direction {
        LEFT, RIGHT
    }

}
