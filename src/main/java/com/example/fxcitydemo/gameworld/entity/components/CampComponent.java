package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.entity.component.Component;

public class CampComponent extends Component {
  private final Camp camp;
  private final boolean targetable;

  public CampComponent(Camp camp) {
    this(camp, false);
  }

  public CampComponent(Camp camp, boolean targetable) {
    this.camp = camp;
    this.targetable = targetable;
  }

  public Camp camp() {
    return camp;
  }

  public boolean isTargetable() {
    return targetable;
  }
}
