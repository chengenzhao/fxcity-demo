package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

public class SimpleMovingForwardComponent  extends Component {
  private double velocity;

  public SimpleMovingForwardComponent(double velocity) {
    this.velocity = velocity;
  }

  @Override
  public void onUpdate(double tpf) {
    entity.getComponentOptional(PhysicsComponent.class)
      .ifPresentOrElse(physicsComponent -> physicsComponent.setVelocityX(- velocity),
        ()-> entity.setX(entity.getX() - velocity*tpf));
  }
}
