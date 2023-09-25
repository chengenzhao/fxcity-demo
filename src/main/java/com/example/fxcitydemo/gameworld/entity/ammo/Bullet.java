package com.example.fxcitydemo.gameworld.entity.ammo;

import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.fxcitydemo.gameworld.Type;
import com.example.fxcitydemo.gameworld.entity.components.Camp;
import com.example.fxcitydemo.gameworld.entity.components.CampComponent;
import com.whitewoodcity.fxgl.app.ImageData;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class Bullet extends Component {

  private static final ImageData imageData = new ImageData("units/gi/bullet.png", 400, 25);

  public static List<ImageData> getImageData() {
    return List.of(imageData);
  }

  AnimatedTexture texture;
//  static Image img = FXGL.image("player/bullet.png", 800*ratio, 50*ratio);

  public Bullet() {
    Image img = imageData.image();
    var channel = new AnimationChannel(img, 4, 100, 25, Duration.seconds(0.2), 0, 3);
    texture = new AnimatedTexture(channel);
    texture.loop();
  }

  @Override
  public void onAdded() {
    entity.getViewComponent().addChild(texture);
  }

  @Override
  public void onRemoved() {
    super.onRemoved();
  }

  @Override
  public void onUpdate(double tpf) {
    super.onUpdate(tpf);
  }

  public static Entity of(SpawnData data) {
    var x = data.getX();
    var y = data.getY();
    var z = data.getZ();
    var angle = data.<Point2D>get("angle");

    var builder = entityBuilder()
      .at(x, y)
      .type(Type.BULLET)
      .bbox(new HitBox(BoundingShape.box(100, 25)));
//      .view(new Rectangle(200*ratio, 50*ratio, Color.WHITE))

//    if(data.hasKey("z")){
//      builder.with(new ZComponent(data.<Number>get("z").doubleValue()));
//    }

    return builder
      .with(new ProjectileComponent(angle, 1750))
      .with(new ExpireCleanComponent(Duration.seconds(0.5)))
//      .with(new OffscreenCleanComponent())
      .with(new HealthIntComponent(data.get("hp")))
      .with(new Bullet())
      .with(new CampComponent(data.hasKey("camp") ? data.get("camp") : Camp.ALLIANCE))
      .zIndex((int)z)
      .collidable()
      .build();
  }
}
