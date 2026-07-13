//package com.engine.graphics;
//
//import com.engine.World;
//import com.engine.ecs.components.MeshComponent;
//import com.engine.ecs.components.TransformComponent;
//import com.engine.math.Vector3;
//
//import java.util.ArrayList;
//
//public class ConsoleRenderer {
//
//    public void init(Viewport viewport) {
//        System.out.print("\033]2;Cons\033\\");
//        System.out.print("\033[?25l");
//    }
//
//    public void startDrawing() {
//        clearScreen();
//    }
//
//    public void draw(World world) {
//        var entities = world.ecsRegistry.view(TransformComponent.class, MeshComponent.class);
//
//        entities.forEach(entity -> {
//            var transform = world.ecsRegistry.get(entity, TransformComponent.class);
//            var mesh      = world.ecsRegistry.get(entity, MeshComponent.class);
//
//            //1. apply transform to mesh
//            ArrayList<Vector3> vertices = new ArrayList<>();
//            //1.1 position
//            mesh.vertices().forEach(vertex -> {
//                var transformed = new Vector3();
//                transformed.x = vertex.x + transform.position().x;
//                transformed.y = vertex.y + transform.position().y;
//                transformed.z = vertex.z + transform.position().z;
//
//                //1.2 scale
//                transformed.x *= transform.scale().x;
//                transformed.y *= transform.scale().y;
//                transformed.z *= transform.scale().z;
//
//                //1.3 rotation
////                transformed.x = (float) ((Math.cos(Math.toRadians(transform.rotation().x)) * transformed.x) - (Math.sin(Math.toRadians(transform.rotation().x))) * transformed.z);
////                transformed.z = (float) ((Math.sin(Math.toRadians(transform.rotation().z)) * transformed.x) - (Math.cos(Math.toRadians(transform.rotation().z)) * transformed.z));
//
//                vertices.add(transformed);
//            });
//
//            //2. project mesh
//            vertices.forEach(vertex -> {
//                var projected = Projection.project(vertex);
//                System.out.print("\033[" + (int) (projected.y) + ";" + (int) (projected.x) + "H");
//                System.out.print("X");
//            });
//        });
//    }
//
//    public void endDrawing() {
//
//    }
//
//    private void clearScreen() {
//        System.out.print("\033[1;1H");
//        System.out.print("\033[0J");
//    }
//}
