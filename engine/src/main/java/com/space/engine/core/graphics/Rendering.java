package com.space.engine.core.graphics;

public interface Rendering {
    void init(int width, int height);

    void camera(float[] position, float[] rotation, float close, float far);

    // 9position3normal3color
    void draw(float[] buffer);

}
