package com.engine;

import com.engine.components.ActiveCameraComponent;
import com.engine.components.CameraComponent;
import com.engine.components.TransformComponent;
import com.engine.math.Vector3;

public class WorldManager {

    private World currentWorld;

    public World getCurrentWorld() {

        if (currentWorld == null) {
            currentWorld = createTemplateWorld();
        }

        return currentWorld;
    }

    @SuppressWarnings("D")
    private World createTemplateWorld() {
        var world    = new World("Empty World");
        var registry = world.getEntityRegistry();

        int camera = world.createEntity("MainCamera");
        registry.set(camera, new TransformComponent(
                new Vector3(0, 10, -10),
                Vector3.one(),
                new Vector3(30, 0, 0)
        ));
        //fov is the other way around more -> zoom in
        registry.set(camera, new CameraComponent(3000f, 0.1f, 45f));
        registry.set(camera, new ActiveCameraComponent());

        //debug camera movement
        world.register((IUpdate) delta -> {
            var cameraTransform = registry.get(camera, TransformComponent.class);
            //position
            if (Input.IsKeyDown(Input.KEY_A)) {
                cameraTransform.position().x = (float) (cameraTransform.position().x + 0.01f * delta);
            }
            if (Input.IsKeyDown(Input.KEY_D)) {
                cameraTransform.position().x = (float) (cameraTransform.position().x - 0.01f * delta);
            }
            if (Input.IsKeyDown(Input.KEY_E)) {
                cameraTransform.position().y = (float) (cameraTransform.position().y - 0.01f * delta);
            }
            if (Input.IsKeyDown(Input.KEY_Q)) {
                cameraTransform.position().y = (float) (cameraTransform.position().y + 0.01f * delta);
            }
            if (Input.IsKeyDown(Input.KEY_W)) {
                cameraTransform.position().z = (float) (cameraTransform.position().z - 0.01f * delta);
            }
            if (Input.IsKeyDown(Input.KEY_S)) {
                cameraTransform.position().z = (float) (cameraTransform.position().z + 0.01f * delta);
            }
            //rotation
            if (Input.IsKeyDown(Input.KEY_I)) {
                cameraTransform.rotation().x = (float) (cameraTransform.rotation().x + 0.01f * delta);
            }
            if (Input.IsKeyDown(Input.KEY_K)) {
                cameraTransform.rotation().x = (float) (cameraTransform.rotation().x - 0.01f * delta);
            }
            if (Input.IsKeyDown(Input.KEY_J)) {
                cameraTransform.rotation().y = (float) (cameraTransform.rotation().y - 0.01f * delta);
            }
            if (Input.IsKeyDown(Input.KEY_L)) {
                cameraTransform.rotation().y = (float) (cameraTransform.rotation().y + 0.01f * delta);
            }
        });

        return world;
    }
}
