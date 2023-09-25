package com.example.fxcitydemo.gameworld.collisionhandler;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.Trigger;
import com.almasb.fxgl.input.TriggerListener;
import javafx.application.Platform;

/**
 * Trigger once for pressing specific key
 * Overriding key action end method
 */
public class TriggerOnceListener extends TriggerListener {

  private final Runnable runnable;

  public TriggerOnceListener(Runnable runnable) {
    this.runnable = runnable;
  }

  @Override
  protected void onActionEnd(Trigger trigger) {
    super.onActionEnd(trigger);

    if(runnable.run(trigger)){
      Platform.runLater(()->FXGL.getInput().removeTriggerListener(this));
    }
  }

  @FunctionalInterface
  public interface Runnable{
    /**
     *
     * @param trigger
     * @return whether the trigger needs removed
     */
    boolean run(Trigger trigger);
  }
}

