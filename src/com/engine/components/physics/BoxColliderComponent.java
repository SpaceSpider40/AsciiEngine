package com.engine.components.physics;

import com.engine.math.Vector3;

public record BoxColliderComponent(
        Vector3 halfExtents,
        float boundingRadius
) {
    public BoxColliderComponent(Vector3 halfExtents) {
        this(halfExtents, halfExtents.length());
    }
}
