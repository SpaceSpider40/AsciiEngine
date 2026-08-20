package com.engine.components.physics;

import com.engine.math.Vector3;

public record RigidbodyComponent(
        Vector3 velocity,
        float bounciness // [0,1]

) {
}
