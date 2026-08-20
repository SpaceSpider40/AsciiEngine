package com.engine.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector3Test {

    private static final float EPSILON = 0.0001f;

    @Test
    void defaultConstructorCreatesZeroVector() {
        Vector3 vector = new Vector3();

        assertEquals(0.0f, vector.x, EPSILON);
        assertEquals(0.0f, vector.y, EPSILON);
        assertEquals(0.0f, vector.z, EPSILON);
    }

    @Test
    void constructorStoresValues() {
        Vector3 vector = new Vector3(1, 2, 3);

        assertEquals(1.0f, vector.x, EPSILON);
        assertEquals(2.0f, vector.y, EPSILON);
        assertEquals(3.0f, vector.z, EPSILON);
    }

    @Test
    void copyConstructorCopiesValues() {
        Vector3 original = new Vector3(1, 2, 3);

        Vector3 copy = new Vector3(original);

        assertVectorEquals(original, copy);
        assertNotSame(original, copy);
    }

    @Test
    void staticFactoryMethodsReturnExpectedVectors() {
        assertVectorEquals(new Vector3(0, 0, 0), Vector3.zero());
        assertVectorEquals(new Vector3(1, 1, 1), Vector3.one());
        assertVectorEquals(new Vector3(1, 0, 0), Vector3.right());
        assertVectorEquals(new Vector3(0, 1, 0), Vector3.up());
        assertVectorEquals(new Vector3(0, 0, 1), Vector3.forward());
    }

    @Test
    void setCopiesValuesFromOtherVector() {
        Vector3 vector = new Vector3(1, 1, 1);

        Vector3 result = vector.set(new Vector3(2, 3, 4));

        assertSame(vector, result);
        assertVectorEquals(new Vector3(2, 3, 4), vector);
    }

    @Test
    void copyCreatesIndependentVector() {
        Vector3 original = new Vector3(1, 2, 3);

        Vector3 copy = original.copy();
        copy.x = 10;

        assertEquals(1.0f, original.x, EPSILON);
        assertEquals(10.0f, copy.x, EPSILON);
    }

    @Test
    void addMutatesAndReturnsSameVector() {
        Vector3 vector = new Vector3(1, 2, 3);

        Vector3 result = vector.add(new Vector3(4, 5, 6));

        assertSame(vector, result);
        assertVectorEquals(new Vector3(5, 7, 9), vector);
    }

    @Test
    void subMutatesAndReturnsSameVector() {
        Vector3 vector = new Vector3(5, 7, 9);

        Vector3 result = vector.sub(new Vector3(1, 2, 3));

        assertSame(vector, result);
        assertVectorEquals(new Vector3(4, 5, 6), vector);
    }

    @Test
    void mulMutatesAndReturnsSameVector() {
        Vector3 vector = new Vector3(1, 2, 3);

        Vector3 result = vector.mul(2);

        assertSame(vector, result);
        assertVectorEquals(new Vector3(2, 4, 6), vector);
    }

    @Test
    void divMutatesAndReturnsSameVector() {
        Vector3 vector = new Vector3(2, 4, 6);

        Vector3 result = vector.div(2);

        assertSame(vector, result);
        assertVectorEquals(new Vector3(1, 2, 3), vector);
    }

    @Test
    void lengthReturnsVectorMagnitude() {
        Vector3 vector = new Vector3(3, 4, 12);

        assertEquals(13.0f, vector.length(), EPSILON);
    }

    @Test
    void lengthSquaredReturnsSquaredMagnitude() {
        Vector3 vector = new Vector3(3, 4, 12);

        assertEquals(169.0f, vector.lengthSquared(), EPSILON);
    }

    @Test
    void normalizeMutatesAndReturnsSameVector() {
        Vector3 vector = new Vector3(3, 4, 0);

        Vector3 result = vector.normalize();

        assertSame(vector, result);
        assertEquals(1.0f, vector.length(), EPSILON);
        assertEquals(0.6f, vector.x, EPSILON);
        assertEquals(0.8f, vector.y, EPSILON);
        assertEquals(0.0f, vector.z, EPSILON);
    }

    @Test
    void normalizedReturnsUnitVectorWithoutMutatingOriginal() {
        Vector3 vector = new Vector3(3, 4, 0);

        Vector3 normalized = vector.normalized();

        assertNotSame(vector, normalized);
        assertEquals(1.0f, normalized.length(), EPSILON);
        assertVectorEquals(new Vector3(3, 4, 0), vector);
        assertEquals(0.6f, normalized.x, EPSILON);
        assertEquals(0.8f, normalized.y, EPSILON);
        assertEquals(0.0f, normalized.z, EPSILON);
    }

    @Test
    void dotReturnsDotProduct() {
        Vector3 first = new Vector3(1, 2, 3);
        Vector3 second = new Vector3(4, 5, 6);

        assertEquals(32.0f, first.dot(second), EPSILON);
    }

    @Test
    void crossReturnsPerpendicularVectorWithoutMutatingInputs() {
        Vector3 first = Vector3.right();
        Vector3 second = Vector3.up();

        Vector3 cross = Vector3.cross(first, second);

        assertVectorEquals(Vector3.forward(), cross);
        assertVectorEquals(new Vector3(1, 0, 0), first);
        assertVectorEquals(new Vector3(0, 1, 0), second);
    }

    @Test
    void rotateXMutatesAndReturnsSameVector() {
        Vector3 vector = new Vector3(0, 1, 0);

        Vector3 rotated = vector.rotateX(90);

        assertSame(vector, rotated);
        assertEquals(0.0f, vector.x, EPSILON);
        assertEquals(0.0f, vector.y, EPSILON);
        assertEquals(1.0f, vector.z, EPSILON);
    }

    @Test
    void rotateYMutatesAndReturnsSameVector() {
        Vector3 vector = new Vector3(0, 0, 1);

        Vector3 rotated = vector.rotateY(90);

        assertSame(vector, rotated);
        assertEquals(1.0f, vector.x, EPSILON);
        assertEquals(0.0f, vector.y, EPSILON);
        assertEquals(0.0f, vector.z, EPSILON);
    }

    @Test
    void rotateZMutatesAndReturnsSameVector() {
        Vector3 vector = new Vector3(1, 0, 0);

        Vector3 rotated = vector.rotateZ(90);

        assertSame(vector, rotated);
        assertEquals(0.0f, vector.x, EPSILON);
        assertEquals(1.0f, vector.y, EPSILON);
        assertEquals(0.0f, vector.z, EPSILON);
    }

    @Test
    void equalVectorsHaveSameHashCode() {
        Vector3 first = new Vector3(1, 2, 3);
        Vector3 second = new Vector3(1, 2, 3);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void toStringContainsCoordinates() {
        Vector3 vector = new Vector3(1, 2, 3);

        String text = vector.toString();

        assertTrue(text.contains("x=1.0"));
        assertTrue(text.contains("y=2.0"));
        assertTrue(text.contains("z=3.0"));
    }

    private static void assertVectorEquals(Vector3 expected, Vector3 actual) {
        assertEquals(expected.x, actual.x, EPSILON);
        assertEquals(expected.y, actual.y, EPSILON);
        assertEquals(expected.z, actual.z, EPSILON);
    }
}