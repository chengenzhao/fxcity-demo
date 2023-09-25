package com.example.fxcitydemo.gameworld;

import com.almasb.fxgl.entity.SpawnData;
import com.whitewoodcity.fxgl.app.ImageData;

import java.util.HashSet;
import java.util.Set;

public interface ParallelCacheService {
  default Set<ImageData> cacheSet(SpawnData data, Type... types) {
    var set = new HashSet<ImageData>();
    for (var type : types) {
      set.addAll(switch (type) {
//        case TREE ->
//          data.hasKey("treeType") ? Tree.getImageData(data.get("treeType")) : Tree.getImageData();
//        case GROUND -> Ground.getImageData(data.get("groundType"));
//        case GROUND_FONT -> GroundFront.getImageData(data.get("groundType"));
//        case LOCOMOTIVE -> Locomotive.getImageData();
//        case CART -> Carts.getImageData();
//        case CLANCY -> ClancyComponent.getImageData();
//        case TESSA -> TessaComponent.getImageData();
//        case YANG -> YangComponent.getImageData();
//        case BULLET -> Bullet.getImageData();
//        case HELICOPTER_APACHE_MISSILE -> ApacheMissile.getImageData();
//        case CHINOOK_BULLET -> ChinookBullet.getImageData();
//        case F3_MISSILE -> F3Missile.getImageData();
//        case LASER_BEAM -> LaserBeam.getImageData();
//        case LAUNCHER_MISSILE -> LauncherMissile.getImageData();
//        case TANK_M7_SHELL -> TankM7Shell.getImageData();
//        case BATTLESHIP_YAMATO_MISSILE -> YamatoMissile.getImageData();
//        case BATTLESHIP_YAMATO_SHELL -> YamatoShell.getImageData();
//        case SPACE_MARINE_BULLET -> SpaceMarineBullet.getImageData();
//        case HELICOPTER_APACHE -> Apache.getImageData();
//        case HELICOPTER_CHINOOK -> Chinook.getImageData();
//        case F3 -> Fighter3.getImageData();
//        case LATV -> LATV.getImageData();
//        case LAUNCHER -> Launcher.getImageData();
//        case TANK_M7 -> Mechanism7.getImageData();
//        case BATTLESHIP_SIEGFRIED -> Siegfried.getImageData();
//        case SPACE_MARINE -> SpaceMarine.getImageData();
//        case BATTLESHIP_YAMATO -> Yamato.getImageData();
        case FIRE_BALL -> Set.of();
        default -> throw new RuntimeException("No type is matched with " + type);
      });
    }

    return set;
  }
}
