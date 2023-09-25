package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.example.fxcitydemo.GameApp;

public class MovingForwardComponent extends Component {
  private double velocity;
  private final double range;
  private final int freeze;
  private Camp camp = Camp.EMPIRE;

  private Entity mainEntity;

  public MovingForwardComponent(double velocity, double range, Entity mainEntity) {
    this(velocity, range, 1, mainEntity);
  }

  public MovingForwardComponent(double velocity, double range, int freeze, Entity mainEntity) {
    this.velocity = velocity;
    this.range = range;
    this.freeze = freeze;
    this.mainEntity = mainEntity;
  }

  public MovingForwardComponent(double velocity, double range) {
    this(velocity, range, 1);
  }

  public MovingForwardComponent(double velocity, double range, int freeze) {
    this.velocity = velocity;
    this.range = range;
    this.freeze = freeze;
  }

  public MovingForwardComponent(Camp camp, double velocity, double range, int freeze) {
    this((camp == Camp.ALLIANCE ? 1 : -1) * velocity, range, freeze);
    this.camp = camp;
  }

  private Status status = Status.RUN;
  private int statusCount = 0;

  @Override
  public void onUpdate(double tpf) {

    if (status == Status.IDLE) return;

    if (status == Status.FIRE && statusCount % freeze != 0) {
      statusCount++;
      return;
    }

    var ent = mainEntity != null ? mainEntity : entity;

    var service = FXGL.<GameApp>getAppCast().getFrontlineService();
    if (service != null) {
      var x = camp == Camp.ALLIANCE ? service.getEnemyX() : service.getPlayerX();

      if (FXGLMath.abs(x - ent.getX()) > range) {
        setStatus(Status.RUN);
        ent.getComponentOptional(PhysicsComponent.class)
          .ifPresentOrElse(comp -> comp.setVelocityX(velocity), () -> ent.setX(ent.getX() + velocity * tpf));
      } else {
        ent.getComponentOptional(PhysicsComponent.class)
          .ifPresent(comp -> {
            comp.setVelocityX(0);
            comp.setVelocityY(0);
          });
        setStatus(Status.FIRE);
      }
    }
  }

  private void setStatus(Status status) {
    if (this.status == status) {
      statusCount++;
    } else {
      this.status = status;
      statusCount = 0;
    }
  }

  public Status getStatus() {
    return status;
  }

  public int getStatusCount() {
    return statusCount;
  }

  public void stop() {
    setStatus(Status.IDLE);
  }

  public void ruin() {
    setStatus(Status.RUIN);
  }

  public void setMainEntity(Entity mainEntity) {
    this.mainEntity = mainEntity;
  }
}
