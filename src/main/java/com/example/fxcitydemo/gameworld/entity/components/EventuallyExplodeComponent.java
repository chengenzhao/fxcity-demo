package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.dsl.components.HealthDoubleComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class EventuallyExplodeComponent extends Component {
  DestroyableComponent destroy;

  int lifeSpan;

  public EventuallyExplodeComponent(int lifeSpan) {
    this.lifeSpan = lifeSpan;
  }

  @Override
  public void onUpdate(double tpf) {
    lifeSpan--;

    if(lifeSpan<=0){
      entity.getComponentOptional(ProjectileComponent.class).ifPresent(projectileComponent -> {
        projectileComponent.setDirection(new Point2D(1,0));
        projectileComponent.setSpeed(0);
      });
      entity.getComponentOptional(ParabolicComponent.class).ifPresent(ParabolicComponent::pause);

      destroy.destroy();

      entity.getComponentOptional(HealthIntComponent.class).ifPresent(HealthIntComponent::damageFully);
      entity.getComponentOptional(HealthDoubleComponent.class).ifPresent(HealthDoubleComponent::damageFully);
    }
  }
}
