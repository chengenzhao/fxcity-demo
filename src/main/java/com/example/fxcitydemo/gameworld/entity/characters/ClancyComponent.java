package com.example.fxcitydemo.gameworld.entity.characters;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.IDComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.example.fxcitydemo.gameworld.PropertyKey;
import com.example.fxcitydemo.gameworld.Type;
import com.example.fxcitydemo.gameworld.entity.components.*;
import com.whitewoodcity.fxgl.app.ImageData;
import com.whitewoodcity.fxgl.dsl.FXGL;
import com.whitewoodcity.fxgl.texture.AnimatedTexture;
import com.whitewoodcity.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.List;

public class ClancyComponent extends Component implements ClancyCommands {

  private static final ImageData
    upperImage = new ImageData("units/gi/upper.png", 700, 175),
    runImage = new ImageData("units/gi/run.png", 1440, 110),
    jumpImage = new ImageData("units/gi/jump.png", 1200, 110),
    flareImage = new ImageData("units/gi/fire.png", 2250, 125);

  public static List<ImageData> getImageData() {
    return List.of(upperImage, runImage, jumpImage, flareImage);
  }

  private boolean upward = false;
  private boolean firing = false;

  private final AnimatedTexture upper, lower, flare;
  private final AnimationChannel idle, tilt, vertical, fire, animIdle, animRun, animJump;

  private long firingCount = 0;
  private final Clancy clancy;

  public ClancyComponent(Clancy clancy) {
    this.clancy = clancy;

    Image upperImg = upperImage.image();
    Image runImg = runImage.image();
    Image jumpImg = jumpImage.image();
    Image flareImg = flareImage.image();

    idle = new AnimationChannel(upperImg, 4, 175, 175, Duration.seconds(1), 0, 0);
    fire = new AnimationChannel(upperImg, 4, 175, 175, Duration.seconds(1), 1, 1);
    tilt = new AnimationChannel(upperImg, 4, 175, 175, Duration.seconds(1), 2, 2);
    vertical = new AnimationChannel(upperImg, 4, 175, 175, Duration.seconds(1), 3, 3);
    animIdle = new AnimationChannel(jumpImg, 10, 120, 110, Duration.seconds(1), 0, 0);
    animRun = new AnimationChannel(runImg, 12, 120, 110, Duration.seconds(0.5), 0, 11);
    animJump = new AnimationChannel(jumpImg, 10, 120, 110, Duration.seconds(1.5), 1, 9);
    var flareChannel = new AnimationChannel(flareImg, 15, 150, 125, Duration.seconds(0.5), 0, 12);

    upper = new AnimatedTexture(idle);
    upper.setTranslateX(-5);
    upper.setTranslateY(-80);
    lower = new AnimatedTexture(animIdle);
    lower.translateXProperty().bind(upper.translateXProperty().subtract(10));
    lower.translateYProperty().bind(upper.translateYProperty().add(100));
    flare = new AnimatedTexture(flareChannel);
    flare.setVisible(false);
    flare.loop();
  }

  @Override
  public void onAdded() {
    entity.getViewComponent().addChild(lower);
    entity.getViewComponent().addChild(upper);
    entity.getViewComponent().addChild(flare);
  }

  @Override
  public void onUpdate(double tpf) {
    if (firing) {
      if (!flare.isVisible()) {
        flare.loop();
        flare.setVisible(true);
      }
      firingCount++;
    } else {
      firingCount = 0;
      flare.setVisible(false);
    }

    entity.getComponentOptional(MovableComponent.class).ifPresentOrElse(movableComponent -> {
      if ((movableComponent.isMovingLeft() || movableComponent.isMovingRight()) && upward) {
        upper.loop(tilt);
        flare.setRotate(-35);
//        flare.setTranslateX(45);
//        flare.setTranslateY(-95);
        flare.translateXProperty().bind(upper.translateXProperty().add(50));
        flare.translateYProperty().bind(upper.translateYProperty().subtract(15));

        if (firingCount % 4 == 1) {
          var data = new SpawnData(entity.getX() + entity.getScaleX() * 125, entity.getY() + (-5 * entity.getScaleX() - 80) + Math.random() * 3, entity.getZIndex())
            .put("angle", entity.getScaleX() > 0 ? Angle.DEGREE_35_POINT.toPoint2D() : Angle.DEGREE_145_POINT.toPoint2D())
            .put("hp", clancy.getAttack());
          shoot(data);
        }
      } else
        updateWithoutMoving();
    }, () -> {
      entity.getComponentOptional(PhysicsComponent.class).ifPresent(physics -> physics.setVelocityX(0));

      updateWithoutMoving();
    });

    entity.getComponentOptional(GroundComponent.class).ifPresent(ground -> {
      if (ground.isStandingOnSth()) {
        entity.getComponentOptional(PhysicsComponent.class).ifPresentOrElse(physics -> {
          if (physics.getVelocityX() != 0) {
            lower.loopNoOverride(animRun);
          } else {
            lower.loopNoOverride(animIdle);
          }
        }, () -> lower.loopNoOverride(animIdle));
      }
    });
  }

