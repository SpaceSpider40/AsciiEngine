package com.engine;

import com.engine.ecs.EcsRegister;

public class Camera {
    public float far  = 3000;
    public float near = 0.1f;
    public float fov  = 60;

    public final int entity;

    public Camera(EcsRegister ecs) {
         entity = ecs.createEntity();

    }
}
