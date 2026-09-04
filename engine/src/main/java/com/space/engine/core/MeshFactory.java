package com.space.engine.core;

import com.space.engine.core.ecs.components.MeshComponent;
import com.space.engine.core.math.Plane;
import com.space.engine.core.math.Triangle;
import com.space.engine.core.math.Vector3;

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
