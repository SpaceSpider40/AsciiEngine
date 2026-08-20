package com.engine.components;

import com.engine.graphics.Color;

public record MaterialComponent(
        Color albedo,
        float specular
        //optionally custom ramp
) {
}
