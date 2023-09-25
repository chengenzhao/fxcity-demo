package com.example.fxcitydemo.xgamescenes;

import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.app.scene.GameSubScene;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.particle.ParticleSystem;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.texture.Texture;
import com.example.fxcitydemo.GameApp;
import com.example.fxcitydemo.gameworld.FrontlineService;
import com.example.fxcitydemo.gameworld.SpawnService;
import com.example.fxcitydemo.gameworld.collisionhandler.BulletCollisionHandler;
import com.example.fxcitydemo.gameworld.collisionhandler.SmallExplodableCollisionHandler;
import com.example.fxcitydemo.gameworld.collisionhandler.TranslucentCollisionHandler;
import com.example.fxcitydemo.gameworld.entity.Launcher;
import com.example.fxcitydemo.gameworld.entity.accessories.Shadow;
import com.example.fxcitydemo.gameworld.entity.characters.Clancy;
import com.example.fxcitydemo.gameworld.entity.characters.ClancyComponent2;
import com.example.fxcitydemo.gameworld.entity.characters.ClancyViewComponent2;
import com.example.fxcitydemo.gameworld.entity.components.Camp;
import com.example.fxcitydemo.gameworld.entity.components.CampComponent;
import com.example.fxcitydemo.gameworld.entity.components.DestroyableComponent;
import com.example.fxcitydemo.xgamescenes.control.ClancyGame;
import com.whitewoodcity.fxgl.app.ImageData;
import com.whitewoodcity.fxgl.app.scene.ConcurrentGameSubScene;
import com.whitewoodcity.fxgl.dsl.FXGL;
import com.whitewoodcity.fxgl.service.*;
import com.whitewoodcity.fxgl.service.component.AsyncLabel;
import javafx.animation.Transition;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.ImagePattern;
import javafx.util.Duration;
import com.example.fxcitydemo.gameworld.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.example.fxcitydemo.gameworld.Type.*;

public class RogueLikeGame implements ReplaceableGameSceneWithConcurrentService, DimensionService, SpawnService, ClancyGame, ExitService, UpdateService, FrontlineService, ParallelCacheService {

  public static final String SCENE_NAME = "Rogue Like Game";

  public RogueLikeGame() {
    FXGL.set(PropertyKey.CLANCY, new Clancy());
  }

  private List<ConcurrentGameSubScene> concurrentGameSubScenes;

  @Override
  public List<GameSubScene> generateGameSubSceneList() {
    concurrentGameSubScenes = List.of(new ConcurrentGameSubScene(getGameWidth(), getGameHeight(), false));
    return new ArrayList<>(concurrentGameSubScenes);
  }

  Entity clancy;
  GameWorld materium, immaterium;
  private List<GameScene> gameScenes;
  Phase phase = Phase.BATTLE;

  @Override
  public void asyncLoadResources(AsyncLabel label) {
    FXGL.getAssetLoader().clearCache();
    label.syncText("10%");

    FXGL.getAssetLoader().loadTexture("backgrounds/mountain0.png");

    label.syncText("20%");

    List.of("maou_game_battle34.mp3")
      .parallelStream().forEach(FXGL.getAssetLoader()::loadMusic);

    label.syncText("50%");

    var cacheSet = cacheSet(GROUND, GROUND_FONT, CLANCY, BULLET, LAUNCHER_MISSILE, LAUNCHER);
    cacheSet.addAll(DestroyableComponent.getImageData());
    cacheSet.parallelStream().forEach(ImageData::image);

    label.syncText("100%");
  }

