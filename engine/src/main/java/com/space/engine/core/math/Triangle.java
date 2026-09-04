package com.space.engine.core.math;

import java.util.Arrays;

public class Triangle {
    public final float[] verts;

    private float normalX;
    private float normalY;
    private float normalZ;

    private boolean normalDirty = true;

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

    public float getNormalX(){
        normal();
        return normalX;
    }

    public float getNormalY(){
        normal();
        return normalY;
    }

    public float getNormalZ(){
        normal();
        return normalZ;
    }

    public void normal(){
        if (normalDirty){
            float ax = verts[3] - verts[0];
            float ay = verts[4] - verts[1];
            float az = verts[5] - verts[2];

            float bx = verts[6] - verts[0];
            float by = verts[7] - verts[1];
            float bz = verts[8] - verts[2];

            float nx = ay * bz - az * by;
            float ny = az * bx - ax * bz;
            float nz = ax * by - ay * bx;

            float lengthSquared = nx * nx + ny * ny + nz * nz;

            if (lengthSquared == 0.0f) {
                normalX = 0.0f;
                normalY = 0.0f;
                normalZ = 0.0f;
            } else {
                float invLength = 1.0f / (float) Math.sqrt(lengthSquared);

                normalX = nx * invLength;
                normalY = ny * invLength;
                normalZ = nz * invLength;
            }

            normalDirty = false;
        }
    }
}