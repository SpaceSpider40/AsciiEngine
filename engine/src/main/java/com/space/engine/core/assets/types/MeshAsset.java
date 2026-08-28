package com.space.engine.core.assets.types;

import com.space.engine.core.math.Triangle;

import java.util.List;

public record MeshAsset(
        List<Triangle> triangles
) {
}
