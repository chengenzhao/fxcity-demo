package com.example.fxcitydemo.gameworld.entity.ammo;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.example.fxcitydemo.gameworld.Type;
import com.example.fxcitydemo.gameworld.entity.components.Camp;
import com.example.fxcitydemo.gameworld.entity.components.CampComponent;
import com.example.fxcitydemo.gameworld.entity.components.DestroyableComponent;
import com.example.fxcitydemo.gameworld.entity.components.EventuallyExplodeComponent;
import com.whitewoodcity.fxgl.app.ImageData;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class LauncherMissile extends Component {
  private static final ImageData imageData = new ImageData("units/vehicle/launcher/missile.png", 124, 57);

  public static List<ImageData> getImageData() {
    return List.of(imageData);
  }

  Texture texture;
  boolean downwardAdjusted = true;

  public LauncherMissile() {
    Image img = imageData.image();

    texture = new Texture(img);
  }

  public LauncherMissile(double deltaX, double deltaY) {
    this();
    if (deltaX < deltaY * 1.2) {
      downwardAdjusted = false;
    }
  }

  private Point2D lastDirection = new Point2D(0, 0);

  @Override
  public void onUpdate(double tpf) {
    super.onUpdate(tpf);

    entity.getComponentOptional(ProjectileComponent.class)
      .ifPresent(projectile -> {
        if (downwardAdjusted) {
          var direction = projectile.getDirection();
          if (FXGLMath.abs(direction.getY()) > 0.001) {
            var y = FXGLMath.abs(direction.getY());
            y -= 0.01 * 60 * tpf;
            if (y < 0) y = 0;
            projectile.setDirection(new Point2D(direction.getX(), -y));
          }
        }
        if (projectile.getSpeed() == 0) {
          entity.setX(entity.getX() + tpf * 150 * lastDirection.getX());
          entity.setY(entity.getY() + tpf * 150 * lastDirection.getY());
        }else{
          lastDirection = projectile.getDirection();
        }
      });
  }

  @Override
  public void onAdded() {
    entity.getViewComponent().addChild(texture);
  }

  public static Entity of(SpawnData data) {
    var missile = new LauncherMissile();
    if (data.getData().containsKey("delta")) {
      var delta = data.<Point2D>get("delta");
      var deltaX = delta.getX();
      var deltaY = delta.getY();
      missile = new LauncherMissile(deltaX, deltaY);
    }

    var direction = data.getData().containsKey("direction") ? (Point2D) data.get("direction") : new Point2D(-1, 0);

    return entityBuilder()
      .at(data.getX(), data.getY())
      .type(Type.LAUNCHER_MISSILE)
      .collidable()
      .bbox(new HitBox(BoundingShape.box(124, 57)))
//      .view(new Rectangle(124, 57, Color.RED))
      .with(new EffectComponent())
      .with(new HealthIntComponent(300))
      .with(new ProjectileComponent(direction, 1000))
      .with(missile)
      .with(new DestroyableComponent(DestroyableComponent.DestroyType.SHELL_EXPLOSION))
      .with(new CampComponent(data.hasKey("camp") ? data.get("camp") : Camp.EMPIRE))
      .with(new EventuallyExplodeComponent(60))
      .zIndex((int) data.getZ())
      .build();
  }
}
