package com.example.fxcitydemo.gameworld.entity.characters;

import java.io.Serializable;

public abstract class Character implements Serializable {
  protected String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
