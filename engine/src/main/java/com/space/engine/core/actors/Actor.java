package com.space.engine.core.actors;

import com.space.engine.core.actors.components.ActorComponent;
import com.space.engine.core.actors.components.MeshComponent;
import com.space.engine.core.actors.components.TransformComponent;
import com.space.engine.core.graphics.Color;
import com.space.engine.core.graphics.RenderObject;
import com.space.engine.core.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Actor implements RenderObject {

    private UUID uid = UUID.randomUUID();

    protected final List<ActorComponent> components = new ArrayList<>();

    //todo: consumes to much memory
    public <T> T getComponent(Class<T> componentClass) {
        return components.stream()
                         .filter(componentClass::isInstance)
                         .map(componentClass::cast)
                         .findFirst() //todo: slow
                         .orElse(null);
    }

    public boolean hasComponent(Class<? extends ActorComponent> componentClass) {
        return components.stream()
                         .anyMatch(componentClass::isInstance);
    }

    @Override
    public Renderer.VisualInstance getVisualInstance() {//todo: this design is wrong

        var tc = getComponent(TransformComponent.class);
        var mc = getComponent(MeshComponent.class);

        if (tc == null || mc == null) return null;

        return new Renderer.VisualInstance(
                tc.position,
                tc.rotation,
                tc.scale,
                mc.getTriangles(),
                new Color[]{Color.WHITE}
        );
    }

    public final void start() {
        components.forEach(ActorComponent::start);

        onStart();
    }

    public final void update(double deltaTime) {
        components.forEach(c -> c.update(deltaTime));

        onUpdate(deltaTime);
    }

    public final void step(double deltaTime) {
        components.forEach(c -> c.step(deltaTime));

        onStep(deltaTime);
    }

    public final void end() {
        components.forEach(ActorComponent::end);

        onEnd();
    }

    protected abstract void onStart();

    protected abstract void onUpdate(double deltaTime);

    protected abstract void onStep(double deltaTime);

    protected abstract void onEnd();
}
