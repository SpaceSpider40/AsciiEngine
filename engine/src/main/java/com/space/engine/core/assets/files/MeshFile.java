package com.space.engine.core.assets.files;

import com.space.engine.core.math.Triangle;

public class MeshFile extends AbstractFile {
    public Triangle[] triangles;

    public MeshFile() {
        this.type = "mesh";
        this.version = 1;
    }
}
