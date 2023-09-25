package com.example.fxcitydemo.xgamescenes;

import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.example.fxcitydemo.GameApp;
import com.example.fxcitydemo.gameworld.*;
import com.example.fxcitydemo.gameworld.entity.Launcher;
import com.example.fxcitydemo.gameworld.entity.characters.Clancy;
import com.example.fxcitydemo.gameworld.entity.characters.ClancyComponent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.whitewoodcity.flame.SVG;
import com.whitewoodcity.fxgl.service.SaveService;
import com.whitewoodcity.fxgl.service.XInput;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;

import java.util.List;

public class Setting extends MenuScene implements SaveService, SpawnService, FrontlineService {
  public static final String SCENE_NAME = "Setting";

  private GameWorld gameWorld;
  private Entity player, launcher;

  @Override
  public void initGame(GameWorld gameWorld, XInput input) {
    this.gameWorld = gameWorld;
  }

  @Override
  public void initUI(GameScene gameScene, XInput input) {

    var os = new Label(System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
    var vm = new Label(System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.name"));
    var java = new Label("Java: " + System.getProperty("java.version"));
    var javafx = new Label("JavaFX: " + System.getProperty("javafx.runtime.version"));
    var fxgl = new Label("FXGL: " + FXGL.getVersion());

    os.setFont(FXGL.getUIFactoryService().newFont(20));
    vm.fontProperty().bind(os.fontProperty());
    java.fontProperty().bind(os.fontProperty());
    javafx.fontProperty().bind(os.fontProperty());
    fxgl.fontProperty().bind(os.fontProperty());

    var vol = new Button();
    var svg = SVG.newSVG("M11.5,2.75 C11.5,2.22634895 12.0230228,1.86388952 12.5133347,2.04775015 L18.8913911,4.43943933 C20.1598961,4.91511241 21.0002742,6.1277638 21.0002742,7.48252202 L21.0002742,10.7513533 C21.0002742,11.2750044 20.4772513,11.6374638 19.9869395,11.4536032 L13,8.83332147 L13,17.5 C13,17.5545945 12.9941667,17.6078265 12.9830895,17.6591069 C12.9940859,17.7709636 13,17.884807 13,18 C13,20.2596863 10.7242052,22 8,22 C5.27579485,22 3,20.2596863 3,18 C3,15.7403137 5.27579485,14 8,14 C9.3521238,14 10.5937815,14.428727 11.5015337,15.1368931 L11.5,2.75 Z M8,15.5 C6.02978478,15.5 4.5,16.6698354 4.5,18 C4.5,19.3301646 6.02978478,20.5 8,20.5 C9.97021522,20.5 11.5,19.3301646 11.5,18 C11.5,16.6698354 9.97021522,15.5 8,15.5 Z M13,3.83223733 L13,7.23159672 L19.5002742,9.669116 L19.5002742,7.48252202 C19.5002742,6.75303682 19.0477629,6.10007069 18.3647217,5.84393903 L13,3.83223733 Z",
      os.getFont().getSize(), os.getFont().getSize());
    vol.setGraphic(svg);

    var musicVolSlider = new Slider();
    musicVolSlider.setMin(0);
    musicVolSlider.setMax(1);
    musicVolSlider.setValue(FXGL.getSettings().getGlobalMusicVolume());
    musicVolSlider.setShowTickLabels(true);
    musicVolSlider.setShowTickMarks(true);
    musicVolSlider.setMajorTickUnit(0.5);
    musicVolSlider.setMinorTickCount(5);
    musicVolSlider.setBlockIncrement(0.01);

    vol.setOnAction(event -> {
//      FXGL.getAudioPlayer().stopAllMusic();
      FXGL.getSettings().setGlobalMusicVolume(musicVolSlider.getValue());
//      FXGL.play("battle0.mp3");
    });

    var hBox0 = new HBox();
    hBox0.setSpacing(10);
    hBox0.getChildren().addAll(vol, musicVolSlider);

    musicVolSlider.styleProperty().bind(os.fontProperty().map(f -> "-fx-font-size: " + f.getSize()));
    vol.fontProperty().bind(os.fontProperty());

    vol = new Button();
    svg = SVG.newSVG("M308.971 657.987l150.28 165.279a16 16 0 0 0 11.838 5.236c8.837 0 16-7.163 16-16v-600.67a16 16 0 0 0-5.236-11.839c-6.538-5.944-16.657-5.463-22.602 1.075l-150.28 165.279A112 112 0 0 1 226.105 403H177c-17.673 0-32 14.327-32 32v154.333c0 17.674 14.327 32 32 32h49.105a112 112 0 0 1 82.866 36.654zM177 701.333c-61.856 0-112-50.144-112-112V435c0-61.856 50.144-112 112-112h49.105a32 32 0 0 0 23.676-10.472l150.28-165.28c35.668-39.227 96.383-42.113 135.61-6.445a96 96 0 0 1 31.418 71.028v600.671c0 53.02-42.98 96-96 96a96 96 0 0 1-71.029-31.417l-150.28-165.28a32 32 0 0 0-23.675-10.472H177z m456.058-348.336c-18.47-12.118-23.621-36.915-11.503-55.386 12.118-18.471 36.916-23.621 55.387-11.503C752.495 335.675 799 419.908 799 512c0 92.093-46.505 176.325-122.058 225.892-18.471 12.118-43.269 6.968-55.387-11.503-12.118-18.471-6.968-43.268 11.503-55.386C686.303 636.07 719 576.848 719 512c0-64.848-32.697-124.07-85.942-159.003z m92.93-137.323c-18.07-12.71-22.415-37.66-9.706-55.73s37.66-22.415 55.73-9.706C888.942 232.478 960 366.298 960 512s-71.058 279.522-187.988 361.762c-18.07 12.71-43.021 8.364-55.73-9.706-12.709-18.07-8.363-43.02 9.706-55.73C821.838 740.912 880 631.38 880 512c0-119.38-58.161-228.912-154.012-296.326z",
      37.33 * os.getFont().getSize() / 33.07, os.getFont().getSize());
    vol.setGraphic(svg);

    var soundVolSlider = new Slider();
    soundVolSlider.setMin(0);
    soundVolSlider.setMax(1);
    soundVolSlider.setValue(FXGL.getSettings().getGlobalSoundVolume());
    soundVolSlider.setShowTickLabels(true);
    soundVolSlider.setShowTickMarks(true);
    soundVolSlider.setMajorTickUnit(0.5);
    soundVolSlider.setMinorTickCount(5);
    soundVolSlider.setBlockIncrement(0.01);

    vol.setOnAction(event -> {
      FXGL.getSettings().setGlobalSoundVolume(soundVolSlider.getValue());
      FXGL.play("finger.wav");
    });

    soundVolSlider.styleProperty().bind(os.fontProperty().map(f -> "-fx-font-size: " + f.getSize()));
    vol.fontProperty().bind(os.fontProperty());

    var polyline = new Polyline();
    polyline.getPoints().addAll(4.0, 13.0, 9.0, 18.0, 20.0, 7.0);
    polyline.setStrokeLineCap(StrokeLineCap.ROUND);
    polyline.setStrokeLineJoin(StrokeLineJoin.ROUND);
    polyline.setStrokeWidth(2);
    polyline.setStyle("-fx-stroke:-color-fg-default;");
    var pane = new StackPane(polyline);
    pane.minWidthProperty().bind(pane.prefWidthProperty());
    pane.maxWidthProperty().bind(pane.prefWidthProperty());
    pane.minHeightProperty().bind(pane.prefHeightProperty());
    pane.maxHeightProperty().bind(pane.prefHeightProperty());
    pane.prefHeightProperty().bind(os.fontProperty().map(Font::getSize));
    pane.prefWidthProperty().bind(pane.prefHeightProperty().map(h -> 17.414 / 12.414 * h.doubleValue()));

    var hBox1 = new HBox();
    hBox1.setSpacing(10);
    hBox1.getChildren().addAll(vol, soundVolSlider);

    player = ClancyComponent.of(new SpawnData(200, 800).put(PropertyKey.CLANCY, new Clancy()));
    launcher = Launcher.of(new SpawnData(1000,800));

    gameWorld.addEntities(
      Borders.of(FXGL.getAppWidth(), FXGL.getAppHeight()),
      player, launcher);

    var w = new Button("W");
    var a = new Button("A");
    var s = new Button("S");
    var d = new Button("D");

    w.setOnMousePressed(_1 -> input.mockKeyPress(KeyCode.W));
    w.setOnMouseReleased(_1 -> input.mockKeyRelease(KeyCode.W));

    a.setOnMousePressed(_1 -> input.mockKeyPress(KeyCode.A));
    a.setOnMouseReleased(_1 -> input.mockKeyRelease(KeyCode.A));

    s.setOnMousePressed(_1 -> input.mockKeyPress(KeyCode.S));
    s.setOnMouseReleased(_1 -> input.mockKeyRelease(KeyCode.S));
    s.prefWidthProperty().bind(w.widthProperty());
    s.setDisable(true);

    d.setOnMousePressed(_1 -> input.mockKeyPress(KeyCode.D));
    d.setOnMouseReleased(_1 -> input.mockKeyRelease(KeyCode.D));

    w.fontProperty().bind(os.fontProperty());
    a.fontProperty().bind(os.fontProperty());
    s.fontProperty().bind(os.fontProperty());
    d.fontProperty().bind(os.fontProperty());

    var ws = new VBox();
    ws.setSpacing(3);
    ws.setAlignment(Pos.CENTER);
    ws.getChildren().addAll(w, s);

    var j = new Button("J");
    j.setOnMousePressed(_1 -> input.mockKeyPress(KeyCode.J));
    j.setOnMouseReleased(_1 -> input.mockKeyRelease(KeyCode.J));

    var k = new Button("K");
    k.setOnMousePressed(_1 -> input.mockKeyPress(KeyCode.K));
    k.setOnMouseReleased(_1 -> input.mockKeyRelease(KeyCode.K));

    var l = new Button("L");
    l.setOnMousePressed(_1 -> input.mockKeyPress(KeyCode.L));
    l.setOnMouseReleased(_1 -> input.mockKeyRelease(KeyCode.L));

    j.fontProperty().bind(os.fontProperty());
    k.fontProperty().bind(os.fontProperty());
    l.fontProperty().bind(os.fontProperty());

    var shoot = new HBox();
    shoot.spacingProperty().bind(ws.spacingProperty());
    shoot.getChildren().addAll(j, k, l);

    var space = new Button("Space");
    space.setOnMousePressed(_1 -> input.mockKeyPress(KeyCode.SPACE));
    space.setOnMouseReleased(_1 -> input.mockKeyRelease(KeyCode.SPACE));

    space.fontProperty().bind(os.fontProperty());

    var shootAndSpace = new VBox();
    shootAndSpace.spacingProperty().bind(ws.spacingProperty());
    shootAndSpace.setAlignment(Pos.CENTER_LEFT);
    shootAndSpace.getChildren().addAll(shoot, space);

    var hBox2 = new HBox();
    hBox2.setSpacing(10);
    hBox2.setAlignment(Pos.BOTTOM_LEFT);
    hBox2.getChildren().addAll(a, ws, d, new Region(), shootAndSpace);

    var wd = new Button("W + D");
    wd.setOnMousePressed(_1 -> {
      input.mockKeyPress(KeyCode.W);
      input.mockKeyPress(KeyCode.D);
    });
    wd.setOnMouseReleased(_1 -> {
      input.mockKeyRelease(KeyCode.W);
      input.mockKeyRelease(KeyCode.D);
    });

    var wda = new Button("A + W + D");
    wda.setOnMousePressed(_1 -> {
      input.mockKeyPress(KeyCode.W);
      input.mockKeyPress(KeyCode.D);
      input.mockKeyPress(KeyCode.A);
    });
    wda.setOnMouseReleased(_1 -> {
      input.mockKeyRelease(KeyCode.W);
      input.mockKeyRelease(KeyCode.D);
      input.mockKeyRelease(KeyCode.A);
    });

    wd.fontProperty().bind(os.fontProperty());
    wda.fontProperty().bind(os.fontProperty());

    var hBox4 = new HBox();
    hBox4.setSpacing(10);
    hBox4.getChildren().addAll(wd, wda);

    Button bt = new Button();
    bt.setGraphic(pane);
    bt.setOnAction(_1 -> {
      FXGL.getSettings().setGlobalSoundVolume(soundVolSlider.getValue());
      FXGL.getSettings().setGlobalMusicVolume(musicVolSlider.getValue());

      FXGL.play("finger.wav");
//      FXGL.getAudioPlayer().stopAllMusic();
      FXGL.<GameApp>getAppCast().pop();

      ObjectMapper mapper = new ObjectMapper();
      ObjectNode savedConfig = mapper.createObjectNode();
      savedConfig.put("musicVolume", musicVolSlider.getValue());
      savedConfig.put("soundVolume", soundVolSlider.getValue());
      saveFile("conf", "xtrike.config.json", savedConfig);
    });

    bt.fontProperty().bind(os.fontProperty());

    var vbox = new VBox();
    vbox.setPadding(new Insets(10));
    vbox.spacingProperty().bind(hBox0.spacingProperty());
    vbox.getChildren().addAll(os, vm, java, javafx, fxgl, hBox0, hBox1, hBox2, hBox4, bt);

    input.onAction(KeyCode.ENTER, bt::fire);
//    FXGL.onKeyBuilder(input, KeyCode.ENTER).onActionBegin(bt::fire);

    List.of(KeyCode.D, KeyCode.RIGHT)
      .forEach(keyCode -> input
        .onAction(keyCode, player.getComponent(ClancyComponent.class)::moveRight)
        .onActionEnd(keyCode, player.getComponent(ClancyComponent.class)::stopMovingRight));

    List.of(KeyCode.D, KeyCode.RIGHT)
      .forEach(keyCode -> input
        .onAction(keyCode, player.getComponent(ClancyComponent.class)::moveRight)
        .onActionEnd(keyCode, player.getComponent(ClancyComponent.class)::stopMovingRight));

    List.of(KeyCode.A, KeyCode.LEFT)
      .forEach(keyCode -> input
        .onAction(keyCode, player.getComponent(ClancyComponent.class)::moveLeft)
        .onActionEnd(keyCode, player.getComponent(ClancyComponent.class)::stopMovingLeft));

    List.of(KeyCode.W, KeyCode.UP)
      .forEach(keyCode -> input
        .onAction(keyCode, player.getComponent(ClancyComponent.class)::upward)
        .onActionEnd(keyCode, player.getComponent(ClancyComponent.class)::stopUpward));

//    FXGL.onKeyBuilder(input, KeyCode.SPACE).onActionBegin(player.getComponent(ClancyComponent.class)::jump);

    input.onAction(KeyCode.SPACE, player.getComponent(ClancyComponent.class)::jump);

    List.of(KeyCode.J, KeyCode.K, KeyCode.L, KeyCode.U, KeyCode.I, KeyCode.O, KeyCode.Y, KeyCode.H, KeyCode.N, KeyCode.M, KeyCode.P)
      .forEach(keyCode -> input
        .onAction(keyCode, player.getComponent(ClancyComponent.class)::fire)
        .onActionEnd(keyCode, player.getComponent(ClancyComponent.class)::stopFire));

    setFocusTraversableToFalse(vbox);

    gameScene.addUINodes(vbox);
  }

  private void setFocusTraversableToFalse(Node node) {
    if (node instanceof Pane pane) {
      for (Node child : pane.getChildren())
        setFocusTraversableToFalse(child);
    } else
      node.setFocusTraversable(false);
  }

  @Override
  public Entity spawn(Type type, SpawnData data) {
    var entity = build(type, data);
    gameWorld.addEntity(entity);
    return entity;
  }

  @Override
  public double getPlayerX() {
    return player.getX();
  }

  @Override
  public double getPlayerY() {
    return player.getY();
  }

  @Override
  public double getEnemyX() {
    return launcher.getX();
  }

  @Override
  public double getEnemyY() {
    return launcher.getY();
  }
}
