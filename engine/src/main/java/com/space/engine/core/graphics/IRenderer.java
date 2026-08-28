package com.space.engine.core.graphics;

import com.space.engine.core.World;

//todo: make new rendering interface
public interface IRenderer {
    void init(int width, int height);

    void render(World world);
}
