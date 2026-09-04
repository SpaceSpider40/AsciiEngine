package com.space.engine.core.math;

public final class Vector3Pool {
    private static final int DEFAULT_CAPACITY = 100_000;

    private static float[] x;
    private static float[] y;
    private static float[] z;

    private static boolean[] isUsed;

    private static int[] freeIds;

    private static int capacity;
    private static int freeCount;
    private static int usedCount;

    static {
        init(DEFAULT_CAPACITY);
    }

    private Vector3Pool() {
    }

    /**
     * Get a new vector. REMEMBER TO FREE IT AFTER USE.
     * @return id
     */
    public static  int get() {
        if (freeCount == 0) {
            expand();
        }

        int id = freeIds[--freeCount];
        isUsed[id] = true;
        usedCount++;

        return id;
    }

    public static  void setX(int id, float value) {
        x[id] = value;
    }

    public static  void setY(int id, float value) {
        y[id] = value;
    }

    public static  void setZ(int id, float value) {
        z[id] = value;
    }

    public static  void set(int id, float _x, float _y, float _z) {
        x[id] = _x;
        y[id] = _y;
        z[id] = _z;
    }

    public static  void copy(int id, int otherId) {
        x[id] = x[otherId];
        y[id] = y[otherId];
        z[id] = z[otherId];
    }

    public static  void copy(int id, float[] src) {
        x[id] = src[0];
        y[id] = src[1];
        z[id] = src[2];
    }

    public static  void copy(int id, float[] src, int offset) {
        x[id] = src[offset];
        y[id] = src[offset + 1];
        z[id] = src[offset + 2];
    }

    public static  void set0(int id) {
        x[id] = 0;
        y[id] = 0;
        z[id] = 0;
    }

    public static  void set1(int id) {
        x[id] = 1;
        y[id] = 1;
        z[id] = 1;
    }

    public static  float getX(int id) {
        return x[id];
    }

    public static  float getY(int id) {
        return y[id];
    }

    public static  float getZ(int id) {
        return z[id];
    }

    /**
     * Adds the values of vector b to vector a.
     *
     * @param a id of vector a
     * @param b id of vector b
     */
    public static  void add(int a, int b) {
        x[a] += x[b];
        y[a] += y[b];
        z[a] += z[b];
    }

    /**
     * Adds the values of vector b to vector a.
     *
     * @param a  id of vector a
     * @param _x x value of vector b
     * @param _y y value of vector b
     * @param _z z value of vector b
     */
    public static  void add(int a, float _x, float _y, float _z) {
        x[a] += _x;
        y[a] += _y;
        z[a] += _z;
    }

    /**
     * Subtracts the values of vector b from vector a.
     *
     * @param a id of vector a
     * @param b id of vector b
     */
    public static  void sub(int a, int b) {
        x[a] -= x[b];
        y[a] -= y[b];
        z[a] -= z[b];
    }

    /**
     * Subtracts the values of vector b from vector a.
     *
     * @param a  id of vector a
     * @param _x x value of vector b
     * @param _y y value of vector b
     * @param _z z value of vector b
     */
    public static  void sub(int a, float _x, float _y, float _z) {
        x[a] -= _x;
        y[a] -= _y;
        z[a] -= _z;
    }

    /**
     * Multiplies the values of vector a by scalar.
     *
     * @param a      id of vector a
     * @param scalar scalar value
     */
    public static  void mul(int a, float scalar) {
        x[a] *= scalar;
        y[a] *= scalar;
        z[a] *= scalar;
    }

    /**
     * Divides the values of vector a by scalar.
     *
     * @param a      id of vector a
     * @param scalar scalar value
     */
    public static  void div(int a, float scalar) {
        x[a] /= scalar;
        y[a] /= scalar;
        z[a] /= scalar;
    }

    public static  void cross(int a, int b, int out) {
        x[out] = y[a] * z[b] - z[a] * y[b];
        y[out] = z[a] * x[b] - x[a] * z[b];
        z[out] = x[a] * y[b] - y[a] * x[b];
    }