  private void updateWithoutMoving() {
    if (firingCount % 4 == 1) {
      SpawnData data;
      if (upward) {
        data = new SpawnData(entity.getX() - 10 + entity.getScaleX() * 28 + (Math.random() * 6 - 3) * 0.5, entity.getY() - 100 + Math.random() * 3, entity.getZIndex())
          .put("angle", Angle.DEGREE_90_POINT.toPoint2D())
          .put("hp", clancy.getAttack());
      } else {
        data = new SpawnData(entity.getX() + entity.getScaleX() * 135, entity.getY() + 10 + Math.random() * 3, entity.getZIndex())
          .put("angle", entity.getScaleX() > 0 ? Angle.DEGREE_0_POINT.toPoint2D() : Angle.DEGREE_180_POINT.toPoint2D())
          .put("hp", clancy.getAttack());
      }
      shoot(data);
    }

    if (upward) {
      upper.loop(vertical);
      flare.setRotate(-90);
//      flare.setTranslateX(-5);
//      flare.setTranslateY(-110);
      flare.translateXProperty().bind(upper.translateXProperty());
      flare.translateYProperty().bind(upper.translateYProperty().subtract(30));
    } else if (firing) {
      upper.loop(fire);
      flare.setRotate(0);
//      flare.setTranslateX(60);
//      flare.setTranslateY(-38);
      flare.translateXProperty().bind(upper.translateXProperty().add(65));
      flare.translateYProperty().bind(upper.translateYProperty().add(42));
    } else
      upper.loop(idle);
  }

  public void moveRight() {
    entity.getComponentOptional(MovableComponent.class).ifPresent(MovableComponent::moveRight);
  }

  public void moveLeft() {
    entity.getComponentOptional(MovableComponent.class).ifPresent(MovableComponent::moveLeft);
  }

  public void stopMovingRight() {
    entity.getComponentOptional(MovableComponent.class).ifPresent(MovableComponent::stopMovingRight);
  }

  public void stopMovingLeft() {
    entity.getComponentOptional(MovableComponent.class).ifPresent(MovableComponent::stopMovingLeft);
  }

  public void jump() {
    entity.getComponentOptional(GroundComponent.class).ifPresent(ground -> {
      if (ground.isStandingOnSth()) {
        lower.play(animJump);
        entity.getComponentOptional(PhysicsComponent.class).ifPresent(physics -> physics.setVelocityY(-720));
      }
    });
  }

  public void fire() {
    firing = true;
  }

  public void stopFire() {
    firing = false;
  }

  public void upward() {
    upward = true;
  }

  public void stopUpward() {
    upward = false;
  }

  public static EntityBuilder builder(Component... components) {
    var builder = FXGL.entityBuilder().type(Type.CLANCY);
    for (var component : components)
      builder.with(component);
    return builder;
  }

  public static Entity of(SpawnData data, Component... components) {
    return of(builder(), data, components);
  }

  public static Entity of(EntityBuilder builder, SpawnData data, Component... components) {
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);

    var clancy = data.<Clancy>get(PropertyKey.CLANCY);

    return builder
      .at(data.getX(), data.getY())
      .collidable()
      .bbox(new HitBox(BoundingShape.box(80, 120)))
//      .view(new Rectangle(80,120, Color.RED))
      .with(new GroundComponent(), physics, new EffectComponent(), new ClancyComponent(clancy),
        new IDComponent(clancy.getName(), 0),
        new HealthIntComponent(clancy.getHp()),
        new DestroyableComponent(DestroyableComponent.DestroyType.FADE_OUT), new MovableComponent(),
        new CampComponent(data.hasKey("camp") ? data.get("camp") : Camp.ALLIANCE, true))
      .with(components)
      .with(new AfterUpdateComponent())
      .zIndex(Integer.MAX_VALUE -80 * 120)
      .build();
  }

  public static final Point2D DEGREE_35_POINT = new Point2D(FXGLMath.cosDeg(35), -FXGLMath.sinDeg(35));
  public static final Point2D DEGREE_145_POINT = new Point2D(FXGLMath.cosDeg(145), -FXGLMath.sinDeg(145));

  public enum Angle {
    DEGREE_35_POINT, DEGREE_145_POINT, DEGREE_180_POINT, DEGREE_0_POINT, DEGREE_90_POINT;

    public Point2D toPoint2D() {
      return switch (this) {
        case DEGREE_0_POINT -> new Point2D(1, 0);
        case DEGREE_180_POINT -> new Point2D(-1, 0);
        case DEGREE_35_POINT -> ClancyComponent.DEGREE_35_POINT;
        case DEGREE_145_POINT -> ClancyComponent.DEGREE_145_POINT;
        case DEGREE_90_POINT -> new Point2D(0, -1);
      };
    }
  }

  private void shoot(SpawnData data) {
    FXGL.spawn(Type.BULLET, data, entity.getWorld());
  }
}