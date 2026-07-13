import com.engine.Engine;
import com.engine.ITick;
import com.engine.IUpdate;
import com.engine.Input;
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

                            transform.rotation().y += (float) (0.1f * delta);
//                            transform.rotation().z += (float) (0.1f * delta);
//                            transform.rotation().x -= (float) (0.1f * delta);
                        });
            }
        };

        engine.register((ITick) system);
        engine.register((IUpdate) system);
        engine.run();
    }
}
