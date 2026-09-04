package com.space.engine.core.math;

import java.util.Arrays;

public final class Matrix44Pool {
    private static final int DEFAULT_CAPACITY = 1000;
    private static final int STRIDE           = 16;

    private static float[] m;

    private static int[] freeIds;

    private static int capacity;
    private static int freeCount;
    private static int usedCount;

    static {
        init(DEFAULT_CAPACITY);
    }

    private Matrix44Pool() {
    }

    /**
     * gets a free matrix. REMEMBER TO FREE IT AFTER USE.
     *
     * @return id
     */
    public static   int get() {
        if (freeCount == 0) {
            expand();
        }

        int id = freeIds[--freeCount];
        usedCount++;

        return id;
    }

    public static   void setIdentity(int id) {
        Arrays.fill(m, id * STRIDE, (id + 1) * STRIDE, 0);
        m[id * STRIDE]      = 1;
        m[id * STRIDE + 5]  = 1;
        m[id * STRIDE + 10] = 1;
        m[id * STRIDE + 15] = 1;
    }

    public static   void setZero(int id) {
        Arrays.fill(m, id * STRIDE, (id + 1) * STRIDE, 0);
    }

    public static   void copy(int a, int b){
        int beginA = a * STRIDE;
        int beginB = b * STRIDE;
        System.arraycopy(m, beginB, m, beginA, STRIDE);
    }

    /**
     * Multiplies matrix A with matrix B and stores the result in matrix A.
     *
     * @param a matrix id
     * @param b matrix id
     */
    public static   void mul(int a, int b) {
        int beginA = a * STRIDE;
        int beginB = b * STRIDE;

        for (int r = 0; r < 4; r++) {
            int row = beginA + r * 4;

            float a0 = m[row];
            float a1 = m[row + 1];
            float a2 = m[row + 2];
            float a3 = m[row + 3];

            m[row] = a0 * m[beginB] +
                    a1 * m[beginB + 4] +
                    a2 * m[beginB + 8] +
                    a3 * m[beginB + 12];

            m[row + 1] = a0 * m[beginB + 1] +
                    a1 * m[beginB + 5] +
                    a2 * m[beginB + 9] +
                    a3 * m[beginB + 13];

            m[row + 2] = a0 * m[beginB + 2] +
                    a1 * m[beginB + 6] +
                    a2 * m[beginB + 10] +
                    a3 * m[beginB + 14];

            m[row + 3] = a0 * m[beginB + 3] +
                    a1 * m[beginB + 7] +
                    a2 * m[beginB + 11] +
                    a3 * m[beginB + 15];
        }
    }

    /**
     * does not mutate the matrix
     * @param id
     * @param inVecId
     * @param outVecId
     */
    public static   void mulPoint(int id, int inVecId, int outVecId) {
        int begin = id * STRIDE;

        float x = m[begin + 0] * Vector3Pool.getX(inVecId) + m[begin + 1] * Vector3Pool.getY(inVecId) + m[begin + 2] * Vector3Pool.getZ(inVecId) + m[begin + 3];
        float y = m[begin + 4] * Vector3Pool.getX(inVecId) + m[begin + 5] * Vector3Pool.getY(inVecId) + m[begin + 6] * Vector3Pool.getZ(inVecId) + m[begin + 7];
        float z = m[begin + 8] * Vector3Pool.getX(inVecId) + m[begin + 9] * Vector3Pool.getY(inVecId) + m[begin + 10] * Vector3Pool.getZ(inVecId) + m[begin + 11];
        float w = m[begin + 12] * Vector3Pool.getX(inVecId) + m[begin + 13] * Vector3Pool.getY(inVecId) + m[begin + 14] * Vector3Pool.getZ(inVecId) + m[begin + 15];

        if (Math.abs(w - 1) > 0.0001f && Math.abs(w) > 0.00001f) {
            x /= w;
            y /= w;
            z /= w;
        }

        Vector3Pool.set(outVecId, x, y, z);
    }

    public static   void mulVector(int id, int inVecId, int outVecId) {
        int begin = id * STRIDE;

        float x = m[begin + 0] * Vector3Pool.getX(inVecId) + m[begin + 1] * Vector3Pool.getY(inVecId) + m[begin + 2] * Vector3Pool.getZ(inVecId);
        float y = m[begin + 4] * Vector3Pool.getX(inVecId) + m[begin + 5] * Vector3Pool.getY(inVecId) + m[begin + 6] * Vector3Pool.getZ(inVecId);
        float z = m[begin + 8] * Vector3Pool.getX(inVecId) + m[begin + 9] * Vector3Pool.getY(inVecId) + m[begin + 10] * Vector3Pool.getZ(inVecId);

        Vector3Pool.set(outVecId, x, y, z);
    }

    public static   int getTranslationMatrix(float x, float y, float z) {
        int id = get();
        setIdentity(id);
        setTranslationMatrix(id, x, y, z);
        return id;
    }

    public static   void setTranslationMatrix(int id, float x, float y, float z) {
        int begin = id * STRIDE;
        setIdentity(id);
        m[begin + 3]  = x;
        m[begin + 7]  = y;
        m[begin + 11] = z;
    }

    public static   int getRotationXMatrix(float deg) {
        int   id    = get();
        setIdentity(id);
        int   begin = id * STRIDE;
        float rad   = (float) Math.toRadians(deg);
        float cos   = (float) Math.cos(rad);
        float sin   = (float) Math.sin(rad);

        m[begin + 5]  = cos;
        m[begin + 6]  = -sin;
        m[begin + 9]  = sin;
        m[begin + 10] = cos;

        return id;
    }

