package com.example.fxcitydemo.gameworld;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.GameWorld;
import com.example.fxcitydemo.gameworld.entity.components.Camp;
import com.example.fxcitydemo.gameworld.entity.components.CampComponent;
import com.whitewoodcity.fxgl.service.DimensionService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public interface FrontlineService extends DimensionService {

  enum Position{
    PLAYER_X, PLAYER_Y, ENEMY_X, ENEMY_Y
  }

  Map<Position, Double> map = new HashMap<>(){{
    put(Position.PLAYER_X, .0);
    put(Position.PLAYER_Y, .0);
    put(Position.ENEMY_X, .0);
    put(Position.ENEMY_Y, .0);
  }};

  default double getPlayerX() {
//    var entity = FXGL.getGameWorld().getEntityByID("Clancy", 0);
//    if(entity.isPresent()){
//      var clancy = entity.get();
//      return clancy.getX() + clancy.getWidth() / 2;
//    } else return 0;
    return map.get(Position.PLAYER_X);
  }

  default double getPlayerY() {
//    var entity = FXGL.getGameWorld().getEntityByID("Clancy", 0);
//    if(entity.isPresent()){
//      var clancy = entity.get();
//      return clancy.getY() + clancy.getHeight() / 2;
//    } else return 500;
    return map.get(Position.PLAYER_Y);
  }

  default double getEnemyX(){
//    return getGameWidth() - 10;
    return map.get(Position.ENEMY_X);
  }

  default double getEnemyY(){
//    return 0;
    return map.get(Position.ENEMY_Y);
  }

  private void updateFrontLine(GameWorld world){
    var entities = world.getEntities();
    for (var entity : entities) {
      entity.getComponentOptional(CampComponent.class).ifPresent(campComponent -> {
        if (campComponent.isTargetable()) {
          if (campComponent.camp() == Camp.ALLIANCE) {
            if (entity.getX() > getPlayerX()) {
              map.put(Position.PLAYER_X, entity.getX());
              map.put(Position.PLAYER_Y, entity.getY());
            }
          } else {
            if (entity.getX() < getEnemyX()) {
              map.put(Position.ENEMY_X, entity.getX());
              map.put(Position.ENEMY_Y, entity.getY());
            }
          }
        }
      });
    }
  }

  default void updateFrontLine(){
    initFrontLine();
    updateFrontLine(FXGL.getGameWorld());
  }

  default void updateFrontLine(GameWorld... gameWorlds){
    initFrontLine();
    Arrays.stream(gameWorlds).forEach(this::updateFrontLine);
  }

  default void initFrontLine(){
    map.put(Position.PLAYER_X, .0);
    map.put(Position.PLAYER_Y, .0);
    map.put(Position.ENEMY_X, getGameWidth() - 10.0);
    map.put(Position.ENEMY_Y, .0);
  }
}
