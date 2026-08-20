package com.engine.physics;

import com.engine.Engine;
import com.engine.components.TransformComponent;
import com.engine.components.physics.BoxColliderComponent;
import com.engine.components.physics.GravityComponent;
import com.engine.components.physics.RigidbodyComponent;
import com.engine.ecs.EcsRegister;
import com.engine.math.Vector3;

import java.util.ArrayList;
import java.util.List;

//todo: mass
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

    @Override
    public void update(double delta) {
        collisionEvents.clear();

        applyGravity(delta);
        applyVelocity();
        resolveCollisions();
    }

    @Override
    public void applyForce() {

    }

    @Override
    public void applyImpulse() {

    }

    @Override
    public void applyTorque() {

    }

    private void resolveCollisions() {
        var ecs = getEcs();

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

                CollisionManifold manifold = checkOBBCollision(tA, cA, tB, cB);
                if (manifold.colliding) {
                    collisionEvents.add(new int[]{entI, entJ});

                    resolveCollisionResponse(entI, entJ, tA, tB, manifold);
                }
            }
        }
    }

    private CollisionManifold checkOBBCollision(
            TransformComponent tA, BoxColliderComponent cA,
            TransformComponent tB, BoxColliderComponent cB
    ) {

        Vector3[] axesA = tA.getLocalAxes();
        Vector3[] axesB = tB.getLocalAxes();

        Vector3 centerDelta = tB.position().copy().sub(tA.position());

        CollisionManifoldBuilder manifold = new CollisionManifoldBuilder();

        for (int i = 0; i < 3; i++) {
            if (!testOBBAxis(
                    axesA[i],
                    centerDelta,
                    axesA,
                    cA,
                    axesB,
                    cB,
                    manifold
            )) {
                return CollisionManifold.none();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (!testOBBAxis(
                    axesB[i],
                    centerDelta,
                    axesA,
                    cA,
                    axesB,
                    cB,
                    manifold
            )) {
                return CollisionManifold.none();
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Vector3 crossAxis = Vector3.cross(axesA[i], axesB[j]);

                if (crossAxis.lengthSquared() < 0.000001f)
                    continue;

                crossAxis = crossAxis.normalized();

                if (!testOBBAxis(
                        crossAxis,
                        centerDelta,
                        axesA,
                        cA,
                        axesB,
                        cB,
                        manifold
                )) {
                    return CollisionManifold.none();
                }
            }
        }

        Vector3 normal = manifold.normal;

        if (normal.dot(centerDelta) < 0) {
            normal.mul(-1);
        }

        return new CollisionManifold(true, normal, manifold.penetration);
    }

    public List<int[]> getCollisions() {
        return collisionEvents;
    }

    private void applyGravity(double delta) {
        var ecs = getEcs();

        var entities = ecs.view(
                RigidbodyComponent.class,
                GravityComponent.class
        );

        for (int entity : entities) {
            var gravityComponent = ecs.get(entity, GravityComponent.class);

            if (gravityComponent == null || !gravityComponent.hasGravity()) continue;

            var rigidbodyComponent = ecs.get(entity, RigidbodyComponent.class);

            rigidbodyComponent.velocity().y += (float) (gravity * delta);
        }
    }

    private void applyVelocity() {
        var ecs = getEcs();

        var entities = ecs.view(
                RigidbodyComponent.class,
                TransformComponent.class
        );

        for (int entity : entities) {
            var rigidbodyComponent = ecs.get(entity, RigidbodyComponent.class);
            var transformComponent = ecs.get(entity, TransformComponent.class);

            transformComponent.position().add(rigidbodyComponent.velocity());
        }
    }

    private void resolveCollisionResponse(
            int entA,
            int entB,
            TransformComponent tA,
            TransformComponent tB,
            CollisionManifold manifold
    ) {
        var ecs = getEcs();

        boolean hasBodyA = ecs.has(entA, RigidbodyComponent.class);
        boolean hasBodyB = ecs.has(entB, RigidbodyComponent.class);

        correctPositions(
                tA, tB,
                manifold.normal,
                manifold.penetration,
                hasBodyA,
                hasBodyB
        );

        //resolve bounciness and sliding
        if (hasBodyA) {
            var rigidbody = ecs.get(entA, RigidbodyComponent.class);

            Vector3 oppositeNormal = manifold.normal.copy().mul(-1);
            resolveVelocityResponse(oppositeNormal, rigidbody);
        }

        if (hasBodyB) {
            var rigidbody = ecs.get(entB, RigidbodyComponent.class);

            resolveVelocityResponse(manifold.normal, rigidbody);
        }
    }

    private void resolveVelocityResponse(Vector3 normal, RigidbodyComponent rigidbody) {
        float velocityAlongNormal = rigidbody.velocity().dot(normal);

        if (velocityAlongNormal > 0) {
            return;
        }

        float bounciness = rigidbody.bounciness();

//        if (Math.abs(velocityAlongNormal) < 0.05f){
//            bounciness = 0.0f;
//        }

        Vector3 normalVelocity  = normal.copy().mul(velocityAlongNormal);
        Vector3 tangentVelocity = rigidbody.velocity().copy().sub(normalVelocity);

        Vector3 bouncedVelocity = normal.copy().mul(-velocityAlongNormal * bounciness);

        rigidbody.velocity().set(tangentVelocity.add(bouncedVelocity));
    }

    private boolean testOBBAxis(
            Vector3 axis,
            Vector3 centerDelta,
            Vector3[] axesA,
            BoxColliderComponent cA,
            Vector3[] axesB,
            BoxColliderComponent cB,
            CollisionManifoldBuilder manifold
    ) {
        float radiusA = projectOBBRadius(axis, axesA, cA);
        float radiusB = projectOBBRadius(axis, axesB, cB);

        float distance = Math.abs(centerDelta.dot(axis));
        float overlap  = radiusA + radiusB - distance;

        if (overlap <= 0) {
            return false;
        }

        if (overlap < manifold.penetration) {
            manifold.penetration = overlap;
            manifold.normal      = axis.copy();
        }

        return true;
    }

    private float projectOBBRadius(
            Vector3 axis,
            Vector3[] boxAxes,
            BoxColliderComponent collider
    ) {
        //todo: mul halfextents by scale
        var he = collider.halfExtents();

        return he.x * Math.abs(axis.dot(boxAxes[0])) + he.y * Math.abs(axis.dot(boxAxes[1])) + he.z * Math.abs(axis.dot(boxAxes[2]));
    }

    private void correctPositions(
            TransformComponent tA,
            TransformComponent tB,
            Vector3 normal,
            float penetration,
            boolean hasBodyA,
            boolean hasBodyB
    ) {
        float slop              = 0.001f;
        float correctionPercent = 0.8f;

        float correctionAmount = Math.max(penetration - slop, 0.0f) * correctionPercent;

        if (correctionAmount <= 0) {
            return;
        }

        Vector3 correction = normal.copy().mul(correctionAmount);

        if (hasBodyA && hasBodyB) {
            tA.position().sub(correction.copy().mul(0.5f));
            tB.position().add(correction.copy().mul(0.5f));
        } else if (hasBodyA) {
            tA.position().sub(correction);
        } else if (hasBodyB) {
            tB.position().add(correction);
        }
    }

    private EcsRegister getEcs() {
        return engine.worldManager.getCurrentWorld().getEntityRegistry();
    }

    private static class CollisionManifoldBuilder {
        private Vector3 normal      = Vector3.zero();
        private float   penetration = Float.MAX_VALUE;
    }

    private record CollisionManifold(
            boolean colliding,
            Vector3 normal,
            float penetration
    ) {
        public static CollisionManifold none() {
            return new CollisionManifold(false, null, 0);
        }
    }
}
