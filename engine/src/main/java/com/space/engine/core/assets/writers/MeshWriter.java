package com.space.engine.core.assets.writers;

import com.google.gson.Gson;
import com.space.engine.core.assets.AssetId;
import com.space.engine.core.assets.AssetManager;
import com.space.engine.core.assets.files.MeshFile;
import com.space.engine.core.assets.types.MeshAsset;
import com.space.engine.core.math.Triangle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MeshWriter implements AssetWriter<MeshAsset> {
    private final Gson gson = new Gson();

    @Override
    public void write(AssetManager assetManager, AssetId id, Path file, MeshAsset asset) throws IOException {
        MeshFile meshFile = new MeshFile();

        meshFile.triangles = new Triangle[asset.triangles().size()];

        asset.triangles().toArray(meshFile.triangles);

        var str = gson.toJson(meshFile, MeshFile.class);
        Files.writeString(file, str);
    }
}
