package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.example.fxcitydemo.GameApp;

public class MovingAlwaysForwardComponent extends Component {
  private double velocity;
  private final double range;
  private final int freeze;
  private Camp camp = Camp.EMPIRE;

  private Entity mainEntity;

  public void setMainEntity(Entity mainEntity) {
    this.mainEntity = mainEntity;
  }

  public MovingAlwaysForwardComponent(double velocity, double range) {
    this(velocity, range, 1);
  }

  public MovingAlwaysForwardComponent(double velocity, double range, int freeze) {
    this.velocity = velocity;
    this.range = range;
    this.freeze = freeze;
  }

  public MovingAlwaysForwardComponent(Camp camp, double velocity, double range, int freeze) {
    this((camp == Camp.ALLIANCE ? 1 : -1) * velocity, range, freeze);
    this.camp = camp;
  }

  private Status status = Status.RUN;
  private int statusCount = 0;

  @Override
  public void onUpdate(double tpf) {

    if (status == Status.IDLE) return;

    var ent = mainEntity == null ? entity:mainEntity;

    ent.getComponentOptional(PhysicsComponent.class)
      .ifPresentOrElse(physicsComponent -> physicsComponent.setVelocityX(velocity),
        () -> ent.setX(ent.getX() + velocity * tpf));

    var service = FXGL.<GameApp>getAppCast().getFrontlineService();
    var x = service != null ? service.getPlayerX() : 0;

    if (FXGLMath.abs(x - ent.getX()) > range) {
      setStatus(Status.RUN);
    } else {
      setStatus(Status.FIRE);
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
}
