package com.space.engine.core.graphics;

import com.space.engine.core.math.Matrix44;

public class AsciiRendererV2 implements Rendering {
    private int width;
    private int height;

    private int[] screenBuffer;
    private int[] depthBuffer;
    private int[] colorBuffer;

    private float[] cameraPosition;
    private float[] cameraRotation;
    private float cameraNear;
    private float cameraFar;

    private Matrix44 viewMatrix;

    public void init(int width, int height) {
        this.width = width;
        this.height = height;

        int bufferSize = width * height;
        this.screenBuffer = new int[bufferSize];
        this.depthBuffer = new int[bufferSize];
        this.colorBuffer = new int[bufferSize];
    }

    public void camera(float[] position, float[] rotation, float near, float far) {
        cameraPosition = position;
        cameraRotation = rotation;

        cameraNear = near;
        cameraFar = far;

        viewMatrix = Matrix44.createViewMatrix(
                cameraPosition,
                cameraRotation);
    }

    // add some thing like submit that will take object and modifi them by matrixes

    /**
     * @param buffer layout: [9-vertices, 3-normal, 3-color] -> stride = 15
     *               transformation and normal should be after modification via
     *               modelviewmatrix
     */
    public void draw(float[] buffer) {
        final int stride = 15;

        for (int i = 0; i < buffer.length; i += stride) {
            float[] v0 = new float[3];
            float[] v1 = new float[3];
            float[] v2 = new float[3];
            float[] normal = new float[3];
            float[] color = new float[3];

            System.arraycopy(buffer, i, v0, 0, v0.length);
            System.arraycopy(buffer, i + 3, v1, 0, v1.length);
            System.arraycopy(buffer, i + 6, v2, 0, v2.length);
            System.arraycopy(buffer, i + 9, normal, 0, normal.length);
            System.arraycopy(buffer, i + 12, color, 0, color.length);

        }
    }
}
