package cn.jpanda.screenshot.oss.view.tray.handlers;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangleBinding {
    public static Rectangle doBind(Group group, Rectangle r) {
        // 绘制一个矩形
        Rectangle rectangle = ShapeCovertHelper.toRectangle(r);
        group.getChildren().add(rectangle);
        rectangle.visibleProperty().setValue(true);
        rectangle.setStroke(javafx.scene.paint.Color.BLUE);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.toBack();
        // 绑定矩形的关系
        r.xProperty().bind(rectangle.xProperty());
        r.yProperty().bind(rectangle.yProperty());
        r.widthProperty().bind(rectangle.widthProperty());
        r.heightProperty().bind(rectangle.heightProperty());
        return rectangle;
    }
}
