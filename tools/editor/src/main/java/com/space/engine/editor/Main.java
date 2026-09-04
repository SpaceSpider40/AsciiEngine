package com.space.engine.editor;

import com.space.engine.core.Engine;
import com.space.engine.core.EngineImpl;
import com.space.engine.core.actors.CameraActor;
import com.space.engine.core.actors.components.CameraComponent;
import com.space.engine.core.actors.components.TransformComponent;
import com.space.engine.core.math.Vector3;
import com.space.engine.editor.actors.CubeActor;

public class Main {
    public static void main(String[] args) {
        Engine engine = new EngineImpl();
        engine.init();

        var camera = new CameraActor(0.01f, 1000f, 60f);
        camera
                .getComponent(CameraComponent.class)
                .setIsActive(true);
        camera
                .getComponent(TransformComponent.class)
                .position.set(0, 0, -100);

        for (int x = -10; x <= 10; x++) {
            for (int y = -10; y <= 10; y++) {
                AddCube(engine, new Vector3(x, y, 10));
            }
        }

        engine.currentWorld().addActor(camera);

        // run the engine for now it is single thread so last instruciton
        engine.run();
    }

    private static void AddCube(Engine engine, Vector3 position) {
        var box = new CubeActor();

        box.getComponent(TransformComponent.class).position.set(position);

        engine.currentWorld().addActor(box);
    }
}
