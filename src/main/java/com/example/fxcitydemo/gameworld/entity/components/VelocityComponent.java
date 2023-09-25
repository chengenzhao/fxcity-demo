package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.entity.component.Component;

public class VelocityComponent extends Component {
  private double velocityX, velocityY;

  public VelocityComponent(double velocityX, double velocityY) {
    this.velocityX = velocityX;
    this.velocityY = velocityY;
  }

  public double getVelocityX() {
    return velocityX;
  }

  public void setVelocityX(double velocityX) {
    this.velocityX = velocityX;
  }

  public double getVelocityY() {
    return velocityY;
  }

  public void setVelocityY(double velocityY) {
    this.velocityY = velocityY;
  }
}
