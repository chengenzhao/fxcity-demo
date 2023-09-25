package com.example.fxcitydemo.gameworld.entity;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.example.fxcitydemo.GameApp;
import com.example.fxcitydemo.gameworld.Type;
import com.example.fxcitydemo.gameworld.entity.accessories.Shadow;
import com.example.fxcitydemo.gameworld.entity.components.*;
import com.whitewoodcity.fxgl.app.ImageData;
import com.whitewoodcity.fxgl.dsl.FXGL;
import com.whitewoodcity.fxgl.texture.AnimatedTexture;
import com.whitewoodcity.fxgl.texture.AnimationChannel;
import com.whitewoodcity.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.List;

public class Launcher extends Component {

  private static final ImageData
    bodyImage = new ImageData("units/vehicle/launcher/body.png", 240, 150),
    whlsImage = new ImageData("units/vehicle/launcher/wheels.png", 600, 100),
    lnchImage = new ImageData("units/vehicle/launcher/launcher.png", 1470, 200),
    flmeImage = new ImageData("units/vehicle/launcher/flame.png", 1792, 256);

  public static List<ImageData> getImageData() {
    return List.of(bodyImage, whlsImage, lnchImage, flmeImage);
  }

  int i = -FXGLMath.random(0, 100);
  int width = 160, height = 140;

  Texture body;
  AnimatedTexture wheels, flame, launcher;//, launcher0to17, launcher17to34;

  private final int flameCenterX = 160, flameCenterY = 130;
  Rotate rotate = new Rotate(0, flameCenterX, flameCenterY);

  public Launcher() {
    Image bodyImg = bodyImage.image();
    Image whlsImg = whlsImage.image();
    Image lnchImg = lnchImage.image();
    Image flmeImg = flmeImage.image();

    body = new Texture(bodyImg);
    var whlsChannel = new AnimationChannel(whlsImg, 3, 200, 100, Duration.seconds(0.15), 0, 2);
    wheels = new AnimatedTexture(whlsChannel);
    var lnchChannel = new AnimationChannel(lnchImg, 7, 210, 200, Duration.seconds(0.7), 0, 6);
    launcher = new AnimatedTexture(lnchChannel);
    var flmeChannel = new AnimationChannel(flmeImg, 7, 256, 256, Duration.seconds(0.5), 0, 6);
    flame = new AnimatedTexture(flmeChannel);
  }

  @Override
  public void onAdded() {
    super.onAdded();
    entity.getViewComponent().addChild(wheels);
    wheels.loop();
    wheels.setTranslateY(63);
    wheels.setTranslateX(-20);
    entity.getViewComponent().addChild(body);
    body.setTranslateX(-30);
    body.setTranslateY(13);
    entity.getViewComponent().addChild(launcher);
    launcher.setTranslateX(-15);
    launcher.setTranslateY(-112);

    flame.setVisible(false);
    flame.setTranslateX(-flameCenterX + 8);
    flame.setTranslateY(-flameCenterY + 10);
    flame.getTransforms().addAll(rotate);

    entity.getViewComponent().addChild(flame);

    entity.getComponentOptional(CampComponent.class).ifPresent(campComponent -> {
      var camp = campComponent.camp();
      if (camp == Camp.ALLIANCE) {
        entity.setScaleX(-1);
      }
    });
  }

  @Override
  public void onUpdate(double tpf) {
    entity.getComponentOptional(MovingForwardComponent.class)
      .ifPresent(movingForwardComponent -> {
        if (movingForwardComponent.getStatus() == Status.RUN) {
          var count = movingForwardComponent.getStatusCount();
          if (count == 0) wheels.loop();
          if (count / 5 % 2 == 0) {
            body.setTranslateY(13);
            launcher.setTranslateY(-112);
          } else if (count / 5 % 4 == 1) {
            body.setTranslateY(12);
            launcher.setTranslateY(-113);
          } else if (count / 5 % 4 == 3) {
            body.setTranslateY(14);
            launcher.setTranslateY(-111);
          }

          entity.getComponentOptional(GroundComponent.class).ifPresent(ground -> {
            if (ground.isBlocked() && ground.isStandingOnSth()) {
              entity.getComponentOptional(PhysicsComponent.class).ifPresent(physics -> physics.setVelocityY(-100));
            }
          });
        } else if (movingForwardComponent.getStatus() == Status.FIRE) {
          var count = movingForwardComponent.getStatusCount();
          if (count == 0) wheels.stop();
          if (count % 240 == 1) {
            var frontlineService = FXGL.<GameApp>getAppCast().getFrontlineService();
            if (frontlineService == null) return;
            int target;
            Point2D targetPosition;
            var y = entity.getScaleX() == 1 ? frontlineService.getPlayerY() : frontlineService.getEnemyY();
            if (y >= entity.getY() - 100) {
              target = 0;
            } else if (y >= entity.getY() - 200) {
              target = 17;
            } else {
              target = 34;
            }
            targetPosition = new Point2D(entity.getScaleX() == 1 ? frontlineService.getPlayerX() : frontlineService.getEnemyX(), y);

            switch (target) {
              case 34 -> {
                var direction = new Point2D(-1 * entity.getScaleX(), -Math.tan(34 / 180.0 * Math.PI));
                launcher.playTo(6, () -> fire(direction, targetPosition.getX(), targetPosition.getY()));
                flame.setTranslateX(-flameCenterX + 50);
                flame.setTranslateY(-flameCenterY - 70);
                rotate.setAngle(34);
              }
              case 17 -> {
                var direction = new Point2D(-1 * entity.getScaleX(), -Math.tan(17 / 180.0 * Math.PI));
                launcher.playTo(3, () -> fire(direction));
                flame.setTranslateX(-flameCenterX + 20);
                flame.setTranslateY(-flameCenterY - 32);
                rotate.setAngle(17);
              }
              default -> {//0
                launcher.playTo(0, () -> fire(entity.getScaleX()));
                flame.setTranslateX(-flameCenterX + 8);
                flame.setTranslateY(-flameCenterY + 10);
                rotate.setAngle(0);
              }
            }
          }
        }
      });
  }

