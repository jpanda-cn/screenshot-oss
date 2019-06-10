package cn.jpanda.screenshot.oss.view.tray.handlers;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ShapeCovertHelper {

    public static Rectangle toRectangle(Ellipse ellipse) {
        double x = ellipse.centerXProperty().get();
        double y = ellipse.centerYProperty().get();
        double w = ellipse.radiusXProperty().get();
        double h = ellipse.radiusYProperty().get();
        // 绘制一个矩形
        return new Rectangle(x - w, y - h, w * 2, h * 2);
    }

    public static Rectangle toRectangle(Rectangle rectangle) {
        return new Rectangle(rectangle.xProperty().get(), rectangle.yProperty().get(), rectangle.widthProperty().get(), rectangle.heightProperty().get());
    }

    public static Rectangle toRectangle(Pane pane) {
        return new Rectangle(pane.layoutXProperty().get(), pane.layoutYProperty().get(), pane.widthProperty().get(), pane.heightProperty().get());
    }

    public static Rectangle toRectangle(TextArea pane) {
        return new Rectangle(pane.layoutXProperty().get(), pane.layoutYProperty().get(), pane.widthProperty().get(), pane.heightProperty().get());
    }

    public static Rectangle toRectangle(Node node) {
        if (node instanceof Ellipse) {
            return toRectangle((Ellipse) node);
        } else if (node instanceof Rectangle) {
            return toRectangle((Rectangle) node);
        } else if (node instanceof Pane) {
            return toRectangle((Pane) node);
        } else if (node instanceof TextArea) {
            return toRectangle((TextArea) node);
        }
        return null;
    }

    public static List<Rectangle> toRectangles(List<Node> nodes) {
        if (null == nodes || nodes.isEmpty()) {
            return new ArrayList<>();
        }
        return nodes.stream().map(ShapeCovertHelper::toRectangle).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
