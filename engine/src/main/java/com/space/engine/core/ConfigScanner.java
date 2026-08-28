package com.space.engine.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

public final class ConfigScanner {
    private static final Gson gson = new Gson();

    private static final Path configPath = Path.of("engine.json");

    private ConfigScanner() {
    }

    public static Config scan() throws IOException {
        return gson.fromJson(Files.readString(configPath), Config.class);
    }
}
