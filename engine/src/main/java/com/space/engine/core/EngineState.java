package com.space.engine.core;

public enum EngineState {
    CREATED(0),
    INITIALIZING(1),
    INITIALIZED(2),
    RUNNING(3),
    STOPPING(4),
    STOPPED(5),
    FAILED(6);

    private final int value;

    private EngineState(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
