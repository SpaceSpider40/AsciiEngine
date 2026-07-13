package com.engine.graphics;

import com.engine.World;

public interface IRenderer {
    void init(int width, int height);

    char[][] render(World world);
}
