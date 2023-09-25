package com.example.fxcitydemo.gameworld.entity.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;
import com.whitewoodcity.javafx.binding.XBindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class ShadowComponent extends Component {

  private final DoubleProperty heightProperty = new SimpleDoubleProperty(0);
  Ellipse ellipse;
  double groundY = -1;
  double radiusX, radiusY;

  public ShadowComponent(double centerX, double centerY, double radiusX, double radiusY, double groundY) {
    this(centerX, centerY, radiusX, radiusY);
    this.groundY = groundY;
  }

  public ShadowComponent(double centerX, double centerY, double radiusX, double radiusY, Property<Number> height) {
    this(centerX, centerY, radiusX, radiusY);
    heightProperty.bind(height);
  }

  public ShadowComponent(double centerX, double centerY, double radiusX, double radiusY) {
    this.radiusX = radiusX;
    this.radiusY = radiusY;
    ellipse = new Ellipse(centerX, centerY, radiusX, radiusY);
    ellipse.setFill(Color.GRAY);
    ellipse.setOpacity(.7);
  }

  @Override
  public void onAdded() {
    entity.getViewComponent().addChild(ellipse);
    if(groundY > 0 && !heightProperty.isBound())
      heightProperty.bind(XBindings.reduce(entity.yProperty(), entity.heightProperty(), (y, h) -> FXGLMath.abs(y.doubleValue() + h.doubleValue() - groundY)));
    ellipse.translateYProperty().bind(heightProperty);
    ellipse.radiusXProperty().bind(heightProperty.map(Number::doubleValue).map(height -> height > 100 ? 0 : radiusX * (100 - height) / 100));
    ellipse.radiusYProperty().bind(ellipse.radiusXProperty().map(Number::doubleValue).map(x -> x * radiusY / radiusX));
    ellipse.opacityProperty().bind(ellipse.radiusXProperty().map(Number::doubleValue).map(x -> x * .7 / radiusX));
  }

  public void bindHeightPropertyTo(ObservableValue<Number> heightProperty){
    ellipse.translateYProperty().unbind();
    ellipse.radiusXProperty().unbind();
    ellipse.radiusYProperty().unbind();
    ellipse.opacityProperty().unbind();
    ellipse.radiusXProperty().bind(heightProperty.map(Number::doubleValue).map(height -> height > 100 ? 0 : radiusX * (100 - height) / 100));
    ellipse.radiusYProperty().bind(ellipse.radiusXProperty().map(Number::doubleValue).map(x -> x * radiusY / radiusX));
    ellipse.opacityProperty().bind(ellipse.radiusXProperty().map(Number::doubleValue).map(x -> x * .7 / radiusX));
  }
}
