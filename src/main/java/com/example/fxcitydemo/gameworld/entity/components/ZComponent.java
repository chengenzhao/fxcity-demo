
package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class ZComponent extends Component {
  private final Entity entity;
  private final double z;

  public ZComponent(Entity entity, double translateZ) {
    this.entity = entity;
    z = translateZ;
  }

  public ZComponent(Double z) {
    this.z = z;
    this.entity = null;
  }

  public double getZ(){
    return entity == null ? z:entity.getY() + z;
  }

  public double getTranslateZ(){
    return entity == null ? 0:z;
  }
}
