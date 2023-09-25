package com.example.fxcitydemo.gameworld.entity.characters;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.example.fxcitydemo.gameworld.entity.components.MovableComponent2;
import com.example.fxcitydemo.gameworld.entity.components.ShadowComponent;

public class ClancyComponent2 extends Component implements ClancyCommands{

  public void moveRight() {
    entity.getComponentOptional(MovableComponent2.class).ifPresent(MovableComponent2::moveRight);
  }

  public void moveLeft() {
    entity.getComponentOptional(MovableComponent2.class).ifPresent(MovableComponent2::moveLeft);
  }

  public void stopMovingRight() {
    entity.getComponentOptional(MovableComponent2.class).ifPresent(MovableComponent2::stopMovingRight);
  }

  public void stopMovingLeft() {
    entity.getComponentOptional(MovableComponent2.class).ifPresent(MovableComponent2::stopMovingLeft);
  }

  boolean jumping = false;
  boolean firing = false;
  boolean upward = false;

  public void jump() {
    if(!jumping)
      jumping = true;
  }

  public void jumped(){
    jumping = false;
  }

  public boolean isJumping(){
    return jumping;
  }

  public void fire() {
    firing = true;
  }

  public void stopFire() {
    firing = false;
  }

  public boolean isFiring() {
    return firing;
  }

  public void upward() {
    entity.getComponentOptional(MovableComponent2.class).ifPresent(MovableComponent2::moveUp);
    upward = true;
  }

  public void stopUpward() {
    entity.getComponentOptional(MovableComponent2.class).ifPresent(MovableComponent2::stopMovingUp);
    upward = false;
  }

  public boolean isUpward() {
    return upward;
  }

  @Override
  public void downward() {
    entity.getComponentOptional(MovableComponent2.class).ifPresent(MovableComponent2::moveDown);
  }

  @Override
  public void stopDownward() {
    entity.getComponentOptional(MovableComponent2.class).ifPresent(MovableComponent2::stopMovingDown);
  }

  public static Entity of(double x, double y, Component... components){
    return of(new SpawnData(x, y),components);
  }

  public static Entity of(SpawnData data, Component... components) {
    PhysicsComponent physics = new PhysicsComponent();

    physics.setFixtureDef(new FixtureDef().friction(0).density(0.01f));
    BodyDef bd = new BodyDef();
    bd.setFixedRotation(true);
    bd.setType(BodyType.DYNAMIC);
    physics.setBodyDef(bd);

    return FXGL.entityBuilder()
      .at(data.getX(), data.getY())
      .collidable()
      .bbox(new HitBox(BoundingShape.circle(20)))
      .with(new ShadowComponent(20, 20, 50, 10))
//      .view(new Circle(20,20,20, Color.RED))
      .with(physics)
      .with(new MovableComponent2())
      .with(new ClancyComponent2())
      .with(components)
      .build();
  }

}
