package com.engine.ecs.components;

import com.engine.math.Vector3;

import java.util.Objects;

public record TransformComponent(
        Vector3 position,
        Vector3 scale,
        Vector3 rotation
) {
    public TransformComponent() {
        this(Vector3.zero(), Vector3.one(), Vector3.zero());
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
