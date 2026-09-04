package com.space.engine.core.graphics;

import com.space.engine.core.Debug;
import com.space.engine.core.graphics.ui.UIElement;
import com.space.engine.core.math.Matrix44Pool;
import com.space.engine.core.math.SMath;
import com.space.engine.core.math.Vector3;
import com.space.engine.core.math.Vector3Pool;

import java.util.ArrayList;
import java.util.Arrays;

public class AsciiRendererV2 implements Renderer {
    private final String  ramp           = ".,-~:;=!*#$@";
    private final float   ambientLight   = 0.15f;
    private final Vector3 lightDirection = new Vector3(1.0f, -2.0f, -1.5f).normalized();

    final int stride = 15;

    private int width;
    private int height;

    private char[]  frameBuffer;
    private float[] depthBuffer;
    private int[]   colorBuffer;

    // camera
    private int   cameraPosition = Vector3Pool.get();
    private int   cameraRotation = Vector3Pool.get();
    private float cameraNear     = 0.01f;
    private float cameraFar      = 3000f;
    private float cameraFov      = 60f;

    private int viewMatrix = Matrix44Pool.get();

    private float[] vbo     = new float[1000 * stride]; // default to 100 visual units
    private int     vboTail = 0;

    private ArrayList<UIElement> uiElements = new ArrayList<>();

    public void init(int width, int height) {
        this.width  = width;
        this.height = height;

        int bufferSize = width * height;
        this.frameBuffer = new char[bufferSize];
        this.depthBuffer = new float[bufferSize];
        this.colorBuffer = new int[bufferSize];

        System.out.print("\033[H\033[2J");
        System.out.flush();

        camera(new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, this.cameraNear, this.cameraFar, this.cameraFov);
    }

    public void camera(float[] position, float[] rotation, float near, float far, float fov) {
        Vector3Pool.set(cameraPosition, position[0], position[1], position[2]);
        Vector3Pool.set(cameraRotation, rotation[0], rotation[1], rotation[2]);

        cameraNear = near;
        cameraFar  = far;
        cameraFov  = fov;

        Matrix44Pool.setViewMatrix(
                viewMatrix,
                cameraPosition,
                cameraRotation
        );
    }

    @Override
    public void submit(UIElement obj) {
        uiElements.add(obj); //todo: move ui element to static array to bypass garbage collection
    }

//    float[] w0 = new float[3];
//    float[] w1 = new float[3];
//    float[] w2 = new float[3];

    //todo: temp solution
    int viPosition = Vector3Pool.get();
    int viRotation = Vector3Pool.get();
    int viScale    = Vector3Pool.get();

    int modelMatrix     = Matrix44Pool.get();
    int modelViewMatrix = Matrix44Pool.get();

    int trixV0 = Vector3Pool.get();
    int trixV1 = Vector3Pool.get();
    int trixV2 = Vector3Pool.get();

    int w0 = Vector3Pool.get();
    int w1 = Vector3Pool.get();
    int w2 = Vector3Pool.get();
    int wN = Vector3Pool.get();
    int wC = Vector3Pool.get();

