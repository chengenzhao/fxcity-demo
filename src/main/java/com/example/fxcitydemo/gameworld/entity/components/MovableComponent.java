package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

public class MovableComponent extends Component {
  protected boolean movingRight = false;
  protected boolean movingLeft = false;

  @Override
  public void onUpdate(double tpf) {
    entity.getComponentOptional(PhysicsComponent.class).ifPresent(physics -> {
      if (movingRight && movingLeft) {
        physics.setVelocityX(0);
        entity.setScaleX(1);
      } else if (movingRight) {
        physics.setVelocityX(500);
        entity.setScaleX(1);
      } else if (movingLeft) {
        physics.setVelocityX(-500);
        entity.setScaleX(-1);
      } else
        physics.setVelocityX(0);
    });
  }

  public void moveRight() {
    movingRight = true;
  }

  public void moveLeft() {
    movingLeft = true;
  }

  public void stopMovingRight() {
    movingRight = false;
  }

  public void stopMovingLeft() {
    movingLeft = false;
  }

  public boolean isMovingRight() {
    return movingRight;
  }

  public boolean isMovingLeft() {
    return movingLeft;
  }
}
