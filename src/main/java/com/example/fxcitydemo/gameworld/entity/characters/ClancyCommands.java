package com.example.fxcitydemo.gameworld.entity.characters;

 public interface ClancyCommands {
   void moveRight();

   void moveLeft();

   void stopMovingRight();

   void stopMovingLeft();

   void jump();

   void fire();

   void stopFire();

   void upward();

   void stopUpward();

   default void downward(){}

   default void stopDownward(){}
}
