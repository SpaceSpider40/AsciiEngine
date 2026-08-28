package com.space.engine.core.math;

import java.util.Arrays;

public class Matrix44 {
    public final float[] m = new float[16];

    public Matrix44() {
        setIdentity();
    }

    public void setIdentity() {
        Arrays.fill(m, 0);
        m[0] = 1;
        m[5] = 1;
        m[10] = 1;
        m[15] = 1;
    }

    public Matrix44 mul(Matrix44 other) {
        Matrix44 res = new Matrix44();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                res.m[r * 4 + c] = this.m[r * 4 + 0] * other.m[0 * 4 + c] +
                        this.m[r * 4 + 1] * other.m[1 * 4 + c] +
                        this.m[r * 4 + 2] * other.m[2 * 4 + c] +
                        this.m[r * 4 + 3] * other.m[3 * 4 + c];
            }
        }

        return res;
    }

    public Vector3 mulPoint(Vector3 v) {
        float x = m[0] * v.x + m[1] * v.y + m[2] * v.z + m[3];
        float y = m[4] * v.x + m[5] * v.y + m[6] * v.z + m[7];
        float z = m[8] * v.x + m[9] * v.y + m[10] * v.z + m[11];
        float w = m[12] * v.x + m[13] * v.y + m[14] * v.z + m[15];

        if (Math.abs(w - 1) > 0.0001f && Math.abs(w) > 0.0001f) {
            x /= w;
            y /= w;
            z /= w;
        }
        return new Vector3(x, y, z);
    }

    public Vector3 mulPoint(float[] verts) {
        float x = m[0] * verts[0] + m[1] * verts[1] + m[2] * verts[2] + m[3];
        float y = m[4] * verts[0] + m[5] * verts[1] + m[6] * verts[2] + m[7];
        float z = m[8] * verts[0] + m[9] * verts[1] + m[10] * verts[2] + m[11];
        float w = m[12] * verts[0] + m[13] * verts[1] + m[14] * verts[2] + m[15];

        if (Math.abs(w - 1) > 0.0001f && Math.abs(w) > 0.0001f) {
            x /= w;
            y /= w;
            z /= w;
        }
        return new Vector3(x, y, z);
    }

    public Vector3 mulVector(Vector3 v) {
        float x = m[0] * v.x + m[1] * v.y + m[2] * v.z;
        float y = m[4] * v.x + m[5] * v.y + m[6] * v.z;
        float z = m[8] * v.x + m[9] * v.y + m[10] * v.z;

        return new Vector3(x, y, z);
    }

    public static Matrix44 createTranslation(float x, float y, float z) {
        Matrix44 mat = new Matrix44();
        mat.m[3] = x;
        mat.m[7] = y;
        mat.m[11] = z;
        return mat;
    }

    public static Matrix44 createRotationX(float angle) {
        Matrix44 mat = new Matrix44();
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        mat.m[5] = cos;
        mat.m[6] = -sin;
        mat.m[9] = sin;
        mat.m[10] = cos;

        return mat;
    }

    public static Matrix44 createRotationY(float angle) {
        Matrix44 mat = new Matrix44();
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        mat.m[0] = cos;
        mat.m[2] = sin;
        mat.m[8] = -sin;
        mat.m[10] = cos;

        return mat;
    }

    public static Matrix44 createRotationZ(float angle) {
        Matrix44 mat = new Matrix44();
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        mat.m[0] = cos;
        mat.m[1] = -sin;
        mat.m[4] = sin;
        mat.m[5] = cos;
        return mat;
    }

    public static Matrix44 createScale(float x, float y, float z) {
        Matrix44 mat = new Matrix44();
        mat.m[0] = x;
        mat.m[5] = y;
        mat.m[10] = z;
        return mat;
    }

    public static Matrix44 createModelMatrix(
            Vector3 position,
            Vector3 rotation,
            Vector3 scale) {
        Matrix44 translation = Matrix44.createTranslation(position.x, position.y, position.z);
        Matrix44 rotX = Matrix44.createRotationX((float) Math.toRadians(rotation.x));
        Matrix44 rotY = Matrix44.createRotationY((float) Math.toRadians(rotation.y));
        Matrix44 rotZ = Matrix44.createRotationZ((float) Math.toRadians(rotation.z));

        Matrix44 rotationMatrix = rotY.mul(rotX).mul(rotZ);
        Matrix44 scaleMatrix = Matrix44.createScale(scale.x, scale.y, scale.z);

        return translation.mul(rotationMatrix).mul(scaleMatrix);
    }

    public static Matrix44 createViewMatrix(
            Vector3 cameraPosition,
            Vector3 cameraRotation) {
        Matrix44 invTranslation = Matrix44.createTranslation(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        Matrix44 invRotationX = Matrix44.createRotationX((float) Math.toRadians(-cameraRotation.x));
        Matrix44 invRotationY = Matrix44.createRotationY((float) Math.toRadians(-cameraRotation.y));
        Matrix44 invRotationZ = Matrix44.createRotationZ((float) Math.toRadians(-cameraRotation.z));

        Matrix44 invRotation = invRotationZ.mul(invRotationX).mul(invRotationY);

        return invTranslation.mul(invRotation);
    }

    public static Matrix44 createViewMatrix(
            float[] cameraPosition,
            float[] cameraRotation) {
        Matrix44 invTranslation = Matrix44.createTranslation(-cameraPosition[0], -cameraPosition[1],
                -cameraPosition[2]);

        Matrix44 invRotationX = Matrix44.createRotationX((float) Math.toRadians(-cameraRotation[0]));
        Matrix44 invRotationY = Matrix44.createRotationY((float) Math.toRadians(-cameraRotation[1]));
        Matrix44 invRotationZ = Matrix44.createRotationZ((float) Math.toRadians(-cameraRotation[2]));

        Matrix44 invRotation = invRotationZ.mul(invRotationX).mul(invRotationY);

        return invTranslation.mul(invRotation);
    }
}
