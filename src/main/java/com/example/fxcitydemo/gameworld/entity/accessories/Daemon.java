package com.example.fxcitydemo.gameworld.entity.accessories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.example.fxcitydemo.gameworld.Type;
import com.example.fxcitydemo.gameworld.entity.components.CampComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class Daemon extends Component {
  private final boolean isDaemon;
  private final List<Entity> daemons;
  private final Entity mainEntity;

  public Daemon(boolean isDaemon, Entity... entities) {
    this.isDaemon = isDaemon;
    if (isDaemon) {
      assert entities.length > 0;
      mainEntity = entities[0];
      daemons = null;
    } else {
      mainEntity = null;
      daemons = new ArrayList<>();
      daemons.addAll(Arrays.stream(entities).toList());
    }
  }

  public void addDaemons(Entity... entities) {
    assert daemons != null;
    daemons.addAll(Arrays.stream(entities).toList());
  }

  public boolean isDaemon() {
    return isDaemon;
  }

  public List<Entity> getDaemons() {
    assert daemons != null;
    return daemons;
  }

  public Entity getMainEntity() {
    return mainEntity;
  }

  public static Entity of(Type type, Entity entity) {
    assert entity != null;

    var daemon = entityBuilder()
      .at(entity.getX(), entity.getY())
      .type(type)
      .bbox(new HitBox(BoundingShape.box(entity.getWidth(), entity.getHeight())))
//      .view(new Rectangle(entity.getWidth(), entity.getHeight(), Color.RED))
      .with(new Daemon(true, entity))
      .collidable()
      .build();

    entity.getComponentOptional(CampComponent.class).ifPresent(campComponent -> {
      daemon.addComponent(new CampComponent(campComponent.camp()));
    });

    entity.getComponentOptional(PhysicsComponent.class).ifPresentOrElse(physicsComponent -> {
      var physics = new PhysicsComponent();
      physics.setBodyType(physicsComponent.getBody().getType());
      daemon.addComponent(physics);
    }, () -> {
      daemon.xProperty().bind(entity.xProperty());
      daemon.yProperty().bind(entity.yProperty());
      daemon.rotationXProperty().bind(entity.rotationXProperty());
      daemon.rotationYProperty().bind(entity.rotationYProperty());
      daemon.rotationZProperty().bind(entity.rotationZProperty());
    });

    entity.setOnNotActive(() -> entity.getComponent(Daemon.class).getDaemons().forEach(Entity::removeFromWorld));

    entity.getComponentOptional(Daemon.class).ifPresentOrElse(
      daemonComponent -> daemonComponent.addDaemons(daemon)
      , () -> entity.addComponent(new Daemon(false, daemon))
    );

    return daemon;
  }
}
