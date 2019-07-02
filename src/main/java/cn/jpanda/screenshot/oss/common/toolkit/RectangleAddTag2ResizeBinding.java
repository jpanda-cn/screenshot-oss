package cn.jpanda.screenshot.oss.common.toolkit;

import cn.jpanda.screenshot.oss.common.enums.ResizeType;
import cn.jpanda.screenshot.oss.service.handlers.ResizeTagRectangleEventHandler;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RectangleAddTag2ResizeBinding {

    private double tagWidth = 3;
    private double tagHeight = 3;
    private Rectangle rectangle;
    /**
     * 所属的父类矩形
     */
    private Rectangle parent;

    /**
     * 首先的子节点
     */
    private List<Rectangle> subs;

    private Map<Ellipse, EventHandler<MouseEvent>> binds = new HashMap<>(8);

    public RectangleAddTag2ResizeBinding(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public RectangleAddTag2ResizeBinding(double tagWidth, double tagHeight, Rectangle rectangle) {
        this.tagWidth = tagWidth;
        this.tagHeight = tagHeight;
        this.rectangle = rectangle;
    }

    public RectangleAddTag2ResizeBinding(double tagWidth, double tagHeight, Rectangle rectangle, Rectangle parent, List<Rectangle> subs) {
        this.tagWidth = tagWidth;
        this.tagHeight = tagHeight;
        this.rectangle = rectangle;
        this.parent = parent;
        this.subs = subs;
    }

    public RectangleAddTag2ResizeBinding(Rectangle rectangle, Rectangle parent, List<Rectangle> subs) {
        this.rectangle = rectangle;
        this.parent = parent;
        this.subs = subs;
    }

    public RectangleAddTag2ResizeBinding(Rectangle rectangle, Rectangle parent) {
        this.rectangle = rectangle;
        this.parent = parent;
    }

    public RectangleAddTag2ResizeBinding(Rectangle rectangle, List<Rectangle> subs) {
        this.rectangle = rectangle;
        this.subs = subs;
    }

    public RectangleAddTag2ResizeBinding bind() {
        addResizeTag(rectangle);
        return this;
    }

    public void unbind() {
        for (Map.Entry<Ellipse, EventHandler<MouseEvent>> entry : binds.entrySet()) {
            entry.getKey().removeEventHandler(MouseEvent.ANY, entry.getValue());
        }
        binds.clear();
    }

    private void addResizeTag(Rectangle rectangle) {
        // 为矩形绘制六个角
        // 添加左上角
        Ellipse leftTop = newEllipse(Cursor.NW_RESIZE);
        leftTop.centerXProperty().bind(rectangle.xProperty());
        leftTop.centerYProperty().bind(rectangle.yProperty());
        addResizeEvent(leftTop, rectangle, ResizeType.NW_OPPOSITE);

        // 添加上
        Ellipse top = newEllipse(Cursor.V_RESIZE);
        top.centerXProperty().bind(rectangle.xProperty().add(rectangle.widthProperty().divide(2)));
        top.centerYProperty().bind(rectangle.yProperty());
        addResizeEvent(top, rectangle, ResizeType.N_VERTICAL);

        // 添加右上
        Ellipse rightTop = newEllipse(Cursor.NE_RESIZE);
        rightTop.centerXProperty().bind(rectangle.xProperty().add(rectangle.widthProperty()));
        rightTop.centerYProperty().bind(rectangle.yProperty());
        addResizeEvent(rightTop, rectangle, ResizeType.NE_OPPOSITE);

        // 添加右
        Ellipse right = newEllipse(Cursor.H_RESIZE);
        right.centerXProperty().bind(Bindings.add(rectangle.xProperty(), rectangle.widthProperty()));
        right.centerYProperty().bind(rectangle.yProperty().add(rectangle.heightProperty().divide(2)));
        addResizeEvent(right, rectangle, ResizeType.E_CROSSWISE);

        // 添加右下
        Ellipse rightCenter = newEllipse(Cursor.SE_RESIZE);
        rightCenter.centerXProperty().bind(Bindings.add(rectangle.xProperty(), rectangle.widthProperty()));
        rightCenter.centerYProperty().bind(Bindings.add(rectangle.yProperty(), rectangle.heightProperty()));
        addResizeEvent(rightCenter, rectangle, ResizeType.SE_OPPOSITE);

        // 添加下
        Ellipse below = newEllipse(Cursor.V_RESIZE);
        below.centerXProperty().bind(rectangle.xProperty().add(rectangle.widthProperty().divide(2)));
        below.centerYProperty().bind(Bindings.add(rectangle.yProperty(), rectangle.heightProperty()));
        addResizeEvent(below, rectangle, ResizeType.S_VERTICAL);

        // 添加左下
        Ellipse leftBelow = newEllipse(Cursor.SW_RESIZE);
        leftBelow.centerXProperty().bind(rectangle.xProperty());
        leftBelow.centerYProperty().bind(Bindings.add(rectangle.yProperty(), rectangle.heightProperty()));
        addResizeEvent(leftBelow, rectangle, ResizeType.SW_OPPOSITE);

        // 添加左
        Ellipse left = newEllipse(Cursor.H_RESIZE);
        left.centerXProperty().bind(rectangle.xProperty());
        left.centerYProperty().bind(rectangle.yProperty().add(rectangle.heightProperty().divide(2)));
        addResizeEvent(left, rectangle, ResizeType.W_CROSSWISE);

        // 统一添加到容器内
        ((Group) rectangle.getParent()).getChildren().addAll(leftTop, top, rightTop, right, rightCenter, below, leftBelow, left);
        Arrays.asList(leftTop, top, rightTop, right, rightCenter, below, leftBelow, left).forEach(Node::toFront);
    }

    private Ellipse newEllipse(Cursor cursor) {
        Ellipse el = new Ellipse(tagWidth, tagHeight);
        el.setCursor(cursor);
        el.setStroke(javafx.scene.paint.Color.RED);
        el.setFill(Color.TRANSPARENT);
        return el;
    }

    private void addResizeEvent(Ellipse ellipse, Rectangle rectangle, ResizeType resizeType) {
        ellipse.visibleProperty().bind(rectangle.visibleProperty());
        ResizeTagRectangleEventHandler resizeTagRectangleEventHandler = new ResizeTagRectangleEventHandler(resizeType, rectangle, parent, subs);
        ellipse.addEventHandler(MouseEvent.ANY, resizeTagRectangleEventHandler);
        binds.put(ellipse, resizeTagRectangleEventHandler);
    }
}
