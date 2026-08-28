package com.space.engine.core.assets.loaders;

import com.space.engine.core.assets.AssetId;
import com.space.engine.core.assets.AssetManager;

import java.io.IOException;
import java.nio.file.Path;

public interface AssetLoader<T> {
    T load(AssetManager assetManager, AssetId id, Path file) throws IOException;
}
