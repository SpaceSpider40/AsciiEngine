package com.engine;

import com.engine.components.MeshComponent;
import com.engine.math.Plane;
import com.engine.math.Triangle;
import com.engine.math.Vector3;

import java.util.ArrayList;

public final class MeshFactory {
    public static MeshComponent createBox(Vector3 size) {
        var tris = new ArrayList<Triangle>();

        var front = new Plane(
                new Vector3(-size.x, size.y, -size.z),
                new Vector3(size.x, size.y, -size.z),
                new Vector3(size.x, -size.y, -size.z),
                new Vector3(-size.x, -size.y, -size.z)
        );

        var left = new Plane(
                new Vector3(size.x, size.y, -size.z),
                new Vector3(size.x, size.y, size.z),
                new Vector3(size.x, -size.y, size.z),
                new Vector3(size.x, -size.y, -size.z)
        );

        var right = new Plane(
                new Vector3(-size.x, size.y, -size.z),
                new Vector3(-size.x, size.y, size.z),
                new Vector3(-size.x, -size.y, size.z),
                new Vector3(-size.x, -size.y, -size.z)
        );

        var back = new Plane(
                new Vector3(-size.x, size.y, size.z),
                new Vector3(size.x, size.y, size.z),
                new Vector3(size.x, -size.y, size.z),
                new Vector3(-size.x, -size.y, size.z)
        );

        var top = new Plane(
                new Vector3(-size.x, size.y, -size.z),
                new Vector3(size.x, size.y, -size.z),
                new Vector3(size.x, size.y, size.z),
                new Vector3(-size.x, size.y, size.z)
        );

        var bottom = new Plane(
                new Vector3(-size.x, -size.y, -size.z),
                new Vector3(size.x, -size.y, -size.z),
                new Vector3(size.x, -size.y, size.z),
                new Vector3(-size.x, -size.y, size.z)
        );

        tris.addAll(front.triangularize());
        tris.addAll(left.triangularize());
        tris.addAll(back.triangularize());
        tris.addAll(right.triangularize());
        tris.addAll(top.triangularize());
        tris.addAll(bottom.triangularize());

        return new MeshComponent(tris);
    }
}
