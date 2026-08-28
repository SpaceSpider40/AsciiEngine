package com.space.engine.core.components.physics;

import com.space.engine.core.math.Vector3;

public record BoxColliderComponent(
        Vector3 halfExtents,
        float boundingRadius
) {
    public BoxColliderComponent(Vector3 halfExtents) {
        this(halfExtents, halfExtents.length());
    }
}
