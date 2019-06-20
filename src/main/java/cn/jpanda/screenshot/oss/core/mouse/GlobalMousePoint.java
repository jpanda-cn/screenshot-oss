package cn.jpanda.screenshot.oss.core.mouse;

import javafx.beans.property.SimpleObjectProperty;

import java.awt.*;

public class GlobalMousePoint {
    public SimpleObjectProperty<Point> pointSimpleObjectProperty = new SimpleObjectProperty<>(new Point(0, 0));
}
