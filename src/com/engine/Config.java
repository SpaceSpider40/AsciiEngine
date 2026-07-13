package com.engine;

import java.util.stream.Stream;

public class Config {

    public final boolean isDebug;
    public final String worldsPath = "assets/worlds/";

    Config(Stream<String> args) {
        //parse args
        isDebug = args.anyMatch(arg -> arg.equals("--debug"));

        //todo: parse config file
    }

    record ConfigSaveData() {

    }
}