    public void submit(RenderObject ro) {

        var vi = ro.getVisualInstance();

        if (vi == null) return;

        expandVBO(vi.tris().length * stride);// expands if vbo runs out of space for verts

        Vector3Pool.set(viPosition, vi.position().x, vi.position().y, vi.position().z);
        Vector3Pool.set(viRotation, vi.rotation().x, vi.rotation().y, vi.rotation().z);
        Vector3Pool.set(viScale, vi.scale().x, vi.scale().y, vi.scale().z);
        Matrix44Pool.setModelMatrix(modelMatrix, viPosition, viRotation, viScale);

//        Matrix44 modelMatrix = Matrix44.createModelMatrix(
//                vi.position(),
//                vi.rotation(),
//                vi.scale()
//        );

//        Matrix44 modelViewMatrix = viewMatrix.mul(modelMatrix);

        Matrix44Pool.copy(modelViewMatrix, viewMatrix);
        Matrix44Pool.mul(modelViewMatrix, modelMatrix);

        for (int i = 0; i < vi.tris().length; i++) {
            if (vi.tris()[i] == null) continue;

            var tris = vi.tris()[i];
            var color = vi.colors()[0];

            Vector3Pool.set(trixV0, tris.verts[0], tris.verts[1], tris.verts[2]);
            Vector3Pool.set(trixV1, tris.verts[3], tris.verts[4], tris.verts[5]);
            Vector3Pool.set(trixV2, tris.verts[6], tris.verts[7], tris.verts[8]);

            Matrix44Pool.mulPoint(modelViewMatrix, trixV0, w0);
            Matrix44Pool.mulPoint(modelViewMatrix, trixV1, w1);
            Matrix44Pool.mulPoint(modelViewMatrix, trixV2, w2);

            Vector3Pool.set(wN, tris.getNormalX(), tris.getNormalY(), tris.getNormalZ());
            Matrix44Pool.mulVector(modelMatrix, wN, wN);

            vbo[vboTail + 0] = Vector3Pool.getX(w0);
            vbo[vboTail + 1] = Vector3Pool.getY(w0);
            vbo[vboTail + 2] = Vector3Pool.getZ(w0);
            vbo[vboTail + 3] = Vector3Pool.getX(w1);
            vbo[vboTail + 4] = Vector3Pool.getY(w1);
            vbo[vboTail + 5] = Vector3Pool.getZ(w1);
            vbo[vboTail + 6] = Vector3Pool.getX(w2);
            vbo[vboTail + 7] = Vector3Pool.getY(w2);
            vbo[vboTail + 8] = Vector3Pool.getZ(w2);
            vbo[vboTail + 9] = Vector3Pool.getX(wN);
            vbo[vboTail + 10] = Vector3Pool.getY(wN);
            vbo[vboTail + 11] = Vector3Pool.getZ(wN);
            vbo[vboTail + 12] = color.getR();
            vbo[vboTail + 13] = color.getG();
            vbo[vboTail + 14] = color.getB();

//            System.arraycopy(w0, 0, vbo, vboTail + 0, w0.length);
//            System.arraycopy(w1, 0, vbo, vboTail + 3, w1.length);
//            System.arraycopy(w2, 0, vbo, vboTail + 6, w2.length);
//            System.arraycopy(wN, 0, vbo, vboTail + 9, wN.length);
//            System.arraycopy(c0, 0, vbo, vboTail + 12, c0.length);

            vboTail += stride;
        }
    }

    public void draw() {
        clear();
        internalDraw(vbo);

        vboTail = 0;
        uiElements.clear();
    }

    private final float[] v0     = new float[3];
    private final float[] v1     = new float[3];
    private final float[] v2     = new float[3];
    private final float[] normal = new float[3];
    private final float[] color  = new float[3];

