package com.example.fxcitydemo.xgamescenes;

import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.fxgl.service.PushAndPopGameSubScene;
import com.whitewoodcity.fxgl.service.component.AsyncLabel;
import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

public class MenuScene implements PushAndPopGameSubScene {
  @Override
  public void setGameScene(GameScene gameScene) {
    gameScene.getRoot().setStyle("-fx-background-color: -color-bg-default;");

    var paint = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
      new Stop(0, Color.web("010425")), new Stop(0.5, Color.web("3a74a6")), new Stop(1, Color.web("010425")));
    var rect = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), paint);

    var label = new AsyncLabel("Xtrike");
    label.setFont(FXGL.getAssetLoader().loadFont("BlackOpsOne-Regular.ttf").newFont(200));
    label.setTextFill(Color.WHITE);

    label.translateXProperty().bind(label.layoutBoundsProperty().map(layout -> FXGL.getAppCenter().getX() - layout.getWidth() / 2));
    label.translateYProperty().bind(label.layoutBoundsProperty().map(layout -> FXGL.getAppCenter().getY() - layout.getHeight() / 2));

    label.setEffect(new Bloom());

    rect.setOpacity(.5);
    label.setOpacity(.5);

    gameScene.getContentRoot().getChildren().add(0, label);
    gameScene.getContentRoot().getChildren().add(0, rect);
  }
}
