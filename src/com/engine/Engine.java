package com.engine;

import com.engine.graphics.AsciiRenderer;
import com.engine.graphics.IRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public final class Engine extends Thread {

    public static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("wind");

    public final WorldManager worldManager = new WorldManager();

    private static final float TPS = 1.0f / 60;
    private static final float FPS = 1.0f / 60f; //0.08f

    private final ArrayList<ITick>   tickRegistry   = new ArrayList<>();
    private final ArrayList<IUpdate> updateRegistry = new ArrayList<>();

    private double deltaTime = 0;

    private boolean isInitialized = false;

    private Config config;

    private IRenderer renderer = new AsciiRenderer();

    public void init(String[] args) {
        if (isInitialized) {
            return;
        }

        System.out.print("\033]2;Cons\033\\");//todo: change to title
        System.out.print("\033[?25l");
        System.out.print("\033[1;1H");
        System.out.print("\033[0J");

        config = new Config(Arrays.stream(args));
        renderer.init(100, 50);

        try {
            Input.init();
        } catch (IOException | InterruptedException e) {
            isInitialized = false;
            throw new RuntimeException(e);
        }

        isInitialized = true;
    }

    public void run() {

        double accumulator = 0;
        while (!isInterrupted()) {
            var start = System.nanoTime();

            // tick
            accumulator += deltaTime;
            while (accumulator >= TPS) {
                accumulator -= TPS;
                tick();
            }

            // render
            update();

            // calculate delta time
            var end = System.nanoTime();
            deltaTime = (end - start) / 1_000_000.0;

            // sleep if delta time is less than FPS
            if (deltaTime < FPS) {
                try {
                    Thread.sleep((long) (FPS - deltaTime) * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        cleanup();
    }

    public void register(ITick tick) {
        tickRegistry.add(tick);
    }

    public void register(IUpdate update) {
        updateRegistry.add(update);
    }

    private void update() {
        Input.update();

        updateRegistry.forEach(consumer -> consumer.update(deltaTime));

        renderer.render(worldManager.getCurrentWorld());
    }

    private void tick() {
        tickRegistry.forEach(consumer -> consumer.tick(TPS));
    }

    private void cleanup() {
        Input.clean();
    }
}
