package com.space.engine.core.physics;

import com.space.engine.core.Engine;
import com.space.engine.core.EngineImpl;
import com.space.engine.core.ecs.components.TransformComponent;
import com.space.engine.core.ecs.components.physics.BoxColliderComponent;
import com.space.engine.core.ecs.components.physics.GravityComponent;
import com.space.engine.core.ecs.components.physics.RigidbodyComponent;
import com.space.engine.core.ecs.EcsRegister;
import com.space.engine.core.math.Vector3;

import java.util.ArrayList;
import java.util.List;

//todo: off center collision angular velocity
public class Physics implements IPhysics {
    public final float gravity;

    private final Engine      engine;
    private final List<int[]> collisionEvents = new ArrayList<>();

    public Physics(EngineImpl engineImpl, float gravity) {
        this.engine = engineImpl;

        if (gravity > 0) {
            this.gravity = -gravity;
        } else {
            this.gravity = gravity;
        }
    }

    @Override
    public void update(double delta) {
        collisionEvents.clear();

        applyForces(delta);
        applyGravity(delta);
        applyVelocity(delta);
        resolveCollisions();

        clearAccumulators();
    }

    @Override
    public void force(int entity, Vector3 force) {
        var ecs = getEcs();
        if (ecs.has(entity, RigidbodyComponent.class)){
            var rb = ecs.get(entity, RigidbodyComponent.class);
            var invMass = invMass(rb);

            if (invMass > 0){
                rb.forceAccumulator().add(force);
            }
        }
    }

    @Override
    public void impulse(int entity, Vector3 impulse) {
        var ecs = getEcs();
        if (ecs.has(entity, RigidbodyComponent.class)){
            var rb = ecs.get(entity, RigidbodyComponent.class);

            if (rb.mass() > 0){
                rb.velocity().add(impulse.copy().mul(invMass(rb)));
            }
        }
    }

    @Override
    public void torque(int entity, Vector3 torque) {
        var ecs = getEcs();
        if (ecs.has(entity, RigidbodyComponent.class)){
            var rb = ecs.get(entity, RigidbodyComponent.class);
            var invInertia = invInertia(rb);

            if (invInertia > 0){
                rb.torqueAccumulator().add(torque);
            }
        }
    }

