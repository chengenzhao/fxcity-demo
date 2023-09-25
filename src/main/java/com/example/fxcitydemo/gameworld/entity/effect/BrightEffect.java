package com.example.fxcitydemo.gameworld.entity.effect;

import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.entity.Entity;
import com.example.fxcitydemo.gameworld.entity.components.EffectSelectableComponent;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;

public class BrightEffect extends Effect {
  ColorAdjust colorAdjust = new ColorAdjust();

  public BrightEffect(Duration duration) {
    super(duration);
    colorAdjust.setBrightness(0.7);
  }

  @Override
  public void onStart(Entity entity) {
    entity.getComponentOptional(EffectSelectableComponent.class)
      .ifPresentOrElse(effectSelectableComponent -> {
        for (var comp : entity.getViewComponent().getChildren()) {
          if (effectSelectableComponent.effectAppliable(comp))
            comp.setEffect(colorAdjust);
        }
      }, () -> {
        for (var comp : entity.getViewComponent().getChildren()) {
          comp.setEffect(colorAdjust);
        }
      });
  }

  @Override
  public void onEnd(Entity entity) {
    entity.getComponentOptional(EffectSelectableComponent.class)
      .ifPresentOrElse(effectSelectableComponent -> {
          for (var comp : entity.getViewComponent().getChildren()) {
            if (effectSelectableComponent.effectAppliable(comp))
              comp.setEffect(null);
          }
        }
        , () -> {
          for (var comp : entity.getViewComponent().getChildren()) {
            comp.setEffect(null);
          }
        });
  }
}
