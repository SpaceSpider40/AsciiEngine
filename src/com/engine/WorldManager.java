package com.engine;

import com.engine.ecs.components.MeshComponent;
import com.engine.ecs.components.TransformComponent;
import com.engine.math.Triangle;
import com.engine.math.Vector3;

import java.util.List;

public class WorldManager {

    private World currentWorld;

    public World getCurrentWorld() {

        if (currentWorld == null) {
            currentWorld = createTemplateWorld();
        }

        return currentWorld;
    }

    private World createTemplateWorld() {
        var world = new World("Empty World");
        var box   = world.createEntity();
        world.ecsRegistry.set(
                box,
                new TransformComponent(
                        new Vector3(0, 0, 1),
                        Vector3.one(),
                        Vector3.zero()
                )
        );
        float h = 4 / 2.0f;
        // Define 8 basic corners
        Vector3 c0 = new Vector3(-h, -h, -h);
        Vector3 c1 = new Vector3(h, -h, -h);
        Vector3 c2 = new Vector3(h, h, -h);
        Vector3 c3 = new Vector3(-h, h, -h);
        Vector3 c4 = new Vector3(-h, -h, h);
        Vector3 c5 = new Vector3(h, -h, h);
        Vector3 c6 = new Vector3(h, h, h);
        Vector3 c7 = new Vector3(-h, h, h);

        // Construct 12 distinct structural triangles with correct exterior winding
        world.ecsRegistry.set(
                box,
                new MeshComponent(
                        List.of(
                                new Triangle(
                                        new Vector3(1, 0, 0),
                                        new Vector3(10, 0, 0),
                                        new Vector3(1, 10, 0)
                                ),
                                new Triangle(
                                        new Vector3(-1, 0, 0),
                                        new Vector3(-10, 0, 0),
                                        new Vector3(-1, 10, 0)
                                ),
                                new Triangle(
                                        new Vector3(0, 0, -1),
                                        new Vector3(0, 0, -10),
                                        new Vector3(0, 10, -1)
                                ),
                                new Triangle(
                                        new Vector3(0, 0, 1),
                                        new Vector3(0, 0, 10),
                                        new Vector3(0, 10, 1)
                                )
                        )
//                        List.of(
//                                new Triangle(c0, c2, c1), new Triangle(c0, c3, c2), // Front
//                                new Triangle(c5, c7, c4), new Triangle(c5, c6, c7), // Back
//                                new Triangle(c3, c6, c2), new Triangle(c3, c7, c6), // Top
//                                new Triangle(c4, c1, c0), new Triangle(c4, c5, c1), // Bottom
//                                new Triangle(c4, c3, c0), new Triangle(c4, c7, c3), // Left
//                                new Triangle(c1, c6, c5), new Triangle(c1, c2, c6) // Right
//                        )
                )
        );

        return world;
    }
}
