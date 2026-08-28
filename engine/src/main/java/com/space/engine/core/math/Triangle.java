package com.space.engine.core.math;

import java.util.Arrays;

public class Triangle {
    public final float[] verts;

    public Triangle() {
        this.verts = new float[9];

        Arrays.fill(verts, 0.0f);
    }

    public Triangle(float[] verts) {
        this.verts = verts;
    }

    public Triangle(float[] v0, float[] v1, float[] v2) {
        float[] verts = new float[9];
        System.arraycopy(v0, 0, verts, 0, v0.length);
        System.arraycopy(v1, 0, verts, 3, v1.length);
        System.arraycopy(v2, 0, verts, 6, v2.length);
        this(verts);
    }

    public Triangle(Vector3 v0, Vector3 v1, Vector3 v2) {
        this(v0.asFloats(), v1.asFloats(), v2.asFloats());
    }

    public float[] v0() {
        return Arrays.copyOfRange(verts, 0, 3);
    }

    public float[] v1() {
        return Arrays.copyOfRange(verts, 3, 6);
    }

    public float[] v2() {
        return Arrays.copyOfRange(verts, 6, 9);
    }

    public Vector3 normal() {
        var v0 = new Vector3(v0());
        var v1 = new Vector3(v1());
        var v2 = new Vector3(v2());

        return Vector3.cross(v1.sub(v0), v2.sub(v0)).normalize();
    }
}
// public final Vector3 v1;
// public final Vector3 v2;
//
// public final Vector3 normal;
//
// public Triangle(Vector3 v0, Vector3 v1, Vector3 v2) {
// this.v0 = v0;
// this.v1 = v1;
// this.v2 = v2;
//
// this.normal = Vector3.cross(v1.copy().sub(v0),
// v2.copy().sub(v0)).normalized();
// }
// }
