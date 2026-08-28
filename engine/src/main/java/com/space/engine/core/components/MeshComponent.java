package com.space.engine.core.components;

import com.space.engine.core.math.Triangle;

import java.util.Collection;

public record MeshComponent(
        Collection<Triangle> tris
) {

}
