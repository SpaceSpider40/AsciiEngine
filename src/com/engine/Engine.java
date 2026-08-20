package com.engine;

import com.engine.graphics.AsciiRenderer;
import com.engine.graphics.IRenderer;
import com.engine.physics.IPhysics;
import com.engine.physics.Physics;
import com.engine.util.TerminalSizer;

import java.io.IOException;
import java.util.Arrays;

public final class Engine extends Thread {

    public static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("wind");

    public final WorldManager worldManager = new WorldManager();

    private static final float TPS = 1.0f / 60f;
    private static final float FPS = 1.0f / 60f; //0.08f

    private double deltaTime = 0;

    private boolean isInitialized = false;

    private Config config;

    private IRenderer renderer = new AsciiRenderer();
    private IPhysics  physics  = null;

    public IPhysics getPhysics() {
        return physics;
    }

    public void init(String[] args) {
        if (isInitialized) {
            return;
        }

        System.out.print("\033[?25l");
        System.out.print("\033[1;1H");
        System.out.print("\033[0J");

        config = new Config(Arrays.stream(args));

        try {
            Input.init();
        } catch (IOException | InterruptedException e) {
            isInitialized = false;
            throw new RuntimeException(e);
        }

        var terminalSize = TerminalSizer.getTerminalSize();
        renderer.init(terminalSize.width(), terminalSize.height());

        //init physics
        physics = new Physics(this, 0.00000001f);//9.86f

        isInitialized = true;
    }

    public void setProjectTitle(String title) {
        System.out.print("\033]2;" + title + "\033\\");
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

    private void update() {
        Input.update();

        if (Input.IsKeyDown(Input.KEY_X)) this.interrupt();//todo: temp exit key

        worldManager.getCurrentWorld().update(deltaTime);

        renderer.render(worldManager.getCurrentWorld());
    }

    private void tick() {
        physics.update(TPS);
        worldManager.getCurrentWorld().tick(TPS);
    }

    private void cleanup() {
        Input.clean();

        System.out.print("\033[?25h");//show cursor
        System.out.print("\033[1;1H");//move
        System.out.print("\033[0J");//clear
    }
}
