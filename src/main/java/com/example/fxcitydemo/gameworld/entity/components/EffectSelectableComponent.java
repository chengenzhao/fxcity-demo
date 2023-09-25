package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.Node;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EffectSelectableComponent extends Component {
  private final Set<Node> componentSet = new HashSet<>();

  public EffectSelectableComponent(Node... components) {
    Collections.addAll(componentSet, components);
  }

  public boolean effectAppliable(Node component){
    return componentSet.contains(component);
  }
}
