package com.engine.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Matrix44Test {

    private static final float EPSILON = 0.0001f;

    @Test
    void constructorCreatesIdentityMatrix() {
        Matrix44 matrix = new Matrix44();

        assertMatrixEquals(new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        }, matrix);
    }

    @Test
    void setIdentityResetsMatrixToIdentity() {
        Matrix44 matrix = Matrix44.createTranslation(1, 2, 3);

        matrix.setIdentity();

        assertMatrixEquals(new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        }, matrix);
    }

    @Test
    void identityMatrixDoesNotChangePoint() {
        Matrix44 matrix = new Matrix44();
        Vector3 point = new Vector3(1, 2, 3);

        Vector3 result = matrix.mulPoint(point);

        assertVectorEquals(new Vector3(1, 2, 3), result);
    }

    @Test
    void identityMatrixDoesNotChangeVector() {
        Matrix44 matrix = new Matrix44();
        Vector3 vector = new Vector3(1, 2, 3);

        Vector3 result = matrix.mulVector(vector);

        assertVectorEquals(new Vector3(1, 2, 3), result);
    }

    @Test
    void translationMovesPoint() {
        Matrix44 translation = Matrix44.createTranslation(10, 20, 30);

        Vector3 result = translation.mulPoint(new Vector3(1, 2, 3));

        assertVectorEquals(new Vector3(11, 22, 33), result);
    }

    @Test
    void translationDoesNotAffectVectorDirection() {
        Matrix44 translation = Matrix44.createTranslation(10, 20, 30);

        Vector3 result = translation.mulVector(new Vector3(1, 2, 3));

        assertVectorEquals(new Vector3(1, 2, 3), result);
    }

    @Test
    void scaleScalesPoint() {
        Matrix44 scale = Matrix44.createScale(2, 3, 4);

        Vector3 result = scale.mulPoint(new Vector3(1, 2, 3));

        assertVectorEquals(new Vector3(2, 6, 12), result);
    }

    @Test
    void scaleScalesVector() {
        Matrix44 scale = Matrix44.createScale(2, 3, 4);

        Vector3 result = scale.mulVector(new Vector3(1, 2, 3));

        assertVectorEquals(new Vector3(2, 6, 12), result);
    }

    @Test
    void rotationXRotatesPointAroundXAxis() {
        Matrix44 rotation = Matrix44.createRotationX((float) Math.toRadians(90));

        Vector3 result = rotation.mulPoint(new Vector3(0, 1, 0));

        assertVectorEquals(new Vector3(0, 0, 1), result);
    }

    @Test
    void rotationYRotatesPointAroundYAxis() {
        Matrix44 rotation = Matrix44.createRotationY((float) Math.toRadians(90));

        Vector3 result = rotation.mulPoint(new Vector3(0, 0, 1));

        assertVectorEquals(new Vector3(1, 0, 0), result);
    }

    @Test
    void rotationZRotatesPointAroundZAxis() {
        Matrix44 rotation = Matrix44.createRotationZ((float) Math.toRadians(90));

        Vector3 result = rotation.mulPoint(new Vector3(1, 0, 0));

        assertVectorEquals(new Vector3(0, 1, 0), result);
    }

    @Test
    void matrixMultiplicationCombinesTransformsInOrder() {
        Matrix44 translation = Matrix44.createTranslation(10, 0, 0);
        Matrix44 scale = Matrix44.createScale(2, 2, 2);

        Matrix44 combined = translation.mul(scale);
        Vector3 result = combined.mulPoint(new Vector3(1, 1, 1));

        assertVectorEquals(new Vector3(12, 2, 2), result);
    }

    @Test
    void createModelMatrixAppliesScaleRotationAndTranslation() {
        Matrix44 model = Matrix44.createModelMatrix(
                new Vector3(10, 20, 30),
                new Vector3(0, 0, 0),
                new Vector3(2, 3, 4)
        );

        Vector3 result = model.mulPoint(new Vector3(1, 1, 1));

        assertVectorEquals(new Vector3(12, 23, 34), result);
    }

    @Test
    void createModelMatrixAppliesRotation() {
        Matrix44 model = Matrix44.createModelMatrix(
                Vector3.zero(),
                new Vector3(0, 0, 90),
                Vector3.one()
        );

        Vector3 result = model.mulPoint(new Vector3(1, 0, 0));

        assertVectorEquals(new Vector3(0, 1, 0), result);
    }

    @Test
    void createViewMatrixMovesWorldOppositeCameraPosition() {
        Matrix44 view = Matrix44.createViewMatrix(
                new Vector3(10, 20, 30),
                Vector3.zero()
        );

        Vector3 result = view.mulPoint(new Vector3(11, 22, 33));

        assertVectorEquals(new Vector3(1, 2, 3), result);
    }

    @Test
    void createViewMatrixWithZeroCameraIsIdentity() {
        Matrix44 view = Matrix44.createViewMatrix(
                Vector3.zero(),
                Vector3.zero()
        );

        Vector3 result = view.mulPoint(new Vector3(1, 2, 3));

        assertVectorEquals(new Vector3(1, 2, 3), result);
    }

    private static void assertVectorEquals(Vector3 expected, Vector3 actual) {
        assertEquals(expected.x, actual.x, EPSILON);
        assertEquals(expected.y, actual.y, EPSILON);
        assertEquals(expected.z, actual.z, EPSILON);
    }

    private static void assertMatrixEquals(float[] expected, Matrix44 actual) {
        assertEquals(expected.length, actual.m.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.m[i], EPSILON, "Matrix value differs at index " + i);
        }
    }
}