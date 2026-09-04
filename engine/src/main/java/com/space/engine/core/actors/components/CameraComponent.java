package com.space.engine.core.actors.components;

public class CameraComponent extends ActorComponent {
    public final float near;
    public final float far;
    public final float fov;

    protected boolean isActive = false;

    public CameraComponent(float near, float far, float fov) {
        this.near = near;
        this.far = far;
        this.fov = fov;
    }

    public boolean isActive() {
        return isActive;
    }

    public CameraComponent setIsActive(boolean isActive){
        this.isActive = isActive;
        return this;
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onUpdate(double deltaTime) {

    }

    @Override
    protected void onStep(double deltaTime) {

    }

    @Override
    protected void onEnd() {

    }
}
