package com.engine.graphics;

import com.engine.World;
import com.engine.physics.CollisionEvent;

import java.util.List;

public interface IRenderer {
    void init(int width, int height);

    void render(World world);
}
