package com.space.engine.core.assets.loaders;

import com.space.engine.core.assets.AssetId;
import com.space.engine.core.assets.AssetManager;
import com.space.engine.core.assets.files.MeshFile;
import com.space.engine.core.assets.types.MeshAsset;
import com.google.gson.Gson;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MeshLoader implements AssetLoader<MeshAsset> {
    private final Gson gson = new Gson();

    @Override
    public MeshAsset load(AssetManager assetManager, AssetId assetId, Path path) throws IOException {
        MeshFile meshFile = gson.fromJson(Files.readString(path), MeshFile.class);

        if (!"mesh".equals(meshFile.type)) {
            throw new IOException("Expected mesh asset, got: " + meshFile.type);
        }

        return new MeshAsset(List.of(meshFile.triangles));
    }

}
