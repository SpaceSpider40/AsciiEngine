package com.space.engine.core.actors.components;

import com.space.engine.core.math.Triangle;

import java.util.ArrayList;
import java.util.Collection;

public class MeshComponent extends ActorComponent{
    private final Triangle[] triangles;

    public MeshComponent(Collection<Triangle> triangles) {
        this.triangles = new Triangle[triangles.size()];

        triangles.toArray(this.triangles);
    }

    public Triangle[] getTriangles() {
        return triangles;
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onUpdate(double deltaTime) {

    }

    @Override
    protected void onStep(double deltaTime) {

    }

    @Override
    protected void onEnd() {

    }
}
