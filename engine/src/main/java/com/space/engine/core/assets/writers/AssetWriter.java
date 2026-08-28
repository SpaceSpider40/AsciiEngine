package com.space.engine.core.assets.writers;

import com.space.engine.core.assets.AssetId;
import com.space.engine.core.assets.AssetManager;

import java.io.IOException;
import java.nio.file.Path;

public interface AssetWriter<T> {
    void write(AssetManager assetManager, AssetId id, Path file, T asset) throws IOException;
}
