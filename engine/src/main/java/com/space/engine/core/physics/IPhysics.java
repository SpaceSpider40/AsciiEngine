package com.space.engine.core.physics;

import com.space.engine.core.math.Vector3;

import java.util.List;

public interface IPhysics {
    void update(double delta);

    List<int[]> getCollisions();



    void force(int entity, Vector3 force);
    void impulse(int entity, Vector3 impulse);
    void torque(int entity, Vector3 torque);
    void angularImpulse(int entity, Vector3 impulse);
}
