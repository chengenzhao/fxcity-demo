package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

public class AfterUpdateComponent extends Component {

  private double previousVelocityX = 0;
  private double previousVelocityY = 0;
  private double previousX = -1;
  private double previousY = -1;

  @Override
  public void onUpdate(double tpf) {
    entity.getComponentOptional(PhysicsComponent.class).ifPresent(physics -> {
      previousVelocityX = physics.getVelocityX();
      previousVelocityY = physics.getVelocityY();
      previousX = entity.getX();
      previousY = entity.getY();
    });
  }

  public double getPreviousVelocityX() {
    return previousVelocityX;
  }

  public double getPreviousVelocityY() {
    return previousVelocityY;
  }

  public double getPreviousX() {
    return previousX;
  }

  public double getPreviousY() {
    return previousY;
  }
}
