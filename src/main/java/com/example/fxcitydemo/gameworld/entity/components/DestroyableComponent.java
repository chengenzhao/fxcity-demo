package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.fxcitydemo.gameworld.entity.effect.FadeOutEffect;
import com.whitewoodcity.fxgl.app.ImageData;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.List;

public class DestroyableComponent extends Component {
  public enum DestroyType {
    FADE_OUT, OTHERS, GROUND_EXPLOSION, AIR_EXPLOSION, MASSIVE_AIR_EXPLOSIONS, SHELL_EXPLOSION, FIRE_BALL_EXPLOSION
  }

  private final DestroyType destroyType;

  private EntityRunnable onDestroy;
  private EntityRunnable onDestroyBegin = null;

  public static List<ImageData> getImageData(){
    return List.of(
      new ImageData("explosions/ground.png", 5700, 250),
      new ImageData("explosions/air.png", 3780, 270),
      new ImageData("explosions/shell.png", 1800, 220));
  }

  public DestroyableComponent(EntityRunnable onDestroy) {
    destroyType = DestroyType.OTHERS;

    this.onDestroy = onDestroy;
  }

  public DestroyableComponent(DestroyType destroyType) {
    this.destroyType = destroyType;
  }

  boolean destroyed = false;

  public void destroy() {
    if (destroyed) return;

    destroyed = true;

    if (onDestroyBegin != null) onDestroyBegin.run(entity);

    switch (destroyType) {
      case FADE_OUT -> {
        if (!entity.hasComponent(EffectComponent.class))
          entity.addComponent(new EffectComponent());
        var effectComponent = entity.getComponent(EffectComponent.class);
        if (!effectComponent.hasEffect(FadeOutEffect.class))
          effectComponent.startEffect(new FadeOutEffect(Duration.seconds(1.0)));
      }
      case GROUND_EXPLOSION -> {
        stopEntity();
        var img = FXGL.image("explosions/ground.png", 5700, 250);
        var exploChannel = new AnimationChannel(img, Duration.seconds(1), 19);
        var exploTexture = new AnimatedTexture(exploChannel).play();
        exploTexture.setOnCycleFinished(() -> entity.removeFromWorld());
        entity.getViewComponent().clearChildren();
        exploTexture.setTranslateY(entity.getHeight() - 232.5);
        exploTexture.setTranslateX(entity.getWidth() / 2 - 150);
        entity.getViewComponent().addChild(exploTexture);
        exploTexture.play();
      }
      case AIR_EXPLOSION -> {
        stopEntity();
        var img = FXGL.image("explosions/air.png", 3780, 270);
        var exploChannel = new AnimationChannel(img, Duration.seconds(0.5), 14);
        var exploTexture = new AnimatedTexture(exploChannel);
        exploTexture.setOnCycleFinished(() -> entity.removeFromWorld());
        entity.getViewComponent().clearChildren();
        exploTexture.setTranslateY(entity.getHeight() / 2 - 135);
        exploTexture.setTranslateX(entity.getWidth() / 2 - 135);
        entity.getViewComponent().addChild(exploTexture);
        entity.setRotation(0);
        entity.setScaleX(1);
        entity.setScaleY(1);
        exploTexture.play();
      }
      case SHELL_EXPLOSION -> {
        entity.getComponentOptional(ProjectileComponent.class).ifPresent(projectile -> {
          projectile.setDirection(new Point2D(1, 0));
          projectile.setSpeed(0);
        });
        entity.getComponentOptional(ParabolicComponent.class).ifPresent(ParabolicComponent::pause);
        var img = FXGL.image("explosions/shell.png", 1800, 220);
        var exploChannel = new AnimationChannel(img, Duration.seconds(0.5), 9);
        var exploTexture = new AnimatedTexture(exploChannel);
        exploTexture.setOnCycleFinished(() -> entity.removeFromWorld());
        entity.getViewComponent().clearChildren();
        exploTexture.setTranslateY(entity.getHeight() - 175);
        exploTexture.setTranslateX(-entity.getWidth() / 2);
        entity.getViewComponent().addChild(exploTexture);
        entity.setRotation(0);
        entity.setScaleX(1);
        entity.setScaleY(1);
        exploTexture.play();
      }
      case FIRE_BALL_EXPLOSION -> {
        entity.getComponentOptional(ProjectileComponent.class).ifPresent(projectile -> {
          projectile.setDirection(new Point2D(1, 0));
          projectile.setSpeed(0);
        });
        entity.getComponentOptional(ParticleComponent.class).ifPresent(ParticleComponent::pauseEmitter);
        var img = FXGL.image("explosions/shell.png", 1800, 220);
        var exploChannel = new AnimationChannel(img, Duration.seconds(.5), 9);
        var exploTexture = new AnimatedTexture(exploChannel);
        exploTexture.setOnCycleFinished(() -> {
          exploTexture.setVisible(false);
          entity.getBoundingBoxComponent().removeHitBox("fire ball");
          FXGL.runOnce(() -> entity.removeFromWorld(), Duration.seconds(1));
        });
//          entity.getViewComponent().clearChildren();
        exploTexture.setTranslateY(-140);
        exploTexture.setTranslateX(-100);
        entity.getViewComponent().addChild(exploTexture);
        exploTexture.play();
      }
      case MASSIVE_AIR_EXPLOSIONS -> {
        var width = entity.getWidth();
        var height = entity.getHeight();

        var timeline = new Timeline(new KeyFrame(Duration.seconds(.5),
          new KeyValue(entity.opacityProperty(), 0)));
        timeline.setOnFinished((e) -> entity.removeFromWorld());
        FXGL.runOnce(timeline::play, Duration.seconds(.5));

        var img = FXGL.image("explosions/air.png", 3780, 270);
        var exploChannel = new AnimationChannel(img, Duration.seconds(0.5), 14);

        for (int i = 0; i < Math.max(entity.getWidth() * entity.getHeight() / 8000, 13); i++) {
          FXGL.runOnce(() -> {
            var exploTexture = new AnimatedTexture(exploChannel);
            exploTexture.setTranslateX(-135 + width * FXGLMath.randomDouble());
            exploTexture.setTranslateY(-135 + height * FXGLMath.randomDouble());
            entity.getViewComponent().addChild(exploTexture);
            exploTexture.play();
            exploTexture.setOnCycleFinished(() ->
              Platform.runLater(() -> entity.getViewComponent().removeChild(exploTexture)));
          }, Duration.seconds(.4 * FXGLMath.randomDouble()));
        }

      }
      case OTHERS -> onDestroy.run(entity);
    }
  }

  private void stopEntity() {
    List.of(MovingForwardComponent.class, MovingAlwaysForwardComponent.class, SimpleMovingForwardComponent.class, FloatingComponent.class)
            .forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(Component::pause));
  }

  public DestroyableComponent setOnDestroyBegin(EntityRunnable onDestroyBegin) {
    this.onDestroyBegin = onDestroyBegin;
    return this;
  }
}
