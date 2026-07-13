package com.engine;

import com.engine.graphics.AsciiRenderer;
import com.engine.graphics.IRenderer;

import java.util.ArrayList;
import java.util.Arrays;

public final class Engine {

    public final WorldManager worldManager = new WorldManager();

    private static final float TPS = 1.0f / 60;
    private static final float FPS = 1.0f / 60f; //0.08f

    private final ArrayList<ITick>   tickRegistry   = new ArrayList<>();
    private final ArrayList<IUpdate> updateRegistry = new ArrayList<>();

    private double deltaTime = 0;

    private boolean isInitialized = false;
    private boolean isRunning     = true;

    private Config config;

    private IRenderer renderer = new AsciiRenderer();

    public void init(String[] args) {
        if (isInitialized) {
            return;
        }

        System.out.print("\033]2;Cons\033\\");
        System.out.print("\033[?25l");
        System.out.print("\033[1;1H");
        System.out.print("\033[0J");

        config = new Config(Arrays.stream(args));
        renderer.init(100, 50);

        isInitialized = true;
    }

    public void run() {

        double accumulator = 0;
        while (isRunning) {
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
//            System.out.println(deltaTime);
            // sleep if delta time is less than FPS
            if (deltaTime < FPS) {
                try {
                    Thread.sleep((long) (FPS - deltaTime) * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void register(ITick tick) {
        tickRegistry.add(tick);
    }

    public void register(IUpdate update) {
        updateRegistry.add(update);
    }

    private void update() {
        updateRegistry.forEach(consumer -> consumer.update(deltaTime));
//
//        System.out.print("\033[1;1H");
//        System.out.print("\033[0J");
        char[][] frame = renderer.render(worldManager.getCurrentWorld());
        //print frame
//        for (int y = 0; y < frame.length - 1; y++) {
//            for (int x = 0; x < frame[y].length - 1; x++) {
//                System.out.print("\033[" + (y+1) + ";" + (x+1) + "H" + frame[y][x]);
//            }
//        }
    }

    private void tick() {
        tickRegistry.forEach(consumer -> consumer.tick(TPS));
    }
}
