package com.engine.graphics;

import com.engine.math.Vector2;
import com.engine.math.Vector2Int;
import com.engine.math.Vector3;

public class Projection {

    private static final int FOCAL_LENGTH = 1;

    public static Vector2 project(Vector3 position)
    {
        var z = position.z;
        if (z == 0) z = 1;

        return new Vector2(
                (position.x / z) * FOCAL_LENGTH,
                (position.y / z) * FOCAL_LENGTH
        );
    }
}
