package com.space.engine.core.components;

import java.util.Objects;

public record CameraComponent(float far, float near, float fov) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CameraComponent that = (CameraComponent) o;
        return Float.compare(far, that.far) == 0 && Float.compare(fov, that.fov) == 0 && Float.compare(near, that.near) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(far, near, fov);
    }

    @Override
    public String toString() {
        return "CameraComponent{" +
                "far=" + far +
                ", near=" + near +
                ", fov=" + fov +
                '}';
    }
}