    /**
     * Normalizes the values of vector.
     *
     * @param id id of vector
     */
    public static  void normalize(int id) {
        float length = length(id);
        x[id] /= length;
        y[id] /= length;
        z[id] /= length;
    }

    /**
     * Calculates the dot product of vectors a and b.
     *
     * @param a id of vector a
     * @param b id of vector b
     * @return dot product of vectors a and b
     */
    public static  float dot(int a, int b) {
        return x[a] * x[b] + y[a] * y[b] + z[a] * z[b];
    }

    /**
     * Calculates the length of vector.
     *
     * @param id id of vector
     * @return length of vector
     */
    public static  float length(int id) {
        return (float) Math.sqrt(x[id] * x[id] + y[id] * y[id] + z[id] * z[id]);
    }

    /**
     * Calculates the squared length of vector.
     *
     * @param id id of vector
     * @return squared length of vector
     */
    public static  float lengthSquared(int id) {
        return x[id] * x[id] + y[id] * y[id] + z[id] * z[id];
    }

    /**
     * Rotates vector around x axis.
     *
     * @param id  id of vector
     * @param deg angle in degrees
     */
    public static  void rotateX(int id, float deg) {
        float rad = (float) Math.toRadians(deg);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float _y = y[id];
        float _z = z[id];

        y[id] = _y * cos - _z * sin;
        z[id] = _y * sin + _z * cos;
    }

    /**
     * Rotates vector around y axis.
     *
     * @param id  id of vector
     * @param deg angle in degrees
     */
    public static  void rotateY(int id, float deg) {
        float rad = (float) Math.toRadians(deg);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float _x = x[id];
        float _z = z[id];

        x[id] = _x * cos - _z * sin;
        z[id] = _x * sin + _z * cos;
    }

    /**
     * Rotates vector around z axis.
     *
     * @param id  id of vector
     * @param deg angle in degrees
     */
    public static  void rotateZ(int id, float deg) {
        float rad = (float) Math.toRadians(deg);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float _x = x[id];
        float _y = y[id];

        x[id] = _x * cos - _y * sin;
        y[id] = _x * sin + _y * cos;
    }

    public static  void free(int id) {
        if (id < 0 || id >= capacity) {
            throw new IndexOutOfBoundsException("id: " + id + ", capacity: " + capacity);
        }

        if (!isUsed[id]) {
            return;
        }

        x[id] = 0;
        y[id] = 0;
        z[id] = 0;

        isUsed[id]           = false;
        freeIds[freeCount++] = id;
        usedCount--;
    }

    private static void init(int startCapacity) {
        capacity = startCapacity;

        x      = new float[capacity];
        y      = new float[capacity];
        z      = new float[capacity];
        isUsed = new boolean[capacity];

        freeIds = new int[capacity];

        for (int i = 0; i < capacity; i++) {
            freeIds[i] = capacity - 1 - i;
        }

        freeCount = capacity;
    }

    private static void expand() {
        int oldCapacity = capacity;
        int newCapacity = capacity * 2;

        float[] newX = new float[newCapacity];
        float[] newY = new float[newCapacity];
        float[] newZ = new float[newCapacity];

        boolean[] newIsUsed = new boolean[newCapacity];

        int[] newFreeIds = new int[newCapacity];

        System.arraycopy(x, 0, newX, 0, oldCapacity);
        System.arraycopy(y, 0, newY, 0, oldCapacity);
        System.arraycopy(z, 0, newZ, 0, oldCapacity);
        System.arraycopy(isUsed, 0, newIsUsed, 0, oldCapacity);
        System.arraycopy(freeIds, 0, newFreeIds, 0, oldCapacity);

        for (int i = newCapacity - 1; i >= oldCapacity; i--) {
            newFreeIds[freeCount++] = i;
        }

        x       = newX;
        y       = newY;
        z       = newZ;
        isUsed  = newIsUsed;
        freeIds = newFreeIds;

        capacity = newCapacity;
    }

}
