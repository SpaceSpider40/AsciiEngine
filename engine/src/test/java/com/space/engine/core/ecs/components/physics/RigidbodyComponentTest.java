package com.space.engine.core.ecs.components.physics;

import com.space.engine.core.math.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RigidbodyComponentTest {

    @Test
    void constructorStoresVelocityAndBounciness() {
        Vector3 velocity = new Vector3(1, 2, 3);

        RigidbodyComponent rigidbody = new RigidbodyComponent(velocity, Vector3.zero(), 0.0f, 0.0f, 0.4f);

        assertEquals(velocity, rigidbody.velocity());
        assertEquals(0.4f, rigidbody.bounciness(), 0.0001f);
    }

    @Test
    void componentsWithSameValuesAreEqual() {
        Vector3 velocity = new Vector3(0, 1, 0);

        RigidbodyComponent first = new RigidbodyComponent(velocity, Vector3.zero(), 0.0f, 0.0f, 0.5f);
        RigidbodyComponent second = new RigidbodyComponent(velocity, Vector3.zero(), 0.0f, 0.0f, 0.5f);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}