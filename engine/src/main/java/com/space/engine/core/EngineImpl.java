package com.space.engine.core;

import com.space.engine.core.actors.components.CameraComponent;
import com.space.engine.core.actors.components.TransformComponent;
import com.space.engine.core.assets.AssetId;
import com.space.engine.core.assets.AssetManager;
import com.space.engine.core.graphics.AsciiRendererV2;
import com.space.engine.core.graphics.Renderer;
import com.space.engine.core.graphics.ui.DebugStatsUIElement;
import com.space.engine.core.physics.IPhysics;
import com.space.engine.core.physics.Physics;
import com.space.engine.core.util.TerminalSizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class EngineImpl implements Engine {

    public static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("wind");

    private static final double TPS = 1000.0 / 60.0;
    private static final double FPS = 1000.0 / 60.0; // 0.08f

    private static final List<EngineState> initializableStates = List.of(
            EngineState.CREATED,
            EngineState.STOPPED,
            EngineState.FAILED);

    private final WorldManager worldManager = new WorldManager();

    private final Thread updateThread = new Thread(this::loop, "main-loop");

    private Config config;

    private Renderer renderer = null;
    private IPhysics physics  = null;

    private AssetManager assetManager;

    private EngineState state = EngineState.CREATED;

    private double deltaTime   = 0;
    private double renderTime  = 0;
    private double physicsTime = 0;

    public EngineState state() {
        return state;
    }

    public void init() {
        if (!initializableStates.contains(state)) {
            return; // already initialized
        }
        state = EngineState.INITIALIZING;

        // todo: make some abstract class "window manager"
        System.out.print(TerminalCodes.showCursor(false));
        System.out.print(TerminalCodes.moveCursor(1, 1));
        System.out.print(TerminalCodes.clear());

        // todo: read config file
        try {
            config = ConfigScanner.scan();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Input.init();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Init redering
        var terminalSize = TerminalSizer.getTerminalSize();
        renderer = new AsciiRendererV2();
        renderer.init(terminalSize.width(), terminalSize.height());
        // Init ui

        // init physics0.00000001f
        physics = new Physics(this, 0.00000986f);// 9.86f

        // register all buildin writers and readers
        assetManager = new AssetManager(Path.of("assets")); // todo: engine should export write function
//        assetManager.registerLoader("mesh", new MeshLoader());

        // todo: if project file exist load last used scene

        state = EngineState.INITIALIZED;
    }

    public <T> T load(AssetId assetId, Class<T> type) {
        return null;
    }

    // todo: this should be read from some project file
    public void setProjectTitle(String title) {
        System.out.print("\033]2;" + title + "\033\\");
    }

    public World currentWorld() {
        return worldManager.getCurrentWorld();
    }

    public int createEntity() {
        return -1;
    }

    public void destroyEntity(int entity) {

    }

    public void run() {
        state = EngineState.RUNNING;

        loop(); // for now no the same thread
        // updateThread.run();
    }

    public void shutdown() {
        state = EngineState.STOPPING;

        cleanup();

        state = EngineState.STOPPED;
    }

    private void loop() {
        final double maxFrameTimeMs = 250.0; // todo: move to cofig

        double accumulator = 0;

        long previousTime = System.nanoTime();

        var size = TerminalSizer.getTerminalSize();
        var ele = new DebugStatsUIElement(
                size.width() - 16,
                0,
                16,
                3,
                () -> deltaTime,
                () -> renderTime,
                () -> physicsTime);

        while (state == EngineState.RUNNING) {
            var frameStartTime = System.nanoTime();

            double frameTimeMs = (frameStartTime - previousTime) / 1_000_000.0;
            previousTime = frameStartTime;

            frameTimeMs = Math.min(frameTimeMs, maxFrameTimeMs);

            deltaTime = frameTimeMs;
            accumulator += frameTimeMs;

            // tick
            while (accumulator >= TPS) {
                tick();
                accumulator -= TPS;
            }

            // update
            update();
            ele.update(deltaTime);

            // render
            if (renderer != null) {
                long renderStartTime = System.nanoTime();
                // submit objects
                worldManager.getCurrentWorld().getActors().forEach(a -> {
                    renderer.submit(a);
                });
                // submit ui
                renderer.submit( // todo: test
                        ele);

                // render
                renderer.draw();
                renderTime = (System.nanoTime() - renderStartTime) / 1_000_000.0;
            }

            double elapsedMs = (System.nanoTime() - frameStartTime) / 1_000_000.0;
            double sleepMs   = FPS - elapsedMs;

            if (sleepMs > 0.0) {
                try {
                    long sleepMillis = (long) sleepMs;
                    int  sleepNanos  = (int) ((sleepMs - sleepMillis) * 1_000_000.0);

                    Thread.sleep(sleepMillis, sleepNanos);
                } catch (InterruptedException e) {
                    shutdown();
                    Thread.currentThread().interrupt();
                }
            }
        }

        cleanup();
    }

    private void update() {
        Input.update();

        if (Input.IsKeyDown(Input.KEY_X))
            shutdown();// todo: temp exit key

        worldManager.getCurrentWorld().getActors().forEach(a -> a.update(deltaTime));
    }

    private void tick() {

        //find active camera and pass to renderer
        worldManager.getCurrentWorld().getActors().forEach(a -> {
            if (a.hasComponent(CameraComponent.class)) {
                var cc = a.getComponent(CameraComponent.class);
                var ct = a.getComponent(TransformComponent.class);
                if (cc.isActive()) {
                    renderer.camera(
                            ct.position.asFloats(),
                            ct.rotation.asFloats(),
                            cc.near,
                            cc.far,
                            cc.fov
                    );
                }
            }
        });

        // todo: check if terminal size has changed. if so restart the renderer
        worldManager.getCurrentWorld().getActors().forEach(a -> a.step(TPS));

        if (physics != null) {
            long startPhysicsTime = System.nanoTime();
//            physics.update(TPS);
            physicsTime = (System.nanoTime() - startPhysicsTime) / 1_000_000.0;
        }
    }

    private void cleanup() {
        Input.clean();

        System.out.print(TerminalCodes.showCursor(true));
        System.out.print(TerminalCodes.moveCursor(1, 1));
        System.out.print(TerminalCodes.clear());
    }

    public static class TerminalCodes {
        public static String showCursor(boolean show) {
            return show ? "\033[?25h" : "\033[?25l";
        }

        public static String moveCursor(int x, int y) {
            return "\033[" + y + ";" + x + "H";
        }

        public static String clear() {
            return "\033[0J";
        }
    }
}
