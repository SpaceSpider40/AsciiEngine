package com.space.engine.core.ecs.components;

import com.space.engine.core.math.Vector3;

import java.util.Objects;

public record TransformComponent(
        Vector3 position,
        Vector3 scale,
        Vector3 rotation
) {

    public TransformComponent() {
        this(Vector3.zero(), Vector3.one(), Vector3.zero());
    }

    public Vector3[] getLocalAxes() {
        float pitch = (float) Math.toRadians(rotation.x);
        float yaw   = (float) Math.toRadians(rotation.y);
        float roll  = (float) Math.toRadians(rotation.z);

        float cosP = (float) Math.cos(pitch);
        float cosY = (float) Math.cos(yaw);
        float cosR = (float) Math.cos(roll);

        float sinP = (float) Math.sin(pitch);
        float sinY = (float) Math.sin(yaw);
        float sinR = (float) Math.sin(roll);

        Vector3 right = new Vector3(
                cosY * cosR + sinY * sinP * sinR,
                cosP * sinR,
                -sinY * cosR + cosY * sinP * sinR
        ).normalized();

        Vector3 up = new Vector3(
                -cosY * sinR + sinY * sinP * cosR,
                cosP * cosR,
                sinY * sinR + cosY * sinP * sinR
        ).normalized();

        Vector3 forward = new Vector3(
                sinY * cosP,
                -sinP,
                cosY * cosP
        ).normalized();

        return new Vector3[]{right, up, forward};
    }

    @Override
    public String toString() {
        return "TransformComponent{" +
                "position=" + position +
                ", scale=" + scale +
                ", rotation=" + rotation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransformComponent that = (TransformComponent) o;
        return Objects.equals(rotation, that.rotation) && Objects.equals(scale, that.scale) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, scale, rotation);
    }
}
