package cn.jpanda.screenshot.oss.view.tray.handlers.arrow;

import javafx.scene.Group;
import javafx.scene.shape.Line;
import lombok.Getter;

/**
 * 直线转箭头
 */
public class Arrow extends Line {
    /**
     * 左前方的箭头
     */
    private Line left;
    /**
     * 右前方的箭头
     */
    private Line right;
    /**
     * 箭头度数
     */
    private double angle;
    /**
     * 箭头长度
     */
    private double length;
    @Getter
    private Group group;

    public Arrow() {
        this(new Line(), new Line(), 30, 20);
    }

    public Arrow(double angle) {
        this(angle, 20);
    }

    public Arrow(double angle, double length) {
        this(new Line(), new Line(), angle, length);
    }

    public Arrow(Line left, Line right, double angle, double length) {
        this(left, right, angle, length, new Group());
    }

    public Arrow(Line left, Line right, double angle, double length, Group group) {
        this.left = left;
        this.right = right;
        this.angle = angle;
        this.length = length;
        this.group = group;
        doBind();
    }

    private void change() {
        changeLeft();
        changeRight();
    }

    private void changeLeft() {
        changeLine(left, angle2Radian(angle));
    }

    private void changeRight() {
        changeLine(right, -angle2Radian(angle));
    }

    /**
     * 绑定箭头和直线的关系
     */
    private void doBind() {
        group.getChildren().addAll(this, left, right);
        // 颜色同步
        left.fillProperty().bind(fillProperty());
        right.fillProperty().bind(fillProperty());
        left.strokeProperty().bind(strokeProperty());
        right.strokeProperty().bind(strokeProperty());
        left.strokeWidthProperty().bind(strokeWidthProperty());
        right.strokeWidthProperty().bind(strokeWidthProperty());

        // 展示同步
        left.visibleProperty().bind(visibleProperty());
        right.visibleProperty().bind(visibleProperty());
        // 绑定左前方箭头起始位置
        left.startXProperty().bind(endXProperty());
        left.startYProperty().bind(endYProperty());
        // 绑定右前方箭头起始位置
        right.startXProperty().bind(endXProperty());
        right.startYProperty().bind(endYProperty());
        endXProperty().addListener((observable, oldValue, newValue) -> change());
        endYProperty().addListener((observable, oldValue, newValue) -> change());
        endYProperty().addListener((observable, oldValue, newValue) -> change());
    }

    private void changeLine(Line line, double radian) {
        // 获取矩形x长度
        double x = this.endXProperty().subtract(this.startXProperty()).get();
        // 获取矩形Y长度
        double y = this.endYProperty().subtract(this.startYProperty()).get();
        //
        double vx = x * Math.cos(radian) - y * Math.sin(radian);
        double vy = x * Math.sin(radian) + y * Math.cos(radian);
        double d = Math.sqrt(vx * vx + vy * vy);
        vx = vx / d * length;
        vy = vy / d * length;
        line.endXProperty().set(this.endXProperty().subtract(vx).get());
        line.endYProperty().set(this.endYProperty().subtract(vy).get());
    }

    /**
     * 度数转弧度
     *
     * @param angle 度数
     * @return 弧度
     */
    private double angle2Radian(double angle) {
        return Math.PI * angle / 180;
    }

}

