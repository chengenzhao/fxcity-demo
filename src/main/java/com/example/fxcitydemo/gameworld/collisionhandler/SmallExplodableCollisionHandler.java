package com.example.fxcitydemo.gameworld.collisionhandler;

import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthDoubleComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.whitewoodcity.xtrike.gameworld.entity.Type;
import com.whitewoodcity.xtrike.gameworld.entity.accessories.Daemon;
import com.whitewoodcity.xtrike.gameworld.entity.components.DestroyableComponent;
import com.whitewoodcity.xtrike.gameworld.entity.effect.BrightEffect;
import javafx.util.Duration;

public class SmallExplodableCollisionHandler extends CollisionHandlerWithPlayerHitEffect {

  /**
   * The order of types determines the order of entities in callbacks.
   *
   * @param a entity type of the first entity
   * @param b entity type of the second entity
   */
  public SmallExplodableCollisionHandler(Type a, Type b) {
    super(a, b);
  }

  @Override
  protected void onCollisionBegin(Entity entity, Entity shell) {
    super.onCollisionBegin(entity, shell);
    if (fromSameCamp(entity, shell) || notOnTheSameLine(entity, shell)) return;

    shell.getComponentOptional(Daemon.class).ifPresentOrElse(daemon -> {
      if (daemon.isDaemon()) {
        collideBegin(entity, daemon.getMainEntity());
      } else
        collideBegin(entity, shell);
    }, () -> collideBegin(entity, shell));
  }

  private void collideBegin(Entity entity, Entity shell) {
    shell.getComponentOptional(DestroyableComponent.class).ifPresent(DestroyableComponent::destroy);
    shell.getComponentOptional(HealthIntComponent.class).ifPresent(HealthIntComponent::damageFully);
  }

  @Override
  protected void onCollision(Entity entity, Entity shell) {
    super.onCollision(entity, shell);
    if (fromSameCamp(entity, shell) || notOnTheSameLine(entity, shell)) return;

    shell.getComponentOptional(Daemon.class).ifPresentOrElse(daemon -> {
      if (daemon.isDaemon()) {
        collide(entity, daemon.getMainEntity());
      } else
        collide(entity, shell);
    }, () -> collide(entity, shell));
  }

  private void collide(Entity entity, Entity shell) {
    shell.getComponentOptional(HealthIntComponent.class).ifPresent(dmgComponent -> {
      var damage = dmgComponent.getMaxValue() / 30;
      entity.getComponentOptional(HealthIntComponent.class).ifPresent(healthIntComponent -> healthIntComponent.damage(damage));
      entity.getComponentOptional(HealthDoubleComponent.class).ifPresent(healthDoubleComponent -> healthDoubleComponent.damage(damage));
    });
    shell.getComponentOptional(HealthDoubleComponent.class).ifPresent(dmgComponent -> {
      var damage = dmgComponent.getMaxValue() / 30;
      entity.getComponentOptional(HealthIntComponent.class).ifPresent(healthIntComponent -> healthIntComponent.damage((int) damage));
      entity.getComponentOptional(HealthDoubleComponent.class).ifPresent(healthDoubleComponent -> healthDoubleComponent.damage(damage));
    });

    entity.getComponentOptional(HealthIntComponent.class).ifPresent(health -> {
      if (health.isZero()) {
        entity.getComponentOptional(DestroyableComponent.class)
          .ifPresent(DestroyableComponent::destroy);
      } else {
        entity.getComponentOptional(EffectComponent.class).ifPresent((effectComponent) ->
          effectComponent.startEffect(new BrightEffect(Duration.seconds(1.0 / 60))));
      }
    });
    entity.getComponentOptional(HealthDoubleComponent.class).ifPresent(health -> {
      if (health.isZero()) {
        entity.getComponentOptional(DestroyableComponent.class)
          .ifPresent(DestroyableComponent::destroy);
      } else {
        entity.getComponentOptional(EffectComponent.class).ifPresent((effectComponent) ->
          effectComponent.startEffect(new BrightEffect(Duration.seconds(1.0 / 60))));
      }
    });
  }
}
