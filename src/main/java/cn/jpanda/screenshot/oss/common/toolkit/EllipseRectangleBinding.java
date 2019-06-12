package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

/**
 * 绑定圆形和环绕他的矩形的关系
 */
public final class EllipseRectangleBinding {

    public static Rectangle doBind(Group group, Ellipse ellipse) {
        // 绘制一个矩形
        Rectangle rectangle = ShapeCovertHelper.toRectangle(ellipse);
        group.getChildren().add(rectangle);
        rectangle.visibleProperty().setValue(true);
        rectangle.setStroke(javafx.scene.paint.Color.BLUE);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.toBack();
        // 绑定圆形和矩形的关系
        ellipse.centerXProperty().bind(rectangle.widthProperty().divide(2).add(rectangle.xProperty()));
        ellipse.centerYProperty().bind(rectangle.heightProperty().divide(2).add(rectangle.yProperty()));
        ellipse.radiusXProperty().bind(rectangle.widthProperty().divide(2));
        ellipse.radiusYProperty().bind(rectangle.heightProperty().divide(2));
        return rectangle;
    }
}
