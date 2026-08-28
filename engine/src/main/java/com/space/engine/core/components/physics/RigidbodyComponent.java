package com.space.engine.core.components.physics;

import com.space.engine.core.math.Vector3;

public record RigidbodyComponent(
        Vector3 velocity,
        Vector3 angularVelocity,
        float mass,
        float friction,
        float bounciness, // [0,1]

        float inertiaTensor,

        //accumulators
        Vector3 forceAccumulator,
        Vector3 torqueAccumulator
) {
    public RigidbodyComponent() {
        this(Vector3.zero(), Vector3.zero(), 1f, 0.4f, 0.2f, 1f, Vector3.zero(), Vector3.zero());
    }

    public RigidbodyComponent(
            Vector3 velocity,
            Vector3 angularVelocity,
            float mass,
            float friction,
            float bounciness
    ){
        this(velocity, angularVelocity, mass, friction, bounciness, 1.0f, Vector3.zero(), Vector3.zero());
    }

}
