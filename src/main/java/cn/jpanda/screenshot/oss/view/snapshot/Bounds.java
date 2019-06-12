package cn.jpanda.screenshot.oss.view.snapshot;

import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class Bounds {
    private double x;
    private double y;
    private double width;
    private double height;

    public Bounds(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static Bounds newInstance(Rectangle rectangle) {
        return new Bounds(rectangle.xProperty().get(), rectangle.yProperty().get(), rectangle.widthProperty().get(), rectangle.heightProperty().get());
    }

    public static Bounds newInstance(Ellipse ellipse) {
        double cx = ellipse.centerXProperty().get();
        double cy = ellipse.centerYProperty().get();
        double rx = ellipse.radiusXProperty().get();
        double ry = ellipse.radiusYProperty().get();
        return new Bounds(cx - rx, cy - ry, rx * 2, ry * 2);
    }

    public Rectangle toRectangle() {
        return new Rectangle(x, y, width, height);
    }

    public double getEndX() {
        return x + width;
    }

    public double getEndY() {
        return y + height;
    }
}