    /**
     * @param buffer layout: [9-vertices, 3-normal, 3-color] -> stride = 15
     *               transformation and normal should be after modification via
     *               modelviewmatrix
     */
    private void internalDraw(float[] buffer) {

        // Layer - 0 - 3D
        for (int i = 0; i < vboTail; i += stride) {

            System.arraycopy(buffer, i, v0, 0, v0.length);
            System.arraycopy(buffer, i + 3, v1, 0, v1.length);
            System.arraycopy(buffer, i + 6, v2, 0, v2.length);
            System.arraycopy(buffer, i + 9, normal, 0, normal.length);
            System.arraycopy(buffer, i + 12, color, 0, color.length);

            // todo: add backface culling

            // near plane clipping todo: improve as this is buggy for big tris
            if (v0[2] < cameraNear ||
                    v1[2] < cameraNear ||
                    v2[2] < cameraNear) {
                continue;
            }

            // far plane clipping
            if (v0[2] > cameraFar ||
                    v1[2] > cameraFar ||
                    v2[2] > cameraFar) {
                continue;
            }

            // simple directional global light
            float diffuse   = SMath.VectorDot(normal, lightDirection.asFloats());
            float intensity = (Math.max(0.0f, diffuse) * (1.0f - ambientLight)) + ambientLight;
            char  ch        = ramp.charAt((int) (intensity * ramp.length() - 1));

            // perspective projection
            //todo: change p -> v
            projectPerspective(v0, v0);
            projectPerspective(v1, v1);
            projectPerspective(v2, v2);

            int minX = (int) Math.max(0, Math.min(v0[0], Math.min(v1[0], v2[0])));
            int maxX = (int) Math.min(width - 1, Math.max(v0[0], Math.max(v1[0], v2[0])));
            int minY = (int) Math.max(0, Math.min(v0[1], Math.min(v1[1], v2[1])));
            int maxY = (int) Math.min(height - 1, Math.max(v0[1], Math.max(v1[1], v2[1])));

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    float w0Edge = edgeFunction(v1, v2, x, y);
                    float w1Edge = edgeFunction(v2, v0, x, y);
                    float w2Edge = edgeFunction(v0, v1, x, y);

                    if ((w0Edge >= 0 && w1Edge >= 0 && w2Edge >= 0) ||
                            (w0Edge <= 0 && w1Edge <= 0 && w2Edge <= 0)) {

                        float totalArea = w0Edge + w1Edge + w2Edge;
                        if (Math.abs(totalArea) < 0.0001f)
                            continue;

                        float b0 = w0Edge / totalArea;
                        float b1 = w1Edge / totalArea;
                        float b2 = w2Edge / totalArea;

                        float pixelZ = b0 * v0[2] + b1 * v1[2] + b2 * v2[2];
                        float ooz    = 1.0f / pixelZ;

                        int bufferIdx = x + y * width;
                        if (ooz > depthBuffer[bufferIdx]) {
                            depthBuffer[bufferIdx] = ooz;
                            frameBuffer[bufferIdx] = ch;

                            // todo: implement color blending, increase stride and pass a color for each
                            // vert
                            colorBuffer[bufferIdx] = Color.pack((short) color[0], (short) color[1], (short) color[2]);
                        }
                    }
                }
            }
        }

        // Layer - 1 - UI
        for (UIElement element : uiElements) {
            int    elementX      = element.getX();
            int    elementY      = element.getY();
            int    elementWidth  = element.getWidth();
            int    elementHeight = element.getHeight();
            char[] elementChars  = element.getChars();

            for (int localY = 0; localY < elementHeight; localY++) {
                int screenY = elementY + localY;

                if (screenY < 0 || screenY >= height) {
                    continue;
                }

                for (int localX = 0; localX < elementWidth; localX++) {
                    int screenX = elementX + localX;

                    if (screenX < 0 || screenX >= width) {
                        continue;
                    }

                    int elementIndex = localX + localY * elementWidth;
                    int frameIndex   = screenX + screenY * width;

                    char c = elementChars[elementIndex];

                    if (c == '\0') {
                        continue;
                    }

                    frameBuffer[frameIndex] = c;
                    colorBuffer[frameIndex] = Color.WHITE.pack(); // todo: handle UI colors
                }
            }
        }

        // Layer - 2 - DEBUG MSG
        int messageY = 0;
        for (Debug.Message message : Debug.getMessages()) {
            int messageX = 0;

            // if (messageY - 1 >= height) break;

            for (char c : message.msg().toCharArray()) {
                if (messageX >= width)
                    break;

                int bufferIdx = messageX + messageY * width;

                if (bufferIdx >= frameBuffer.length)
                    break;

                frameBuffer[bufferIdx] = c;
                colorBuffer[bufferIdx] = message.color().pack();

                messageX++;
            }
            messageY++;
        }

        // Print
        int           bufferLength = width * height;
        StringBuilder sb           = new StringBuilder(bufferLength);
        for (int i = 0; i < bufferLength; i++) {
            int color     = colorBuffer[i];
            int prevColor = -1;
            if (i - 1 >= 0)
                prevColor = colorBuffer[i - 1];

            if (i % width == 0) {
                sb.append("\033[")
                  .append((i / width) + 1)
                  .append(";")
                  .append((i % width) + 1)
                  .append("H");
            }

            if (prevColor == -1 || color != prevColor) {
                sb.append("\033[38;2;")
                  .append(Color.unpackR(color))
                  .append(";")
                  .append(Color.unpackG(color))
                  .append(";")
                  .append(Color.unpackB(color))
                  .append("m");
            }

            sb.append(frameBuffer[i]);

        }

        System.out.print(sb);
        System.out.flush();
    }

    private float[] projectPerspective(float[] cameraSpace) {
        float ooz = 1.0f / cameraSpace[2];
        return new float[]{
                (width / 2.0f + (cameraFov * 2.2f) * cameraSpace[0] * ooz),
                (height / 2.0f - cameraFov * cameraSpace[1] * ooz),
                cameraSpace[2]
        };
    }

    private void projectPerspective(float[] cameraSpace, float[] result) {
        float ooz = 1.0f / cameraSpace[2];
        result[0] = (width / 2.0f + (cameraFov * 2.2f) * cameraSpace[0] * ooz);
        result[1] = (height / 2.0f - cameraFov * cameraSpace[1] * ooz);
        result[2] = cameraSpace[2];
    }

    private float edgeFunction(float[] a, float[] b, float px, float py) {
        return (px - a[0]) * (b[1] - a[1]) - (py - a[1]) * (b[0] - a[0]);
    }

    private void clear() {
        Arrays.fill(frameBuffer, ' ');
        Arrays.fill(depthBuffer, 0.0f);
        Arrays.fill(colorBuffer, 0);
    }

    private void expandVBO(int toAlloc) {
        int spaceLeft = vbo.length - vboTail;
        if (spaceLeft < toAlloc) {
            float[] newVBO = new float[vbo.length + (1000 * stride)];
            System.arraycopy(vbo, 0, newVBO, 0, vbo.length);
            vbo = newVBO;
        }
    }
}
