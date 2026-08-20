import com.engine.*;
import com.engine.components.MaterialComponent;
import com.engine.components.MeshComponent;
import com.engine.components.TransformComponent;
import com.engine.components.physics.BoxColliderComponent;
import com.engine.components.physics.GravityComponent;
import com.engine.components.physics.RigidbodyComponent;
import com.engine.ecs.systems.EcsSystem;
import com.engine.graphics.Color;
import com.engine.math.Plane;
import com.engine.math.Vector3;

public class Main {
    static void main(String[] args) throws Exception {
        var engine = new Engine();
        engine.init(args);


        Thread.sleep(1000);

        var world = engine.worldManager.getCurrentWorld();
        int plane = world.createEntity("plane");
        var tris = new Plane(
                new Vector3(-10, 0, 500),
                new Vector3(10, 0, 500),
                new Vector3(10, 0, 0),
                new Vector3(-10, 0, 0)
        ).triangularize();
        world.getEntityRegistry().set(
                plane,
                new MeshComponent(
                        tris
                )
        );
        world.getEntityRegistry().set(
                plane,
                new TransformComponent(
                        new Vector3(0, -5, 0),
                        Vector3.one(),
                        new Vector3(0, 0, 0)
                )
        );
        world.getEntityRegistry().set(
                plane,
                new BoxColliderComponent(new Vector3(50, 1, 50))
        );

        spawnBox(world, new  Vector3(0, 10, 0), randomColor());
        spawnBox(world, new  Vector3(1, 20, 0), randomColor());
        spawnBox(world, new  Vector3(2, 30, 0), randomColor());
        spawnBox(world, new  Vector3(3, 40, 0), randomColor());
        spawnBox(world, new  Vector3(4, 50, 0), randomColor());
        spawnBox(world, new  Vector3(5, 60, 0), randomColor());
        spawnBox(world, new  Vector3(6, 70, 0), randomColor());
        spawnBox(world, new  Vector3(7, 80, 0), randomColor());
        spawnBox(world, new  Vector3(8, 90, 0), randomColor());
        spawnBox(world, new  Vector3(9, 100, 0), randomColor());

        var system = new EcsSystem() {
            @Override
            public void tick(double delta) {
//                var trans = engine.worldManager.getCurrentWorld().getEntityRegistry().get(box, TransformComponent.class);
            }

            @Override
            public void update(double delta) {
                if (!engine.getPhysics().getCollisions().isEmpty()) Debug.log("COLLISION!");
            }
        };
        engine.worldManager.getCurrentWorld().register((ITick) system);
        engine.worldManager.getCurrentWorld().register((IUpdate) system);

        Debug.log("Box spawned");

        engine.start();
    }

    private static Color randomColor(){
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, Color.YELLOW};

        return colors[(int) (Math.random() * colors.length)];
    }

    private static int spawnBox(World world, Vector3 position, Color color) {
        int box = world.createEntity("box");
        world.getEntityRegistry().set(box, MeshFactory.createBox(Vector3.one()));
        world.getEntityRegistry().set(box, new TransformComponent(
                position,
                Vector3.one(),
                Vector3.zero()
        ));
        world.getEntityRegistry().set(
                box,
                new GravityComponent(true)
        );
        world.getEntityRegistry().set(
                box,
                new RigidbodyComponent(Vector3.zero(), .4f)
        );
        world.getEntityRegistry().set(
                box,
                new BoxColliderComponent(new Vector3(1, 1, 1))
        );
        world.getEntityRegistry().set(
                box,
                new MaterialComponent(color, 1)
        );
        return box;
    }
}
