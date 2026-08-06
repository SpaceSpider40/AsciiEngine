package com.engine.math;

public class Triangle {
    public final Vector3 v0;
    public final Vector3 v1;
    public final Vector3 v2;

    public final Vector3 normal;

    public Triangle(Vector3 v0, Vector3 v1, Vector3 v2) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;

        this.normal = Vector3.cross(v1.copy().sub(v0), v2.copy().sub(v0)).normalize();
    }
}
