package com.space.engine.core.assets;

import com.space.engine.core.assets.loaders.AssetLoader;
import com.space.engine.core.assets.writers.AssetWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

//in debug modes all assets are in text format but when packing for release pack everything into binary format.
public class AssetManager {
    private final Path root;

    private final ConcurrentHashMap<String, AssetLoader<?>>   assetLoaders = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, AssetWriter<?>> assetWriters = new ConcurrentHashMap<>();

    public AssetManager(Path root) {
        this.root = root;
    }

    public <T> void registerLoader(String extension, AssetLoader<T> loader) {
        assetLoaders.put(extension, loader);
    }

    public <T> void registerWriter(Class<T> type, AssetWriter<T> writer) {
        assetWriters.put(type, writer);
    }

    public <T> T load(AssetId id, Class<T> type) {
        String         extension = id.extension();
        AssetLoader<?> loader    = assetLoaders.get(extension);

        if (loader == null) {
            throw new RuntimeException("No loader registered for extension: " + extension);
        }

        Path file = resolveDevelopmentFile(id);

        try {
            Object asset = loader.load(this, id, file);

            return type.cast(asset);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load asset: " + id.path(), e);
        }
    }

    public <T> void write(AssetId id, T asset, Class<T> type){
        AssetWriter<T> writer = (AssetWriter<T>) assetWriters.get(type);

        if (writer == null){
            throw new RuntimeException("No writer registered of type: " + type);
        }

        Path file = resolveDevelopmentFile(id);

        try{
            writer.write(
                    this,
                    id,
                    file,
                    asset
            );

        }catch(IOException e){
            throw new RuntimeException("Failed to write asset: " + id.path(), e);
        }
    }

    private Path resolveDevelopmentFile(AssetId id) {
        return root.resolve(id.toString() + ".json");
    }
}
