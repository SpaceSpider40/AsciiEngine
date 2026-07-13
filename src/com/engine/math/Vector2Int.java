package com.engine.math;

public class Vector2Int {
    public int x;
    public int y;

    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2Int() {
        this(0, 0);
    }

    public static Vector2Int zero() {
        return new Vector2Int(0, 0);
    }

    public static Vector2Int one() {
        return new Vector2Int(1, 1);
    }

    public static Vector2Int up() {
        return new Vector2Int(0, 1);
    }

    public static Vector2Int down() {
        return new Vector2Int(0, -1);
    }

    public static Vector2Int left() {
        return new Vector2Int(-1, 0);
    }

    public static Vector2Int right() {
        return new Vector2Int(1, 0);
    }

    public Vector2Int add(int scalar) {
        this.x += scalar;
        this.y += scalar;
        return this;
    }

    public Vector2Int add(Vector2Int other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    @Override
    public String toString() {
        return "Vector2Int{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