    @Override
    public void angularImpulse(int entity, Vector3 impulse) {
        var ecs = getEcs();
        if (ecs.has(entity, RigidbodyComponent.class)){
            var rb = ecs.get(entity, RigidbodyComponent.class);
            var invInertia = invInertia(rb);

            if (invInertia > 0){
                rb.angularVelocity().add(impulse.copy().mul(invInertia));
            }
        }
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

        // face axes A
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

        // face axes B
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

        // edge axes
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Vector3 crossAxis = Vector3.cross(axesA[i], axesB[j]);

                if (crossAxis.lengthSquared() < 0.000001f)
                    continue;

                crossAxis.normalize();

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

    private void applyVelocity(double delta) {
        var ecs = getEcs();

        var entities = ecs.view(
                RigidbodyComponent.class,
                TransformComponent.class
        );

        for (int entity : entities) {
            var rigidbodyComponent = ecs.get(entity, RigidbodyComponent.class);
            var transformComponent = ecs.get(entity, TransformComponent.class);

            transformComponent.position().add(rigidbodyComponent.velocity().copy().mul((float) delta));
        }
    }

    private void applyForces(double delta){
        var ecs = getEcs();
        var entities = ecs.view(RigidbodyComponent.class);

        for (int entity : entities) {
            var rb = ecs.get(entity, RigidbodyComponent.class);

            float invMass = invMass(rb);

            if (invMass == 0) continue;


            Vector3 linearAcceleration = rb.forceAccumulator().copy().mul(invMass);
            rb.velocity().add(linearAcceleration.mul((float) delta));

            float invInertia = invInertia(rb);
            Vector3 angularAcceleration= rb.torqueAccumulator().copy().mul(invInertia);
            rb.angularVelocity().add(angularAcceleration.mul((float) delta));
        }
    }

    private void clearAccumulators(){
        var ecs = getEcs();
        var entities = ecs.view(RigidbodyComponent.class);

        for (int entity : entities){
            var rb = ecs.get(entity, RigidbodyComponent.class);
            rb.forceAccumulator().set(0, 0, 0);
            rb.torqueAccumulator().set(0, 0, 0);
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

        var bodyA = ecs.get(entA, RigidbodyComponent.class);
        var bodyB = ecs.get(entB, RigidbodyComponent.class);

        float invMassA = invMass(bodyA);
        float invMassB = invMass(bodyB);

        if (invMassA + invMassB == 0) return;//both objects are static

        correctPositions(
                tA, tB,
                manifold.normal,
                manifold.penetration,
                invMassA,
                invMassB
        );

        Vector3 velA = bodyA != null ? bodyA.velocity() : Vector3.zero();
        Vector3 velB = bodyB != null ? bodyB.velocity() : Vector3.zero();

        Vector3 relativeVelocity = velB.copy().sub(velA);

        float velAlongNormal = relativeVelocity.dot(manifold.normal);
        if (velAlongNormal > 0) return; //already moving away from each other

        float restA = bodyA != null ? bodyA.bounciness() : 0.02f;
        float restB = bodyB != null ? bodyB.bounciness() : 0.02f;
        float e     = Math.max(restA, restB);

        float j = -(1.0f + e) * velAlongNormal;
        j /= (invMassA + invMassB);

        Vector3 impulse = manifold.normal.copy().mul(j);

        if (bodyA != null){
            bodyA.velocity().sub(impulse.copy().mul(invMassA));
        }

        if (bodyB != null){
            bodyB.velocity().add(impulse.copy().mul(invMassB));
        }

        Vector3 tangent = relativeVelocity.copy().sub(manifold.normal.copy().mul(velAlongNormal));

        if (tangent.lengthSquared() > 0.0001f){
            tangent.normalize();

            float friction = 0.3f;

            float jt = -relativeVelocity.dot(tangent);
            jt/=(invMassA + invMassB);


            Vector3 frictionImpulse = tangent.mul(jt * friction);

            if (bodyA != null) {
                bodyA.velocity().sub(frictionImpulse.copy().mul(invMassA));
            }

            if (bodyB != null) {
                bodyB.velocity().add(frictionImpulse.copy().mul(invMassB));
            }
        }

        //resolve bounciness and sliding
//        if (hasBodyA) {
//            var rigidbody = ecs.get(entA, RigidbodyComponent.class);
//
//            Vector3 oppositeNormal = manifold.normal.copy().mul(-1);
//            resolveVelocityResponse(oppositeNormal, rigidbody);
//        }
//
//        if (hasBodyB) {
//            var rigidbody = ecs.get(entB, RigidbodyComponent.class);
//
//            resolveVelocityResponse(manifold.normal, rigidbody);
//        }
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

            var dir = axis.copy();
            if (dir.dot(centerDelta) < 0) {
                dir.mul(-1.0f);
            }
            manifold.normal = dir;
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
            float invMassA,
            float invMassB
    ) {
        float slop              = 0.001f;
        float correctionPercent = 0.8f;

        float correctionAmount = Math.max(penetration - slop, 0.0f) / (invMassA + invMassB) * correctionPercent;

        if (correctionAmount <= 0) {
            return;
        }

        Vector3 correction = normal.copy().mul(correctionAmount);

        if (invMassA > 0){
            tA.position().sub(correction.copy().mul(invMassA));
        }

        if (invMassB > 0){
            tB.position().add(correction.copy().mul(invMassB));
        }

//        if (hasBodyA && hasBodyB) {
//            tA.position().sub(correction.copy().mul(0.5f));
//            tB.position().add(correction.copy().mul(0.5f));
//        } else if (hasBodyA) {
//            tA.position().sub(correction);
//        } else if (hasBodyB) {
//            tB.position().add(correction);
//        }
    }

    private EcsRegister getEcs() {
        return null;
//        return engine.currentWorld().getEntityRegistry();
    }

    private float invMass(RigidbodyComponent body){
        return (body != null && body.mass() > 0) ? 1f / body.mass() : 0;
    }
    private float invInertia(RigidbodyComponent body){
        return (body != null && body.inertiaTensor() > 0) ? 1f / body.inertiaTensor() : 0;
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
