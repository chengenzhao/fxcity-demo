package com.example.fxcitydemo.gameworld.entity.characters;

import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.IDComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.fxcitydemo.gameworld.PropertyKey;
import com.example.fxcitydemo.gameworld.Type;
import com.example.fxcitydemo.gameworld.entity.components.*;
import com.whitewoodcity.fxgl.app.ImageData;
import com.whitewoodcity.fxgl.dsl.FXGL;
import com.whitewoodcity.fxgl.entity.XSpawnData;
import com.whitewoodcity.javafx.binding.XBindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class ClancyViewComponent2 extends Component {
  private final Clancy clancy;

  private static final ImageData
    upperImage = new ImageData("units/gi/upper.png", 700, 175),
    runImage = new ImageData("units/gi/run.png", 1440, 110),
    jumpImage = new ImageData("units/gi/jump.png", 1200, 110),
    flareImage = new ImageData("units/gi/fire.png", 2250, 125);

  private final Entity mainEntity;

  private final AnimatedTexture upper, lower, flare;
  private final AnimationChannel idle, tilt, vertical, fire, animIdle, animRun, animJump;

  private final DoubleProperty heightProperty = new SimpleDoubleProperty(0);
  private double velocityY = 0;
  private final double gravity = 960;

  private long firingCount = 0;

  public ClancyViewComponent2(Clancy clancy, Entity mainEntity) {
    this.clancy = clancy;
    this.mainEntity = mainEntity;
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
    upper.setTranslateX(-10);
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

    mainEntity.yProperty().map(Number::intValue)
      .addListener((_1, _2, newValue) -> entity.setZIndex(getEntityZIndex(newValue)));

    mainEntity.getComponentOptional(ShadowComponent.class)
      .ifPresent(shadowComponent -> shadowComponent.bindHeightPropertyTo(heightProperty));
  }

  @Override
  public void onUpdate(double tpf) {

    velocityY -= gravity * tpf;
    if (heightProperty.get() + velocityY * tpf > 0) {
      heightProperty.set(heightProperty.get() + velocityY * tpf);
    } else {
      heightProperty.set(0);
      velocityY = 0;
    }

    mainEntity.getComponentOptional(ClancyComponent2.class).ifPresent(clancyComponent2 -> {
      if (clancyComponent2.isJumping() && heightProperty.get() < 0.00001) {
        velocityY = 720;
        lower.playAnimationChannel(animJump);
      }
      clancyComponent2.jumped();

      if (clancyComponent2.isFiring()) {
        if (!flare.isVisible()) {
          flare.loop();
          flare.setVisible(true);
        }
        firingCount++;
      } else {
        firingCount = 0;
        flare.setVisible(false);
      }

      mainEntity.getComponentOptional(MovableComponent2.class).ifPresentOrElse(movableComponent -> {
        if ((movableComponent.isMovingLeft() || movableComponent.isMovingRight()) && clancyComponent2.isUpward()) {
          upper.loopAnimationChannel(tilt);
          flare.setRotate(-35);
          flare.translateXProperty().bind(upper.translateXProperty().add(50));
          flare.translateYProperty().bind(upper.translateYProperty().subtract(15));

          if (firingCount % 4 == 1) {
            var data = new SpawnData(entity.getX() + entity.getScaleX() * 120, entity.getY() + (-5 * entity.getScaleX() - 80) + Math.random() * 3, getEntityZIndex(mainEntity))
              .put("angle", entity.getScaleX() > 0 ? ClancyComponent.Angle.DEGREE_35_POINT.toPoint2D() : ClancyComponent.Angle.DEGREE_145_POINT.toPoint2D())
              .put("hp", clancy.getAttack());
            shoot(data);
          }
        } else {
          updateWithoutHorizontalMoving(clancy, clancyComponent2);
        }
        //lower part
        if (heightProperty.get() < 0.00001 && Math.abs(velocityY) < 0.00001) {
          mainEntity.getComponentOptional(PhysicsComponent.class).ifPresentOrElse(physics -> {
            if (physics.isMoving()) lower.loopNoOverride(animRun);
            else lower.loopNoOverride(animIdle);
          }, () -> lower.loopNoOverride(animIdle));
        }

      }, () -> {
        updateWithoutHorizontalMoving(clancy, clancyComponent2);
      });
    });
  }

  private void updateWithoutHorizontalMoving(Clancy clancy, ClancyComponent2 clancyComponent2) {
    if (firingCount % 4 == 1) {
      if (clancyComponent2.isUpward()) {
        var data = new SpawnData(entity.getX() - 10 + entity.getScaleX() * 23 + (Math.random() * 6 - 3) * 0.5, entity.getY() - 100 + Math.random() * 3, getEntityZIndex(mainEntity))
          .put("angle", ClancyComponent.Angle.DEGREE_90_POINT.toPoint2D())
          .put("hp", clancy.getAttack());
        shoot(data);
      } else {
        var data = new SpawnData(entity.getX() + entity.getScaleX() * 130, entity.getY() + 10 + Math.random() * 3, entity.getZIndex())
          .put("angle", entity.getScaleX() > 0 ? ClancyComponent.Angle.DEGREE_0_POINT.toPoint2D() : ClancyComponent.Angle.DEGREE_180_POINT.toPoint2D())
          .put("hp", clancy.getAttack());//.put("z", entity.getZIndex())
        shoot(data);
      }
    }

    if (clancyComponent2.isUpward()) {
      upper.loopAnimationChannel(vertical);
      flare.setRotate(-90);
      flare.translateXProperty().bind(upper.translateXProperty());
      flare.translateYProperty().bind(upper.translateYProperty().subtract(30));
    } else if (clancyComponent2.isFiring()) {
      upper.loopAnimationChannel(fire);
      flare.setRotate(0);
      flare.translateXProperty().bind(upper.translateXProperty().add(65));
      flare.translateYProperty().bind(upper.translateYProperty().add(42));
    } else
      upper.loopAnimationChannel(idle);
  }

  private void shoot(SpawnData data) {
    FXGL.spawn(Type.BULLET, data, entity.getWorld());
  }

  public static Entity of(Entity mainEntity, String key, Object value, Component... components) {
    return of(mainEntity, new XSpawnData(key, value), components);
  }

  public static Entity of(Entity mainEntity, SpawnData data, Component... components) {

    var clancy = data.<Clancy>get(PropertyKey.CLANCY);
    var clancyComponent = new ClancyViewComponent2(clancy, mainEntity);

    var entity = FXGL.entityBuilder()
      .type(Type.CLANCY)
      .at(mainEntity.getX() - 20, mainEntity.getY() - 100)
      .collidable()
      .bbox(new HitBox(BoundingShape.box(80, 50)))
//      .view(new Rectangle(80,50))
      .with(clancyComponent, new IDComponent(clancy.getName(), 0), new HealthIntComponent(clancy.getHp()))
      .with(new EffectComponent())
      .with(new DestroyableComponent(DestroyableComponent.DestroyType.FADE_OUT)
        .setOnDestroyBegin(e -> {
          e.xProperty().unbind();
          e.yProperty().unbind();
          e.scaleXProperty().unbind();
          mainEntity.removeFromWorld();
        }))
//      .with(new ZComponent(mainEntity, 20))
      .with(new CampComponent(data.hasKey("camp") ? data.get("camp") : Camp.ALLIANCE, true))
      .with(components)
      .zIndex(getEntityZIndex(mainEntity))
      .build();

    entity.xProperty().bind(mainEntity.xProperty().subtract(20));
    entity.yProperty().bind(
      XBindings.reduce(mainEntity.yProperty(), clancyComponent.heightProperty,
        (y, h) -> y.doubleValue() - 100 - h.doubleValue()));
    entity.scaleXProperty().bind(mainEntity.scaleXProperty());

    return entity;
  }

  private static int getEntityZIndex(Entity entity){
    return getEntityZIndex(entity.getY());
  }

  private static int getEntityZIndex(double zValue){
    return (int)zValue + 20;
  }
}
