import com.engine.*;
import com.engine.ecs.components.TransformComponent;
import com.engine.ecs.systems.EcsSystem;

public class Main {
    static void main(String[] args) throws Exception {
        var engine = new Engine();
        engine.init(args);

        var system = new EcsSystem() {
            @Override
            public void tick(double delta) {

            }

            @Override
            public void update(double delta) {
                engine
                        .worldManager
                        .getCurrentWorld()
                        .ecsRegistry
                        .view(TransformComponent.class)
                        .forEach(entity -> {

                            var transform = engine.worldManager.getCurrentWorld().ecsRegistry.get(entity, TransformComponent.class);
                            if (Input.IsKeyDown(100)) {
                                transform.rotation().y += (float) (0.01f * delta);
                            }
                            if (Input.IsKeyDown(97)) {
                                transform.rotation().y -= (float) (0.01f * delta);
                            }
                            if (Input.IsKeyDown(119)) {
                                transform.rotation().x += (float) (0.01f * delta);
                            }
                            if (Input.IsKeyDown(115)) {
                                transform.rotation().x -= (float) (0.01f * delta);
                            }
                            if (Input.IsKeyDown(114)) {
                                transform.rotation().z += (float) (0.01f * delta);
                            }
                            if (Input.IsKeyDown(107)) {
                                transform.rotation().z -= (float) (0.01f * delta);
                            }

                        });
            }
        };

        engine.register((ITick) system);
        engine.register((IUpdate) system);
        engine.start();
    }
}
