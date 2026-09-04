package com.space.engine.core.math;

public final class SMath {
    public static float VectorDot(float[] v0, float[] v1) {
        return v0[0] * v1[0] + v0[1] * v1[1] + v0[2] * v1[2];
    }
}
