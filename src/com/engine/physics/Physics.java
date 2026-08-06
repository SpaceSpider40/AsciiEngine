package com.engine.physics;

import com.engine.Engine;
import com.engine.components.TransformComponent;
import com.engine.components.physics.BoxColliderComponent;
import com.engine.components.physics.GravityComponent;
import com.engine.components.physics.VelocityComponent;
import com.engine.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Physics implements IPhysics {
    public final float gravity;

    private final Engine      engine;
    private final List<int[]> collisionEvents = new ArrayList<>();

    public Physics(Engine engine, float gravity) {
        this.engine = engine;

        if (gravity > 0) {
            this.gravity = -gravity;
        } else {
            this.gravity = gravity;
        }
    }

    public void update(double delta) {
        collisionEvents.clear();

        applyGravity(delta);
        resolveCollisions();

        applyVelocity();
    }

    private void resolveCollisions() {
        var ecs = engine.worldManager.getCurrentWorld().getEntityRegistry();

        //todo: this is narrow pass, implement Bounding Volume Hierarchy before this.
        var entities = ecs.view(
                TransformComponent.class,
                BoxColliderComponent.class
        ).toArray();

        for (int i = 0; i < entities.length; i++) {
            int entI = (int) entities[i];
            var tA   = ecs.get(entI, TransformComponent.class);
            var cA   = ecs.get(entI, BoxColliderComponent.class);

            for (int j = i + 1; j < entities.length; j++) {
                int entJ = (int) entities[j];
                var tB   = ecs.get(entJ, TransformComponent.class);
                var cB   = ecs.get(entJ, BoxColliderComponent.class);

                float distSq  = tA.position().copy().sub(tB.position()).lengthSquared();
                float maxDist = cA.boundingRadius() + cB.boundingRadius();
                if (distSq > maxDist * maxDist) {
                    continue; //bounding spheres don't touch skip
                }

                if (checkOBBCollision(tA, cA, tB, cB)) {
                    collisionEvents.add(new int[]{entI, entJ});

                    if (ecs.has(entI, VelocityComponent.class)){
                        var vel = ecs.get(entI, VelocityComponent.class).velocity();
                        vel.x = 0;
                        vel.y = 0;
                        vel.z = 0;
                    }
                    if (ecs.has(entJ, VelocityComponent.class)){
                        var vel = ecs.get(entJ, VelocityComponent.class).velocity();
                        vel.x = 0;
                        vel.y = 0;
                        vel.z = 0;
                    }
                    //todo: apply velocity patch
                }
            }
        }
    }

    private boolean checkOBBCollision(
            TransformComponent tA, BoxColliderComponent cA,
            TransformComponent tB, BoxColliderComponent cB
    ) {

        Vector3[] axesA = tA.getLocalAxes();
        Vector3[] axesB = tB.getLocalAxes();

        Vector3 d = tB.position().copy().sub(tA.position());

        float t0 = d.dot(axesA[0]);
        float t1 = d.dot(axesA[1]);
        float t2 = d.dot(axesB[2]);

        float[][] r    = new float[3][3];
        float[][] absR = new float[3][3];
        float     eps  = 1e-6f;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                r[i][j]    = axesA[i].dot(axesB[j]);
                absR[i][j] = Math.abs(r[i][j]) + eps;
            }
        }

        // A local axes
        if (Math.abs(t0) > cA.halfExtents().x + (cB.halfExtents().x * absR[0][0] + cB.halfExtents().y * absR[0][1] + cB.halfExtents().z * absR[0][2]))
            return false;
        if (Math.abs(t1) > cA.halfExtents().y + (cB.halfExtents().x * absR[1][0] + cB.halfExtents().y * absR[1][1] + cB.halfExtents().z * absR[1][2]))
            return false;
        if (Math.abs(t2) > cA.halfExtents().z + (cB.halfExtents().x * absR[2][0] + cB.halfExtents().y * absR[2][1] + cB.halfExtents().z * absR[2][2]))
            return false;

        // B local axes
        if (Math.abs(t0 * r[0][0] + t1 * r[1][0] + t2 * r[2][0]) > (cA.halfExtents().x * absR[0][0] + cA.halfExtents().y * absR[1][0] + cA.halfExtents().z * absR[2][0]) + cB.halfExtents().x)
            return false;
        if (Math.abs(t0 * r[0][1] + t1 * r[1][1] + t2 * r[2][1]) > (cA.halfExtents().x * absR[0][1] + cA.halfExtents().y * absR[1][1] + cA.halfExtents().z * absR[2][1]) + cB.halfExtents().y)
            return false;
        if (Math.abs(t0 * r[0][2] + t1 * r[1][2] + t2 * r[2][2]) > (cA.halfExtents().x * absR[0][2] + cA.halfExtents().y * absR[1][2] + cA.halfExtents().z * absR[2][2]) + cB.halfExtents().z)
            return false;

        //cross-product axes
        if (Math.abs(t2 * r[1][0] - t1 * r[2][0]) > (cA.halfExtents().y * absR[2][0] + cA.halfExtents().z * absR[1][0]) + (cB.halfExtents().y * absR[0][2] + cB.halfExtents().z * absR[0][1]))
            return false;
        if (Math.abs(t2 * r[1][1] - t1 * r[2][1]) > (cA.halfExtents().y * absR[2][1] + cA.halfExtents().z * absR[1][1]) + (cB.halfExtents().x * absR[0][2] + cB.halfExtents().z * absR[0][0]))
            return false;
        if (Math.abs(t2 * r[1][2] - t1 * r[2][2]) > (cA.halfExtents().y * absR[2][2] + cA.halfExtents().z * absR[1][2]) + (cB.halfExtents().x * absR[0][1] + cB.halfExtents().y * absR[0][0]))
            return false;

        if (Math.abs(t0 * r[2][0] - t2 * r[0][0]) > (cA.halfExtents().x * absR[2][0] + cA.halfExtents().z * absR[0][0]) + (cB.halfExtents().y * absR[1][2] + cB.halfExtents().z * absR[1][1]))
            return false;
        if (Math.abs(t0 * r[2][1] - t2 * r[0][1]) > (cA.halfExtents().x * absR[2][1] + cA.halfExtents().z * absR[0][1]) + (cB.halfExtents().x * absR[1][2] + cB.halfExtents().z * absR[1][0]))
            return false;
        if (Math.abs(t0 * r[2][2] - t2 * r[0][2]) > (cA.halfExtents().x * absR[2][2] + cA.halfExtents().z * absR[0][2]) + (cB.halfExtents().x * absR[1][1] + cB.halfExtents().y * absR[1][0]))
            return false;

        if (Math.abs(t1 * r[0][0] - t0 * r[1][0]) > (cA.halfExtents().x * absR[1][0] + cA.halfExtents().y * absR[0][0]) + (cB.halfExtents().y * absR[2][2] + cB.halfExtents().z * absR[2][1]))
            return false;
        if (Math.abs(t1 * r[0][1] - t0 * r[1][1]) > (cA.halfExtents().x * absR[1][1] + cA.halfExtents().y * absR[0][1]) + (cB.halfExtents().x * absR[2][2] + cB.halfExtents().z * absR[2][0]))
            return false;
        if (Math.abs(t1 * r[0][2] - t0 * r[1][2]) > (cA.halfExtents().x * absR[1][2] + cA.halfExtents().y * absR[0][2]) + (cB.halfExtents().x * absR[2][1] + cB.halfExtents().y * absR[2][0]))
            return false;

        var cross = Vector3.cross(tB.position(), tA.position());

        return true;
    }

    public List<int[]> getCollisions() {
        return collisionEvents;
    }

    private void applyGravity(double delta) {
        var ecs = engine.worldManager.getCurrentWorld().getEntityRegistry();

        var entities = ecs.view(
                VelocityComponent.class,
                GravityComponent.class
        );

        for (int entity : entities) {
            var gravityComponent = ecs.get(entity, GravityComponent.class);

            if (gravityComponent == null || !gravityComponent.hasGravity()) continue;

            var velocityComponent = ecs.get(entity, VelocityComponent.class);

            velocityComponent.velocity().y += (float) (gravity * delta);
        }
    }

    private void applyVelocity() {
        var ecs = engine.worldManager.getCurrentWorld().getEntityRegistry();

        var entities = ecs.view(
                VelocityComponent.class,
                TransformComponent.class
        );

        for (int entity : entities) {
            var velocityComponent  = ecs.get(entity, VelocityComponent.class);
            var transformComponent = ecs.get(entity, TransformComponent.class);

            transformComponent.position().add(velocityComponent.velocity());
        }
    }
}