  @Override
  public void setGameScene(List<GameScene> gameScenes) {
    ReplaceableGameSceneWithConcurrentService.super.setGameScene(gameScenes);

    this.gameScenes = gameScenes;

    var firstScene = gameScenes.getFirst();
    var lastScene = gameScenes.getLast();

    var image = FXGL.image("backgrounds/mountain0.png");
    firstScene.setBackgroundColor(new ImagePattern(image, 0, -0.3, 1f, 1, true));

    var roadBackImage = FXGL.image("backgrounds/back0.png", 1500, 713 * .625);
    var entityBuilder = new EntityBuilder().at(0, 400);
    for (int i = 0; i < getGameWidth(); i += (int) roadBackImage.getWidth()) {
      var texture = new Texture(roadBackImage);
      texture.setX(i);
      entityBuilder.view(texture);
    }
    var roadBack = entityBuilder.zIndex(Integer.MIN_VALUE).build();
    firstScene.getGameWorld().addEntity(roadBack);

    var roadFontImage = FXGL.image("backgrounds/front0.png", 1500, 441 * .625);
    entityBuilder = new EntityBuilder().at(0, 1000 - 441 * .625);
    for (int i = 0; i < getGameWidth(); i += (int) roadFontImage.getWidth()) {
      var texture = new Texture(roadFontImage);
      texture.setX(i);
      entityBuilder.view(texture);
    }
    var roadFront = entityBuilder.zIndex(Integer.MAX_VALUE - 1).build();
    lastScene.getGameWorld().addEntity(roadFront);

    lastScene.getGameWorld().addEntity(new EntityBuilder().view(smokeParticleSystem.getPane()).zIndex(Integer.MAX_VALUE).build());
  }

  @Override
  public void initPhysics(List<PhysicsWorld> physicsWorlds, XInput input) {
    physicsWorlds.forEach(physicsWorld -> physicsWorld.setGravity(0, 0));

    var ammos = Type.getAmmos();
    var units = Type.getUnits();
    var airAmmos = Type.getAirAmmos();

    for (var ammo : ammos) {
      var handler = switch (ammo) {
        case BULLET, CHINOOK_BULLET, SPACE_MARINE_BULLET -> new BulletCollisionHandler(CLANCY, ammo);
        default -> new SmallExplodableCollisionHandler(CLANCY, ammo);
      };
      physicsWorlds.getLast().addCollisionHandler(handler);
      for (var unit : units) {
        physicsWorlds.getLast().addCollisionHandler(handler.copyFor(unit, ammo));
      }
      if (airAmmos.contains(ammo)) {
        physicsWorlds.getLast().addCollisionHandler(handler.copyFor(GROUND, ammo));
      }
    }

    for (var unit : units) {
      physicsWorlds.getLast().addCollisionHandler(new TranslucentCollisionHandler(CLANCY, unit));
    }
  }

  private Music bgm;
  private final ParticleSystem tessaParticleSystem = new ParticleSystem();
  private final ParticleSystem smokeParticleSystem = new ParticleSystem();

  @Override
  public void initGame(List<GameWorld> gameWorlds, XInput input) {
    var bottom = new HitBox(new Point2D(0.0, 800), BoundingShape.box(getGameWidth(), 100));
    var top = new HitBox(new Point2D(0.0, 0), BoundingShape.box(getGameWidth(), 600));

    immaterium = gameWorlds.getFirst();
    materium = gameWorlds.getLast();

    immaterium.addEntity(Borders.of(getGameWidth(), getGameHeight(), top, bottom));

    var c = ClancyComponent2.of(100, 650);
    immaterium.addEntity(c);
    clancy = ClancyViewComponent2.of(c, PropertyKey.CLANCY, FXGL.geto(PropertyKey.CLANCY));
    materium.addEntity(clancy);

    clancy.setOnNotActive(() -> settle(false));

    initClancyInput(c, input);

    spawn(LAUNCHER, getGameWidth(), FXGLMath.random(650, 750)).setOnNotActive(()-> settle(true));

    var bgms = List.of("maou_game_battle34.mp3","maou_game_battle37.mp3");

    bgm = FXGL.loopBGM(bgms.get(new Random().nextInt(bgms.size())));
    bgm.getAudio().setVolume(0);
    new Transition() {
      {
        setCycleDuration(Duration.seconds(1));
      }

      protected void interpolate(double frac) {
        bgm.getAudio().setVolume(FXGL.getSettings().getGlobalMusicVolume() * frac);
      }
    }.play();

    initSmokeParticleSystem();
  }