  public void fire(double scaleX) {
    flame.setVisible(true);
    flame.setOnCycleFinished(() -> flame.setVisible(false));
    flame.play();

    var data = new SpawnData(entity.getX() - 120 * scaleX, entity.getY() - 15, entity.getZIndex()).put("direction", new Point2D(-scaleX, 0));

    entity.getComponentOptional(CampComponent.class).ifPresent(campComponent -> data.put("camp", campComponent.camp()));

    FXGL.spawn(Type.LAUNCHER_MISSILE, data, entity.getWorld());
  }

  public void fire(Point2D direction) {
    flame.setVisible(true);
    flame.setOnCycleFinished(() -> flame.setVisible(false));
    flame.play();

    var x = direction.getY() > -0.5 ? entity.getX() - 110 * entity.getScaleX() : entity.getX() - 70 * entity.getScaleX();
    var y = direction.getY() > -0.5 ? entity.getY() - 83 : entity.getY() - 135;

    var data = new SpawnData(x, y, entity.getZIndex())
      .put("direction", direction);

    entity.getComponentOptional(CampComponent.class).ifPresent(campComponent -> data.put("camp", campComponent.camp()));

    FXGL.spawn(Type.LAUNCHER_MISSILE, data, entity.getWorld());
  }

  public void fire(Point2D direction, double targetX, double targetY) {
    flame.setVisible(true);
    flame.setOnCycleFinished(() -> flame.setVisible(false));
    flame.play();

    var x = direction.getY() > -0.5 ? entity.getX() - 110 * entity.getScaleX() : entity.getX() - 70 * entity.getScaleX();
    var y = direction.getY() > -0.5 ? entity.getY() - 83 : entity.getY() - 135;

    var data = new SpawnData(x, y, entity.getZIndex())
      .put("direction", direction)
      .put("delta", new Point2D(FXGLMath.abs(x - targetX), FXGLMath.abs(y - targetY)));

    entity.getComponentOptional(CampComponent.class).ifPresent(campComponent -> data.put("camp", campComponent.camp()));

    FXGL.spawn(Type.LAUNCHER_MISSILE, data, entity.getWorld());
  }

  public static EntityBuilder builder(Component... components) {
    var builder = FXGL.entityBuilder().type(Type.LAUNCHER);
    for (var component : components)
      builder.with(component);
    return builder;
  }

  public static Entity of(SpawnData data) {
    return of(builder(), data);
  }

  public static Entity of(EntityBuilder builder, SpawnData data, Component... components) {
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);

    var launcher = new Launcher();
    Camp camp = data.hasKey("camp") ? data.get("camp") : Camp.EMPIRE;

    return builder
      .at(data.getX(), data.getY())
      .collidable()
      .bbox(new HitBox(BoundingShape.box(launcher.width, launcher.height)))
//      .view(new Rectangle(launcher.width, launcher.height, Color.BLUE))
      .with(new GroundComponent())
      .with(physics)
      .with(new EffectComponent())
      .with(new HealthIntComponent(300))
      .with(new DestroyableComponent(DestroyableComponent.DestroyType.GROUND_EXPLOSION))
      .with(new MovingForwardComponent(camp, 100 + FXGLMath.random(-5, 5), 1000, 240))
      .with(new CampComponent(camp, true))
      .with(launcher)
      .with(components)
      .with(new AfterUpdateComponent())
      .zIndex(Integer.MAX_VALUE - launcher.width * launcher.height + launcher.i)
      .build();
  }

  public static Entity of(Entity mainEntity, Component... components) {
    return of(mainEntity, new SpawnData(), components);
  }

  public static Entity of(Entity mainEntity, SpawnData data, Component... components) {
    return of(builder(), data, mainEntity, components);
  }

  public static Entity of(EntityBuilder builder, SpawnData data, Entity mainEntity, Component... components) {
    var entity = of(builder, data, components);

    assert (mainEntity.hasComponent(Shadow.class));

    var shadowWidthCenter = mainEntity.getComponent(Shadow.class).getWidth() / 2;
    var shadowHeightCenter = mainEntity.getComponent(Shadow.class).getHeight() / 2;

    entity.removeComponent(PhysicsComponent.class);
    entity.removeComponent(GroundComponent.class);
    entity.getComponentOptional(MovingForwardComponent.class).ifPresent(comp -> comp.setMainEntity(mainEntity));

    entity.xProperty().bind(mainEntity.xProperty().subtract(entity.getWidth() / 2).add(shadowWidthCenter));
    entity.yProperty().bind(mainEntity.yProperty().subtract(entity.getHeight()).add(shadowHeightCenter));
    entity.setZIndex((int) (mainEntity.getY() + shadowHeightCenter));
    mainEntity.yProperty().map(Number::intValue)
      .addListener((_1, _2, newValue) -> entity.setZIndex((int) (newValue + shadowHeightCenter)));

    entity.getComponentOptional(DestroyableComponent.class).ifPresent(destroyableComponent ->
      destroyableComponent.setOnDestroyBegin(e -> {
        entity.xProperty().unbind();
        entity.yProperty().unbind();
        mainEntity.removeFromWorld();
      }));

    return entity;
  }

}
