package com.engine.physics;

import java.util.List;

public interface IPhysics {
    void update(double delta);

    List<int[]> getCollisions();



    void applyForce();
    void applyImpulse();
    void applyTorque();
}
