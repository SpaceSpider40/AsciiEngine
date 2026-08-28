package com.space.engine.core.graphics;

import com.space.engine.core.Debug;
import com.space.engine.core.World;
import com.space.engine.core.components.*;
import com.space.engine.core.math.Matrix44;
import com.space.engine.core.math.Triangle;
import com.space.engine.core.math.Vector3;

import java.util.Arrays;

@SuppressWarnings("D")
public class AsciiRenderer implements IRenderer {

    private int width;
    private int height;

    private final float ambientLight = 0.15f;

    private char[] frameBuffer;
    private float[] zBuffer;
    private int[] colorBuffer;

    private final String ramp = ".,-~:;=!*#$@";
    private final Vector3 lightDirection = new Vector3(1.0f, -2.0f, -1.5f).normalized();

    @Override
    public void init(
            int width,
            int height) {
        this.width = width;
        this.height = height;

        frameBuffer = new char[width * height];
        zBuffer = new float[width * height];
        colorBuffer = new int[width * height];

        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    @Override
    public void render(World world) {
        clear();

        var ecs = world.getEntityRegistry();

        var entities = ecs.view(
                TransformComponent.class,
                MeshComponent.class);

        var cameras = ecs.view(
                CameraComponent.class,
                ActiveCameraComponent.class,
                TransformComponent.class);

        if (cameras.isEmpty())
            return;

        int camera = cameras.stream().findFirst().orElseThrow();
        var cameraTransform = ecs.get(camera, TransformComponent.class);
        var cameraComponent = ecs.get(camera, CameraComponent.class);
        Matrix44 viewMatrix = Matrix44.createViewMatrix(
                cameraTransform.position(),
                cameraTransform.rotation());

        for (var entity : entities) {
            // LAYER 0 - 3D
            var transform = ecs.get(entity, TransformComponent.class);
            var mesh = ecs.get(entity, MeshComponent.class);

            Matrix44 modelMatrix = Matrix44.createModelMatrix(
                    transform.position(),
                    transform.rotation(),
                    transform.scale());

            Matrix44 modelViewMatrix = viewMatrix.mul(modelMatrix);

            for (Triangle tris : mesh.tris()) {
                // transform
                var w0 = modelViewMatrix.mulPoint(tris.v0());
                var w1 = modelViewMatrix.mulPoint(tris.v1());
                var w2 = modelViewMatrix.mulPoint(tris.v2());

                // rotate normal
                // todo: the proper graphics engineering fix is to transform your normals using
                // the transpose of the inverse of the model matrix
                var wNormal = modelMatrix
                        .mulVector(tris.normal())
                        .normalized();

                // todo: fix as it works weirdly
                // Backface culling
                // var viewDir = w0.normalize();
                // if (wNormal.dot(viewDir) >= 0) continue;

                // near plance clipping
                // todo: fix as it disappears entire plane, it would be better to clip it
                if (w0.z <= cameraComponent.near() || w1.z <= cameraComponent.near() || w2.z <= cameraComponent.near())
                    continue;

                // light
                var diffuse = wNormal.dot(lightDirection);
                var intensity = Math.max(0.0f, diffuse) * 0.85f + ambientLight;
                var ch = ramp.charAt((int) (intensity * (ramp.length() - 1)));

                // perspective projection
                var p0 = project(w0, cameraComponent.fov());
                var p1 = project(w1, cameraComponent.fov());
                var p2 = project(w2, cameraComponent.fov());

                // bounding-box
                int minX = (int) Math.max(0, Math.min(p0.x, Math.min(p1.x, p2.x)));
                int maxX = (int) Math.min(width - 1, Math.max(p0.x, Math.max(p1.x, p2.x)));
                int minY = (int) Math.max(0, Math.min(p0.y, Math.min(p1.y, p2.y)));
                int maxY = (int) Math.min(height - 1, Math.max(p0.y, Math.max(p1.y, p2.y)));

                // rasterisation
                for (int y = minY; y <= maxY; y++) {
                    for (int x = minX; x <= maxX; x++) {
                        // Check containment using edge sub-functions
                        float w0Edge = edgeFunction(p1, p2, x, y);
                        float w1Edge = edgeFunction(p2, p0, x, y);
                        float w2Edge = edgeFunction(p0, p1, x, y);

                        // inside test regardless of CW or CCW triangle generation order
                        if ((w0Edge >= 0 && w1Edge >= 0 && w2Edge >= 0) ||
                                (w0Edge <= 0 && w1Edge <= 0 && w2Edge <= 0)) {
                            float totalArea = w0Edge + w1Edge + w2Edge;
                            if (Math.abs(totalArea) < 0.0001f)
                                continue;

                            // Barycentric interpolation tracking depth shifts across the face surface
                            float b0 = w0Edge / totalArea;
                            float b1 = w1Edge / totalArea;
                            float b2 = w2Edge / totalArea;

                            float pixelZ = b0 * p0.z + b1 * p1.z + b2 * p2.z;
                            float ooz = 1.0f / pixelZ;

                            int bufferIdx = x + y * width;
                            if (ooz > zBuffer[bufferIdx]) {
                                zBuffer[bufferIdx] = ooz;
                                frameBuffer[bufferIdx] = ch;

                                // colors todo: add shading
                                if (ecs.has(entity, MaterialComponent.class)) {
                                    colorBuffer[bufferIdx] = ecs.get(entity, MaterialComponent.class).albedo().pack();
                                } else {
                                    colorBuffer[bufferIdx] = Color.MAGENTA.pack();
                                }
                            }
                        }
                    }
                }

            }
        }

        // LAYER 1 - INTERFACE

        // LAYER 2 - DEBUG
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

        // DRAW
        StringBuilder sb = new StringBuilder(width * height);
        for (int i = 0; i < width * height; i++) {

            int color = colorBuffer[i];

            // todo: optimise by not repeating same commands
            sb
                    // position
                    .append("\033[")
                    .append((i / width) + 1)
                    .append(";")
                    .append((i % width) + 1)
                    .append("H")
                    // color
                    .append("\033[38;2;")
                    .append(Color.unpackR(color))
                    .append(";")
                    .append(Color.unpackG(color))
                    .append(";")
                    .append(Color.unpackB(color))
                    .append("m")
                    .append(frameBuffer[i]);
        }
        System.out.print(sb);
        System.out.flush();
    }

    private Vector3 project(Vector3 cameraSpace, float fov) {
        var ooz = 1.0f / (cameraSpace.z);
        return new Vector3(
                (width / 2.0f + (fov * 2.2f) * cameraSpace.x * ooz),
                (height / 2.0f - fov * cameraSpace.y * ooz),
                cameraSpace.z);
    }

    private float edgeFunction(Vector3 a, Vector3 b, float px, float py) {
        return (px - a.x) * (b.y - a.y) - (py - a.y) * (b.x - a.x);
    }

    private void clear() {
        Arrays.fill(frameBuffer, ' ');
        Arrays.fill(zBuffer, 0.0f);
        Arrays.fill(colorBuffer, 0);
    }
}
