package com.example.fxcitydemo.xgamescenes.control;

import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.ui.ProgressBar;
import com.example.fxcitydemo.gameworld.entity.characters.ClancyComponent;
import com.example.fxcitydemo.gameworld.entity.characters.ClancyComponent2;
import com.example.fxcitydemo.gameworld.entity.characters.ClancyCommands;
import com.whitewoodcity.fxgl.service.XInput;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.List;

@SuppressWarnings("unused")
public interface ClancyGame {

  default void initClancyUI(Entity clancy, GameScene gameScene) {
    //show hp bar
    var hpBar = new ProgressBar();
    hpBar.setHeight(25);
    hpBar.setFill(Color.LIGHTGREEN);
    hpBar.setTraceFill(Color.TRANSPARENT);
    hpBar.setLayoutX(50);
    hpBar.setLayoutY(50);
    hpBar.setBackgroundFill(Color.rgb(0, 0, 0, 0.5));

    hpBar.maxValueProperty().bind(clancy.getComponent(HealthIntComponent.class).maxValueProperty());
    hpBar.currentValueProperty().bind(clancy.getComponent(HealthIntComponent.class).valueProperty());
    gameScene.addUINode(hpBar);
  }

  default void initClancyUI(GameWorld gameWorld, GameScene gameScene) {
    gameWorld.getEntityByID("Clancy", 0).ifPresent(clancy -> initClancyUI(clancy, gameScene));
  }

  default void initClancyInput(Entity entity, XInput input) {
    var clancyComponentClasses = List.of(ClancyComponent.class, ClancyComponent2.class);
    List.of(KeyCode.D, KeyCode.RIGHT).forEach(keyCode -> {
      input
        .onAction(keyCode, () -> clancyComponentClasses.forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(ClancyCommands::moveRight)))
        .onActionEnd(keyCode, () -> clancyComponentClasses.forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(ClancyCommands::stopMovingRight)));
    });

    List.of(KeyCode.A, KeyCode.LEFT)
      .forEach(keyCode -> {
        input
          .onAction(keyCode, () -> clancyComponentClasses.forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(ClancyCommands::moveLeft)))
          .onActionEnd(keyCode, () -> clancyComponentClasses.forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(ClancyCommands::stopMovingLeft)));
      });

    List.of(KeyCode.W, KeyCode.UP)
      .forEach(keyCode -> {
        input
          .onAction(keyCode, () -> clancyComponentClasses.forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(ClancyCommands::upward)))
          .onActionEnd(keyCode, () -> clancyComponentClasses.forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(ClancyCommands::stopUpward)));
      });

    List.of(KeyCode.S, KeyCode.DOWN)
      .forEach(keyCode -> {
        input
          .onAction(keyCode, () -> clancyComponentClasses.forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(ClancyCommands::downward)))
          .onActionEnd(keyCode, () -> clancyComponentClasses.forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(ClancyCommands::stopDownward)));
      });

    input.onAction(KeyCode.SPACE, () -> clancyComponentClasses.forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(ClancyCommands::jump)));

    List.of(KeyCode.ENTER, KeyCode.J, KeyCode.K, KeyCode.L, KeyCode.U, KeyCode.I, KeyCode.O, KeyCode.Y, KeyCode.H, KeyCode.N, KeyCode.M, KeyCode.P)
      .forEach(keyCode -> {
        input
          .onAction(keyCode, () -> clancyComponentClasses.forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(ClancyCommands::fire)))
          .onActionEnd(keyCode, () -> clancyComponentClasses.forEach(clazz -> entity.getComponentOptional(clazz).ifPresent(ClancyCommands::stopFire)));
      });
  }

  default void initClancyInput(GameWorld gameWorld, XInput input) {
    gameWorld.getEntityByID("Clancy", 0).ifPresent(entity -> {
      initClancyInput(entity, input);
    });
  }
}
