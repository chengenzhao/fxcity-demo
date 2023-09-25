package com.example.fxcitydemo;

import com.whitewoodcity.atlantafx.base.theme.CityDark;
import com.whitewoodcity.fxgl.service.FillService;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class Application extends javafx.application.Application implements FillService {
  @Override
  public void start(Stage stage) {
    javafx.application.Application.setUserAgentStylesheet(new CityDark().getUserAgentStylesheet());

    stage.setWidth(Screen.getPrimary().getBounds().getWidth() * .75);
    stage.setHeight(Screen.getPrimary().getBounds().getHeight() * .75);

    var stackpane = new StackPane();
    stage.setScene(new Scene(stackpane));

    stage.setFullScreen(true);
    stage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.ESCAPE, KeyCombination.SHORTCUT_DOWN));

    stage.show();

    var gamePane = GameApp.embeddedLaunch(new GameApp(Index.SCENE_NAME));
    gamePane.setStyle("-fx-background-color: -color-bg-default;");
    gamePane.setRenderFill(Color.TRANSPARENT);
    stackpane.getChildren().add(gamePane);

    gamePane.prefWidthProperty().bind(stage.getScene().widthProperty());
    gamePane.prefHeightProperty().bind(stage.getScene().heightProperty());
    gamePane.renderWidthProperty().bind(stage.getScene().widthProperty());
    gamePane.renderHeightProperty().bind(stage.getScene().heightProperty());
  }

  public static void main(String... args) {
    System.setProperty("prism.lcdtext", "false");
    Application.launch(Application.class, args);
  }
}