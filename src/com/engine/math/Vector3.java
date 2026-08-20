package com.engine.math;

import java.util.Objects;

public class Vector3 {
    public float x;
    public float y;
    public float z;

    public static Vector3[] axes = new Vector3[]
            {
                    right(), up(), forward()
            };

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public static Vector3 zero() {
        return new Vector3(0, 0, 0);
    }

    public static Vector3 one() {
        return new Vector3(1, 1, 1);
    }

    public static Vector3 right() {
        return new Vector3(1, 0, 0);
    }

    public static Vector3 up() {
        return new Vector3(0, 1, 0);
    }

    public static Vector3 forward() {
        return new Vector3(0, 0, 1);
    }

    public static Vector3 cross(Vector3 a, Vector3 b) {
        return new Vector3(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }

    public Vector3 set(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vector3 copy() {
        return new Vector3(this);
    }

    public Vector3 add(Vector3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Vector3 sub(Vector3 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public Vector3 mul(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    public Vector3 div(float scalar) {
        this.x /= scalar;
        this.y /= scalar;
        this.z /= scalar;
        return this;
    }

    public Vector3 rotateX(float angle) {
        float rad = (float) Math.toRadians(angle);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float _y = y;
        float _z = z;
        y = _y * cos - _z * sin;
        z = _y * sin + _z * cos;
        return this;
    }

    public Vector3 rotateY(float angle) {
        float rad = (float) Math.toRadians(angle);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float _x = x;
        float _z = z;
        x = _x * cos + _z * sin;
        z = -_x * sin + _z * cos;
        return this;
    }

    public Vector3 rotateZ(float angle) {
        float rad = (float) Math.toRadians(angle);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float _x = x;
        float _y = y;
        x = _x * cos - _y * sin;
        y = _x * sin + _y * cos;
        return this;
    }

    public Vector3 normalize(){
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        x = x / length;
        y = y / length;
        z = z / length;
        return this;
    }

    public Vector3 normalized() {
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        return new Vector3(x / length, y / length, z / length);
    }

    public float dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public double distance(Vector3 other) {
        return Math.sqrt(
                Math.pow(other.x - this.x, 2) +
                        Math.pow(other.y + this.y, 2) +
                        Math.pow(other.z + this.z, 2)
        );
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vector3 vector3 = (Vector3) o;
        return Float.compare(x, vector3.x) == 0 && Float.compare(y, vector3.y) == 0 && Float.compare(z, vector3.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Vector3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
