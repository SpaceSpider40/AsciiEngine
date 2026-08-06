package com.engine.math;

import java.util.Collection;
import java.util.List;

public class Plane {
    public final Vector3 v0;
    public final Vector3 v1;
    public final Vector3 v2;
    public final Vector3 v3;

    public Plane(Vector3 v0, Vector3 v1, Vector3 v2, Vector3 v3) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Collection<Triangle> triangularize() {
        double dg1 = v0.distance(v2);
        double dg2 = v1.distance(v3);

        if (dg1 <= dg2) {
            return List.of(
                    new Triangle(v0, v1, v2),
                    new Triangle(v0, v2, v3)
            );
        } else {
            return List.of(
                    new Triangle(v0, v1, v3),
                    new Triangle(v1, v2, v3)
            );
        }
    }
}
