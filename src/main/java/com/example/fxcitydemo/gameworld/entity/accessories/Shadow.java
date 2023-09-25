package com.example.fxcitydemo.gameworld.entity.accessories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.example.fxcitydemo.gameworld.entity.components.ShadowComponent;

public class Shadow extends Component{

  private double width = 140;
  private double height = 14;

  public Shadow(double width, double height) {
    this.width = width;
    this.height = height;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  public static Entity of(double x, double y, Component... components) {
    return of(new SpawnData(x, y), components);
  }

  public static Entity of(double x, double y, double width, double height, Component... components) {
    return of(new SpawnData(x,y).put("width", width).put("height", height), components);
  }
  public static Entity of(SpawnData data, Component... components) {
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);
    physics.setFixtureDef(new FixtureDef().friction(1f));

    if(!data.hasKey("width")) data.put("width", 140);
    if(!data.hasKey("height")) data.put("height", 14);

    var shadow = new Shadow(data.<Number>get("width").doubleValue(), data.<Number>get("height").doubleValue());

    return FXGL.entityBuilder()
      .at(data.getX(), data.getY())
      .collidable()
      .bbox(new HitBox(BoundingShape.box(shadow.getWidth(),shadow.getHeight())))
      .with(new ShadowComponent(shadow.getWidth()/2.0, shadow.getHeight()/2.0, shadow.getWidth()/2.0 + 15, shadow.getHeight()/2.0 + 3))
//      .view(new Rectangle(shadow.getWidth(),shadow.getHeight(), Color.RED))
      .with(physics)
      .with(shadow)
      .with(components)
      .build();
  }
}
