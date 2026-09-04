package com.space.engine.core.components;

import com.space.engine.core.ecs.components.TransformComponent;
import com.space.engine.core.math.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransformComponentTest {

    public static final float EPSILON = 0.0001f;

    @Test
    void defaultConstructorUsesExpectedValues() {
        TransformComponent transform = new TransformComponent();

        assertEquals(Vector3.zero(), transform.position());
        assertEquals(Vector3.one(), transform.scale());
        assertEquals(Vector3.zero(), transform.rotation());
    }

    @Test
    void constructorStoresValues() {
        Vector3 position = new Vector3(1, 2, 3);
        Vector3 scale = new Vector3(2, 2, 2);
        Vector3 rotation = new Vector3(10, 20, 30);

        TransformComponent transform = new TransformComponent(position, scale, rotation);

        assertEquals(position, transform.position());
        assertEquals(scale, transform.scale());
        assertEquals(rotation, transform.rotation());
    }

    @Test
    void equalTransformsHaveSameHashCode() {
        TransformComponent first = new TransformComponent(
                new Vector3(1, 2, 3),
                Vector3.one(),
                Vector3.zero()
        );

        TransformComponent second = new TransformComponent(
                new Vector3(1, 2, 3),
                Vector3.one(),
                Vector3.zero()
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void localAxesAreNormalized() {
        TransformComponent transform = new TransformComponent(
                Vector3.zero(),
                Vector3.one(),
                new Vector3(20, 45, 10)
        );

        Vector3[] axes = transform.getLocalAxes();

        assertEquals(1.0f, axes[0].length(), EPSILON);
        assertEquals(1.0f, axes[1].length(), EPSILON);
        assertEquals(1.0f, axes[2].length(), EPSILON);
    }

    @Test
    void zeroRotationReturnsExpectedLocalAxes() {
        TransformComponent transform = new TransformComponent(
                Vector3.zero(),
                Vector3.one(),
                Vector3.zero()
        );

        Vector3[] axes = transform.getLocalAxes();

        assertVectorEquals(new Vector3(1, 0, 0), axes[0]);
        assertVectorEquals(new Vector3(0, 1, 0), axes[1]);
        assertVectorEquals(new Vector3(0, 0, 1), axes[2]);
    }

    private static void assertVectorEquals(Vector3 expected, Vector3 actual){
        assertEquals(expected.x, actual.x, EPSILON);
        assertEquals(expected.y, actual.y, EPSILON);
        assertEquals(expected.z, actual.z, EPSILON);
    }
}