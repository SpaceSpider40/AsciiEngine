package com.engine.components;

import com.engine.math.Triangle;

import java.util.Collection;

public record MeshComponent(
        Collection<Triangle> tris
) {

}
