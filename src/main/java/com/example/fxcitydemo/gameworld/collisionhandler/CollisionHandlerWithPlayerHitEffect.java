package com.example.fxcitydemo.gameworld.collisionhandler;

import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.example.fxcitydemo.gameworld.Type;
import com.example.fxcitydemo.gameworld.entity.components.CampComponent;
import com.example.fxcitydemo.gameworld.entity.components.ZComponent;
import com.whitewoodcity.fxgl.app.scene.ConcurrentGameSubScene;
import javafx.animation.FadeTransition;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class CollisionHandlerWithPlayerHitEffect extends CollisionHandler {

  public CollisionHandlerWithPlayerHitEffect(Object a, Object b) {
    super(a, b);
  }

  @Override
  protected void onCollisionBegin(Entity entity, Entity ammo) {
    super.onCollisionBegin(entity, ammo);
    if (fromSameCamp(entity, ammo) || notOnTheSameLine(entity, ammo)) return;

    if (entity.getType() == Type.CLANCY) {
      RadialGradient gradient = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.TRANSPARENT),
        new Stop(1, Color.RED));

      var rect = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), gradient);

      var currentScene = FXGL.getSceneService().getCurrentScene();
      if(currentScene.isSubState() && currentScene instanceof ConcurrentGameSubScene gameSubScene){
        showRect(gameSubScene.getGameScene(), rect, ammo.getType().toString());
      }else
        showRect(FXGL.getGameScene(), rect, ammo.getType().toString());
    }
  }

  double opacity = 0;

  private void showRect(GameScene gameScene, Rectangle rect, String id){
    var previousRects = gameScene.getContentRoot().lookupAll("#"+id);
    opacity = 0;
    previousRects.forEach(n -> opacity += n.getOpacity());
    if(opacity>=1) return;

    rect.setId(id);
    gameScene.addUINode(rect);

    FadeTransition ft = new FadeTransition(Duration.millis(500), rect);
    ft.setFromValue(1.0);
    ft.setToValue(0.0);

    ft.setOnFinished(_1 -> gameScene.removeUINodes(rect));

    ft.play();
  }

  protected boolean fromSameCamp(Entity entity0, Entity entity1) {
    var entity0Camp = entity0.getComponentOptional(CampComponent.class);
    var entity1Camp = entity1.getComponentOptional(CampComponent.class);
    if (entity0Camp.isPresent() && entity1Camp.isPresent()) {
      return entity0Camp.get().camp() == entity1Camp.get().camp();
    } else return false;
  }

  protected boolean notOnTheSameLine(Entity entity0, Entity entity1){
    if(!entity0.hasComponent(ZComponent.class) || !entity1.hasComponent(ZComponent.class)) return false;

    var z0 = entity0.getComponent(ZComponent.class);
    var z1 = entity1.getComponent(ZComponent.class);

    return FXGLMath.abs(z0.getZ() - z1.getZ()) > FXGLMath.abs(z0.getTranslateZ() + z1.getTranslateZ());
  }
}
