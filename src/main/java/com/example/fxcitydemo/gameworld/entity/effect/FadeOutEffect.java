package com.example.fxcitydemo.gameworld.entity.effect;

import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;

public class FadeOutEffect extends Effect {

  private final double duration;
  private double opacity = 1.0;

  public FadeOutEffect(Duration duration) {
    super(duration);

    this.duration = duration.toSeconds();
  }

  @Override
  public void onEnd(Entity entity) {
    entity.setOpacity(0);
    entity.removeFromWorld();
  }

  @Override
  public void onUpdate(Entity entity, double tpf) {
    opacity = opacity - tpf/duration;
    entity.setOpacity(opacity);
  }

  @Override
  public void onStart(Entity entity) {
    entity.setOpacity(1);
  }
}
