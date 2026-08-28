package com.space.engine.core.components;

import com.space.engine.core.graphics.Color;

public record MaterialComponent(
        Color albedo,
        float specular
        //optionally custom ramp
) {
}
