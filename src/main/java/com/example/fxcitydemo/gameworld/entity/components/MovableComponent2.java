package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.physics.PhysicsComponent;
import com.example.fxcitydemo.gameworld.entity.accessories.Daemon;

@SuppressWarnings("unused")
public class MovableComponent2 extends MovableComponent {

  private boolean movingUp = false;
  private boolean movingDown = false;

  public void moveUp() {
    movingUp = true;
  }

  public void moveDown() {
    movingDown = true;
  }

  public void stopMovingUp() {
    movingUp = false;
  }

  public void stopMovingDown() {
    movingDown = false;
  }

  public boolean isMovingUp() {
    return movingUp;
  }

  public void setMovingUp(boolean movingUp) {
    this.movingUp = movingUp;
  }

  public boolean isMovingDown() {
    return movingDown;
  }

  public void setMovingDown(boolean movingDown) {
    this.movingDown = movingDown;
  }

  @Override
  public void onUpdate(double tpf) {
    var entity = this.entity;
    if (this.entity.hasComponent(Daemon.class)) {
      var daemon = entity.getComponent(Daemon.class);
      if (!daemon.isDaemon())
        entity = daemon.getMainEntity();
    }
    if (entity.hasComponent(PhysicsComponent.class)) {
      var physics = entity.getComponent(PhysicsComponent.class);

      double xSpeed, ySpeed = 300;
      if (physics.getVelocityY() != 0)
        xSpeed = 350;
      else xSpeed = 500;

      if (movingUp && movingDown) {
        physics.setVelocityY(0);
      } else if (movingUp) {
        physics.setVelocityY(-ySpeed);
      } else if (movingDown) {
        physics.setVelocityY(ySpeed);
      } else {
        physics.setVelocityY(0);
      }

      if (movingRight && movingLeft) {
        physics.setVelocityX(0);
        entity.setScaleX(1);
      } else if (movingRight) {
        physics.setVelocityX(xSpeed);
        entity.setScaleX(1);
      } else if (movingLeft) {
        physics.setVelocityX(-xSpeed);
        entity.setScaleX(-1);
      } else
        physics.setVelocityX(0);
    }
  }
}