    public static   int getRotationYMatrix(float deg) {
        int   id    = get();
        setIdentity(id);
        int   begin = id * STRIDE;
        float rad   = (float) Math.toRadians(deg);
        float cos   = (float) Math.cos(rad);
        float sin   = (float) Math.sin(rad);

        m[begin + 0]  = cos;
        m[begin + 2]  = sin;
        m[begin + 8]  = -sin;
        m[begin + 10] = cos;

        return id;
    }

    public static   int getRotationZMatrix(float deg) {
        int   id    = get();
        setIdentity(id);
        int   begin = id * STRIDE;
        float rad   = (float) Math.toRadians(deg);
        float cos   = (float) Math.cos(rad);
        float sin   = (float) Math.sin(rad);

        m[begin + 0] = cos;
        m[begin + 1] = -sin;
        m[begin + 4] = sin;
        m[begin + 5] = cos;

        return id;
    }

    public static   int getScaleMatrix(float x, float y, float z) {
        int id    = get();
        setIdentity(id);
        int begin = id * STRIDE;

        m[begin + 0]  = x;
        m[begin + 5]  = y;
        m[begin + 10] = z;

        return id;
    }

    public static   int getModelMatrix(
            int positionId,
            int rotationId,
            int scaleId
    ) {
        int translation = getTranslationMatrix(
                Vector3Pool.getX(positionId),
                Vector3Pool.getY(positionId),
                Vector3Pool.getZ(positionId)
        );

        int rotX = getRotationXMatrix(Vector3Pool.getX(rotationId));
        int rotY = getRotationYMatrix(Vector3Pool.getY(rotationId));
        int rotZ = getRotationZMatrix(Vector3Pool.getZ(rotationId));

        mul(rotY, rotZ);
        mul(rotY, rotX);

        int scaleMatrix = getScaleMatrix(
                Vector3Pool.getX(scaleId),
                Vector3Pool.getY(scaleId),
                Vector3Pool.getZ(scaleId)
        );

        mul(translation, scaleMatrix);
        mul(translation, rotY);

        free(scaleMatrix);
        free(rotX);
        free(rotY);
        free(rotZ);

        return translation;
    }

    public static   void setModelMatrix(int id, int positionId, int rotationId, int scaleId) {

        setTranslationMatrix(
                id,
                Vector3Pool.getX(positionId),
                Vector3Pool.getY(positionId),
                Vector3Pool.getZ(positionId)
        );

        int rotX = getRotationXMatrix(Vector3Pool.getX(rotationId));
        int rotY = getRotationYMatrix(Vector3Pool.getY(rotationId));
        int rotZ = getRotationZMatrix(Vector3Pool.getZ(rotationId));

        mul(rotY, rotZ);
        mul(rotY, rotX);

        int scaleMatrix = getScaleMatrix(
                Vector3Pool.getX(scaleId),
                Vector3Pool.getY(scaleId),
                Vector3Pool.getZ(scaleId)
        );

        mul(id, scaleMatrix);
        mul(id, rotY);

        free(scaleMatrix);
        free(rotX);
        free(rotY);
        free(rotZ);
    }

    public static   int getViewMatrix(
            int cameraPositionId,
            int cameraRotationId
    ) {
        int invTranslation = getTranslationMatrix(
                -Vector3Pool.getX(cameraPositionId),
                -Vector3Pool.getY(cameraPositionId),
                -Vector3Pool.getZ(cameraPositionId)
        );

        int invRotX = getRotationXMatrix(-Vector3Pool.getX(cameraRotationId));
        int invRotY = getRotationYMatrix(-Vector3Pool.getY(cameraRotationId));
        int invRotZ = getRotationZMatrix(-Vector3Pool.getZ(cameraRotationId));

        mul(invRotZ, invRotY);
        mul(invRotZ, invRotX);

        mul(invTranslation, invRotZ);

        free(invRotZ);
        free(invRotY);
        free(invRotX);

        return invTranslation;
    }

    public static   void setViewMatrix(int id, int cameraPositionId, int cameraRotationId) {
        setTranslationMatrix(id, -Vector3Pool.getX(cameraPositionId), -Vector3Pool.getY(cameraPositionId), -Vector3Pool.getZ(cameraPositionId));

        int invRotX = getRotationXMatrix(-Vector3Pool.getX(cameraRotationId));
        int invRotY = getRotationYMatrix(-Vector3Pool.getY(cameraRotationId));
        int invRotZ = getRotationZMatrix(-Vector3Pool.getZ(cameraRotationId));

        mul(invRotZ, invRotY);
        mul(invRotZ, invRotX);

        mul(id, invRotZ);

        free(invRotX);
        free(invRotZ);
        free(invRotY);
    }

    public static   void free(int id) {
        if (id < 0 || id >= capacity) {
            throw new IndexOutOfBoundsException("id: " + id + ", capacity: " + capacity);
        }

        Arrays.fill(m, id * STRIDE, (id + 1) * STRIDE, 0);

        freeIds[freeCount++] = id;
        usedCount--;
    }

    private static void init(int startingCapacity) {
        capacity = startingCapacity;

        m = new float[capacity * STRIDE];

        freeIds = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            freeIds[i] = capacity - 1 - i;
        }

        freeCount = capacity;
    }

    private static void expand() {
        int oldCapacity = capacity;
        int newCapacity = capacity * 2;

        float[] newM       = new float[newCapacity * STRIDE];
        int[]   newFreeIds = new int[newCapacity];

        System.arraycopy(m, 0, newM, 0, oldCapacity * STRIDE);
        System.arraycopy(freeIds, 0, newFreeIds, 0, oldCapacity);

        for (int i = newCapacity - 1; i >= oldCapacity; i--) {
            newFreeIds[freeCount++] = i;
        }

        m       = newM;
        freeIds = newFreeIds;

        capacity = newCapacity;
    }
}
