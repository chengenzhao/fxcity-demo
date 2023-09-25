package com.example.fxcitydemo.gameworld.entity.characters;

public class Clancy extends Character {
  private int hp = 1000;
  private int attack = 15;
  private int defense = 0;

  public Clancy() {
    this.name = "Clancy";
  }

  public int getHp() {
    return hp;
  }

  public void setHp(int hp) {
    this.hp = hp;
  }

  public int getAttack() {
    return attack;
  }

  public void setAttack(int attack) {
    this.attack = attack;
  }

  public int getDefense() {
    return defense;
  }

  public void setDefense(int defense) {
    this.defense = defense;
  }

  @Override
  public String toString() {
    return "Clancy{" +
      "hp=" + hp +
      ", attack=" + attack +
      ", defense=" + defense +
      ", name='" + name + '\'' +
      '}';
  }
}
