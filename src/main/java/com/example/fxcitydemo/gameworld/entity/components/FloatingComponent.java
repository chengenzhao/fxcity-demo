package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableNumberValue;

public class FloatingComponent extends Component {

  double maxSeep;
  double currentSpeed = maxSeep;
  double acceleration;
  int direction = -1;

  private ObservableNumberValue observableValue = null;
  private final DoubleProperty floatingY = new SimpleDoubleProperty(0);

  @Override
  public void onAdded() {
    if(observableValue == null){
      observableValue = new SimpleDoubleProperty(entity.getY());
    }
    entity.yProperty().unbind();
    entity.yProperty().bind(floatingY.add(observableValue));
  }

  public FloatingComponent() {
    this(1.2, 0.1);
  }

  public FloatingComponent(double maxSeep, double acceleration) {
    this.maxSeep = maxSeep;
    this.acceleration = acceleration;
  }

  public FloatingComponent(ObservableNumberValue observableValue) {
    this(1.2, 0.1);
    this.observableValue = observableValue;
  }

  public FloatingComponent(double maxSeep, double acceleration, ObservableNumberValue observableValue) {
    this.maxSeep = maxSeep;
    this.acceleration = acceleration;
    this.observableValue = observableValue;
  }

  @Override
  public void onUpdate(double tpf) {
    currentSpeed += direction * acceleration;
    if(FXGLMath.abs(currentSpeed) + 0.005 > maxSeep ){
      direction *= -1;//reverse direction
    }

//    entity.setY(entity.getY()+currentSpeed);
    floatingY.set(floatingY.get() + currentSpeed);
  }

  public double getFloatingY() {
    return floatingY.get();
  }

  public DoubleProperty floatingYProperty() {
    return floatingY;
  }
}
