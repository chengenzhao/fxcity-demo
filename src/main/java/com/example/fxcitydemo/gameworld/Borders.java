package com.example.fxcitydemo.gameworld;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class Borders {
  public static Entity of(double width, double height, HitBox... hitBoxes){
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.STATIC);
    physics.setFixtureDef(new FixtureDef().friction(0f));

    double thickness = 500.0;
    var builder = entityBuilder().bbox(new HitBox(new Point2D(-thickness, 0.0), BoundingShape.box(thickness, height)))
      .bbox(new HitBox(new Point2D(width, 0.0), BoundingShape.box(thickness, height)))
      .bbox(new HitBox(new Point2D(0.0, -thickness), BoundingShape.box(width, thickness)))
      .bbox(new HitBox(new Point2D(0.0, height), BoundingShape.box(width, thickness)));

    for(HitBox box : hitBoxes){
      builder.bbox(box);
    }

    return builder.with(physics).build();
  }
}
