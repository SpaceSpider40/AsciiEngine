package com.engine.components.physics;

import com.engine.math.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoxColliderComponentTest {

    @Test
    void constructorCalculatesBoundingRadiusFromHalfExtentsLength() {
        Vector3 halfExtents = new Vector3(3, 4, 0);

        BoxColliderComponent collider = new BoxColliderComponent(halfExtents);

        assertEquals(5.0f, collider.boundingRadius(), 0.0001f);
    }

    @Test
    void fullConstructorStoresValues() {
        Vector3 halfExtents = new Vector3(1, 2, 3);

        BoxColliderComponent collider = new BoxColliderComponent(halfExtents, 10.0f);

        assertEquals(halfExtents, collider.halfExtents());
        assertEquals(10.0f, collider.boundingRadius(), 0.0001f);
    }

    @Test
    void componentsWithSameValuesAreEqual() {
        Vector3 halfExtents = new Vector3(1, 1, 1);

        BoxColliderComponent first = new BoxColliderComponent(halfExtents, 2.0f);
        BoxColliderComponent second = new BoxColliderComponent(halfExtents, 2.0f);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}