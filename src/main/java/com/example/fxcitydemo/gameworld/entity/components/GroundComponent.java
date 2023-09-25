package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

public final class GroundComponent extends Component {

  private double previousVelocityY = 0;
  private double previousX = -1;

  @Override
  public void onUpdate(double tpf) {
    entity.getComponentOptional(AfterUpdateComponent.class).ifPresent(previous -> {
      previousVelocityY = previous.getPreviousVelocityY();
      previousX = previous.getPreviousX();
    });
  }

  public boolean isStandingOnSth() {
    if(entity.hasComponent(PhysicsComponent.class)){
      var physics = entity.getComponent(PhysicsComponent.class);
      return Math.abs(previousVelocityY) < 0.005 && Math.abs(physics.getVelocityY()) < 0.005;
    }else return false;
  }

  public boolean isBlocked(){
    return Math.abs(entity.getX() - previousX) < 0.005;
  }
}
