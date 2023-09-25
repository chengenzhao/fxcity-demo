package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.entity.Entity;

@FunctionalInterface
public interface EntityRunnable {
  void run(Entity entity);
}