  @Override
  public void initUI(Viewport viewport, GameScene gameScene, XInput input) {
    viewport.setBounds(0, 0, getGameWidth(), getGameHeight());
    viewport.bindToEntity(clancy, FXGL.getAppWidth() / 10.0, 0);

    //show hp bar
    initClancyUI(clancy, gameScene);
  }

  @Override
  public void disableConcurrency() {
    for (var gameSubScene : concurrentGameSubScenes) {
      gameSubScene.setAllowConcurrency(false);
    }
  }

  @Override
  public void restoreConcurrency() {
    for (var gameSubScene : concurrentGameSubScenes) {
      gameSubScene.setAllowConcurrency(true);
    }
  }

  @Override
  public void disableInput() {
    for (var gameScene : gameScenes) {
      gameScene.getInput().setRegisterInput(false);
    }
  }

  @Override
  public void restoreInput() {
    for (var gameScene : gameScenes) {
      gameScene.getInput().setRegisterInput(true);
    }
  }


  public Entity spawn(Type type, SpawnData data, GameWorld gameWorld) {
    var entity = build(type, data);
    gameWorld.addEntity(entity);

    return entity;
  }

  @Override
  public Entity spawn(Type type, SpawnData data) {
    var entity = switch (type) {
      case LAUNCHER -> {
        var launcher = Shadow.of(data);
        immaterium.addEntity(launcher);
        yield Launcher.of(launcher, data);
      }
      default -> build(type, data);
    };

    materium.addEntity(entity);

    return entity;
  }

  @Override
  public void update(double tpf) {
    tessaParticleSystem.onUpdate(tpf);
    smokeParticleSystem.onUpdate(tpf);

    if (phase != Phase.BATTLE) return;
    updateFrontLine(materium);

    if (FXGLMath.random(0, 300) == 0) {
      spawnEnemyUnit(getGameWidth() - 200, FXGLMath.random(650, 750));
    }
  }

  private void spawnEnemyUnit(double x, double y) {
    spawn(LAUNCHER, new SpawnData(x, y));
  }

  private void settle(boolean victory, Runnable... runnables) {
    if (phase == Phase.SETTLEMENT) return;
    phase = Phase.SETTLEMENT;

    var transition = new Transition() {
      {
        setCycleDuration(Duration.seconds(3));
      }

      protected void interpolate(double frac) {
        bgm.getAudio().setVolume(FXGL.getSettings().getGlobalMusicVolume() * (1 - frac));
      }
    };
    transition.setOnFinished(_1 -> bgm.getAudio().stop());
    transition.play();

    Arrays.stream(runnables).forEach(Runnable::run);
    FXGL.<GameApp>getAppCast().push(Index.SCENE_NAME);
  }

  enum Phase {
    BATTLE, SETTLEMENT
  }

  private void initSmokeParticleSystem() {
    // particle smoke
    var t = FXGL.texture("particle/smoke.png", 128.0, 128.0).brighter().brighter();

    var emitter = ParticleEmitters.newFireEmitter();
    emitter.setBlendMode(BlendMode.SRC_OVER);
    emitter.setSourceImage(t.getImage());
    emitter.setSize(150.0, 220.0);
    emitter.setNumParticles(10);
    emitter.setEmissionRate(0.01 * getGameWidth() / FXGL.getAppWidth());
    emitter.setVelocityFunction((_1) -> new Point2D(FXGL.random() * 2.5, -FXGL.random() * FXGL.random(80, 120)));
    emitter.setExpireFunction((_1) -> Duration.seconds(FXGL.random(4, 7)));
    emitter.setScaleFunction((_1) -> new Point2D(0.15, 0.10));
    emitter.setSpawnPointFunction((_1 -> new Point2D(FXGL.random(0.0, getGameWidth() - 200.0), 120.0)));

    smokeParticleSystem.addParticleEmitter(emitter, 0.0, FXGL.getAppHeight());
  }
}