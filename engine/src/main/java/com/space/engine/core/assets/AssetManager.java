package com.space.engine.core.assets;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

//in debug modes all assets are in text format but when packing for release pack everything into binary format.
public class AssetManager {
    private final Path root;

//    private final ConcurrentHashMap<AssetId, AssetDTO> registeredAssetType = new ConcurrentHashMap<>();

    public AssetManager(Path root) {
        this.root = root;
    }

    private Path resolveDevelopmentFile(AssetId id) {
        return root.resolve(id.toString() + ".json");//todo: return .pack from binary-packed assets
    }
}
