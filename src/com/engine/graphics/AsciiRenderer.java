package com.engine.graphics;

import com.engine.World;
import com.engine.ecs.components.MeshComponent;
import com.engine.ecs.components.TransformComponent;
import com.engine.math.Triangle;
import com.engine.math.Vector2;
import com.engine.math.Vector3;

import java.util.Arrays;

@SuppressWarnings("D")
public class AsciiRenderer implements IRenderer {

    private int width;
    private int height;

    private final float ambientLight   = 0.15f;
    private final float cameraDistance = 20f;

    private char[]  frameBuffer;
    private float[] zBuffer;

    private final String  ramp           = ".,-~:;=!*#$@";
    private final Vector3 lightDirection = new Vector3(1.0f, -2.0f, -1.5f).normalize();

    @Override
    public void init(
            int width,
            int height
    ) {
        this.width  = width;
        this.height = height;

        frameBuffer = new char[width * height];
        zBuffer     = new float[width * height];

        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    @Override
    public char[][] render(World world) {
        clear();

        var ecs = world.ecsRegistry;

        var entities = ecs.view(
                TransformComponent.class,
                MeshComponent.class
        );

        for (var entity : entities) {
            var transform = ecs.get(entity, TransformComponent.class);
            var mesh      = ecs.get(entity, MeshComponent.class);

            for (Triangle tris : mesh.tris()) {
                //transform
                var w0 = transform(tris.v0, transform);
                var w1 = transform(tris.v1, transform);
                var w2 = transform(tris.v2, transform);

                //rotate normal
                var wNormal = tris
                        .normal
                        .rotateX(transform.rotation().x)
                        .rotateY(transform.rotation().y)
                        .rotateZ(transform.rotation().z)
                        .normalize();

                var diffuse   = wNormal.dot(lightDirection);
                var intensity = Math.max(0.0f, diffuse) * 0.85f + ambientLight;
                var ch        = ramp.charAt((int) (intensity * (ramp.length() - 1)));

                //todo: make proper camera matrix transformation
                //Camera Space
                w0.z += cameraDistance;
                w1.z += cameraDistance;
                w2.z += cameraDistance;

                //near plance clipping
                if (w0.z <= 0.1f || w1.z <= 0.1f || w2.z <= 0.1f) continue;

                //perspective projection
                var p0 = project(w0);
                var p1 = project(w1);
                var p2 = project(w2);

                //bounding-box
                int minX = (int) Math.max(0, Math.min(p0.x, Math.min(p1.x, p2.x)));
                int maxX = (int) Math.min(width - 1, Math.max(p0.x, Math.max(p1.x, p2.x)));
                int minY = (int) Math.max(0, Math.min(p0.y, Math.min(p1.y, p2.y)));
                int maxY = (int) Math.min(height - 1, Math.max(p0.y, Math.max(p1.y, p2.y)));

                //rasterisation
                for (int y = minY; y <= maxY; y++) {
                    for (int x = minX; x <= maxX; x++) {
                        //Check containment using edge sub-functions
                        float w0Edge = edgeFunction(p1, p2, x, y);
                        float w1Edge = edgeFunction(p2, p0, x, y);
                        float w2Edge = edgeFunction(p0, p1, x, y);

                        //inside test regardless of CW or CCW triangle generation order
                        if ((w0Edge >= 0 && w1Edge >= 0 && w2Edge >= 0) ||
                                (w0Edge <= 0 && w1Edge <= 0 && w2Edge <= 0)
                        ) {
                            float totalArea = w0Edge + w1Edge + w2Edge;
                            if (Math.abs(totalArea) < 0.0001f) continue;

                            // Barycentric interpolation tracking depth shifts across the face surface
                            float b0 = w0Edge / totalArea;
                            float b1 = w1Edge / totalArea;
                            float b2 = w2Edge / totalArea;

                            float pixelZ = b0 * p0.z + b1 * p1.z + b2 * p2.z;
                            float ooz    = 1.0f / pixelZ;

                            int bufferIdx = x + y * width;
                            if (ooz > zBuffer[bufferIdx]) {
                                zBuffer[bufferIdx]     = ooz;
                                frameBuffer[bufferIdx] = ch;
                            }
                        }
                    }
                }

            }
        }

        //DRAW
        StringBuilder sb = new StringBuilder(width * height);
        for (int i = 0; i < width * height; i++) {
            sb.append("\033[")
              .append((i / width) + 1)
              .append(";")
              .append((i % width) + 1)
              .append("H")
              .append(frameBuffer[i]);
        }
        System.out.print(sb);

        return new char[0][];
    }

    private Vector3 transform(Vector3 vert, TransformComponent trans) {
        return new Vector3(
                vert.x * trans.scale().x,
                vert.y * trans.scale().y,
                vert.z * trans.scale().z
        )
                .rotateX(trans.rotation().x)
                .rotateY(trans.rotation().y)
                .rotateZ(trans.rotation().z)
                .add(trans.position());
    }

    private Vector3 project(Vector3 cameraSpace) {
        var ooz = 1.0f / (cameraSpace.z);
        return new Vector3(
                (width / 2.0f + (45f * 2.2f) * cameraSpace.x * ooz),
                (height / 2.0f - 45f * cameraSpace.y * ooz),
                cameraSpace.z
        );
    }

    private float edgeFunction(Vector3 a, Vector3 b, float px, float py) {
        return (px - a.x) * (b.y - a.y) - (py - a.y) * (b.x - a.x);
    }

    private void clear() {
        Arrays.fill(frameBuffer, ' ');
        Arrays.fill(zBuffer, 0.0f);
    }
}
