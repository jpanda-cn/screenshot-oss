package cn.jpanda.screenshot.oss.view.snapshot;

import lombok.Data;

@Data
public class DrawRectangle {
    private double x;
    private double y;
    private double width;
    private double height;

    public DrawRectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getEndX() {
        return x + width;
    }

    public double getEndY() {
        return y + height;
    }
}
