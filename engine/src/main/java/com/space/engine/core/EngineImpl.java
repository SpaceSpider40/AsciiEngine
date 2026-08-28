package com.space.engine.core;

import com.space.engine.core.assets.AssetId;
import com.space.engine.core.assets.AssetManager;
import com.space.engine.core.assets.loaders.MeshLoader;
import com.space.engine.core.graphics.AsciiRenderer;
import com.space.engine.core.graphics.IRenderer;
import com.space.engine.core.physics.IPhysics;
import com.space.engine.core.physics.Physics;
import com.space.engine.core.util.TerminalSizer;

import java.util.List;
import java.io.IOException;
import java.nio.file.Path;

public final class EngineImpl implements Engine {

    public static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("wind");

    private static final float TPS = 1.0f / 60f;
    private static final float FPS = 1.0f / 60f; // 0.08f

    private static final List<EngineState> initializableStates = List.of(
            EngineState.CREATED,
            EngineState.STOPPED,
            EngineState.FAILED);

    private final WorldManager worldManager = new WorldManager();

    private final Thread updatetThread = new Thread("update-thread");

    private Config config;

    private IRenderer renderer = null;
    private IPhysics physics = null;

    private AssetManager assetManager;

    private EngineState state = EngineState.CREATED;

    private double deltaTime = 0;

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
        renderer = new AsciiRenderer();
        renderer.init(terminalSize.width(), terminalSize.height());

        // init physics0.00000001f
        physics = new Physics(this, 0.00000986f);// 9.86f

        // register all buildin writers and readers
        assetManager = new AssetManager(Path.of("assets")); // todo: engine should export write function
        assetManager.registerLoader("mesh", new MeshLoader());

        // todo: if project file exist load last used scene

        state = EngineState.INITIALIZED;
    }

    public <T> T load(AssetId assetId, Class<T> type) {
        return assetManager.load(assetId, type);
    }

    // todo: this should be read from some project file
    public void setProjectTitle(String title) {
        System.out.print("\033]2;" + title + "\033\\");
    }

    public World currentWorld() {
        return worldManager.getCurrentWorld();
    }

    public int createEntity() {
        return currentWorld().createEntity();
    }

    public void destroyEntity(int entity) {
        currentWorld().destroyEntity(entity);
    }

    public void run() {
        state = EngineState.RUNNING;

        double accumulator = 0;

        while (state == EngineState.RUNNING) {
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
            // todo: update to something proper
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

    public void shutdown() {
        state = EngineState.STOPPING;

        cleanup();

        state = EngineState.STOPPED;
    }

    private void update() {
        Input.update();

        if (Input.IsKeyDown(Input.KEY_X))
            shutdown();// todo: temp exit key

        worldManager.getCurrentWorld().update(deltaTime);

        if (renderer != null) {
            renderer.render(worldManager.getCurrentWorld());
        }
    }

    private void tick() {

        // todo: check if terminal size has changed. if so restart the renderer

        physics.update(TPS);
        worldManager.getCurrentWorld().tick(TPS);
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
