package com.example.fxcitydemo.gameworld;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.example.fxcitydemo.gameworld.entity.Launcher;
import com.example.fxcitydemo.gameworld.entity.characters.ClancyComponent;

public interface SpawnService extends com.whitewoodcity.fxgl.service.SpawnService<Type> {
  @Override
  default Entity build(Type type, SpawnData data) {
    return switch (type) {
      case CLANCY -> ClancyComponent.of(data);
      case LAUNCHER -> Launcher.of(data);
      default -> throw new RuntimeException("Type "+type+" is not supported yet.");
    };
  }
}
