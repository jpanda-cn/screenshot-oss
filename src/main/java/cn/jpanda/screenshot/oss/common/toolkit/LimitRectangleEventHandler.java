package cn.jpanda.screenshot.oss.common.toolkit;

import cn.jpanda.screenshot.oss.service.handlers.GeneralSplitMouseEventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Optional;

/**
 * 限制矩形空间坐标的事件处理器
 * 指定的矩形的大小以及坐标的变更将会受限于他所属的矩形范围内，以及其内部包含的其他图形
 */
public class LimitRectangleEventHandler extends GeneralSplitMouseEventHandler {
    /**
     * 变更的矩形
     */
    protected Rectangle rectangle;
    /**
     * 所属的父类矩形
     */
    protected Rectangle parent;

    /**
     * 首先的子节点
     */
    protected List<Rectangle> subs;
    protected double x;
    protected double y;
    protected double ox;
    protected double oy;
    protected double ow;
    protected double oh;

    public LimitRectangleEventHandler(Rectangle rectangle) {
        this(rectangle, null);
    }

    public LimitRectangleEventHandler(Rectangle rectangle, Rectangle parent) {
        this(rectangle, parent, null);
    }

    public LimitRectangleEventHandler(Rectangle rectangle, Rectangle parent, List<Rectangle> subs) {
        this.rectangle = rectangle;
        this.parent = parent;
        this.subs = subs;
        ox = rectangle.xProperty().get();
        oy = rectangle.yProperty().get();
    }

    protected void press(MouseEvent event) {
        // 鼠标开始节点
        x = event.getSceneX();
        // 鼠标结束节点
        y = event.getSceneY();
        ox = rectangle.xProperty().get();
        oy = rectangle.yProperty().get();
        ow = rectangle.widthProperty().get();
        oh = rectangle.heightProperty().get();
    }


    protected void setRectangleX(double x) {
        if (checkInParentX(x) && checkOutSubsX(x)) {
            rectangle.xProperty().set(x);
        }
    }

    protected void setRectangleY(double y) {
        if (checkInParentY(y) && checkOutSubsY(y)) {
            rectangle.yProperty().set(y);
        }
    }

    protected void setRectangleW(double w) {
        if (checkInParentW(w) && checkOutSubsW(w)) {
            rectangle.widthProperty().set(w);
        }
    }

    protected void setRectangleH(double h) {
        if (checkInParentH(h) && checkOutSubsH(h)) {
            rectangle.heightProperty().set(h);
        }
    }

    protected boolean checkInParentX(double x) {
        if (parent == null) {
            return true;
        }
        return x >= parent.xProperty().get() && x <= parent.xProperty().add(parent.widthProperty()).subtract(rectangle.widthProperty()).get();
    }

    protected boolean checkOutSubsX(double x) {
        if (null == subs || subs.isEmpty()) {
            return true;
        }
        Optional<Rectangle> minX = subs.stream().min((a, b) -> a.xProperty().subtract(b.xProperty()).intValue());
        // 校验最小X
        return x <= minX.get().xProperty().get();
    }

    protected boolean checkInParentY(double y) {
        if (parent == null) {
            return true;
        }
        return y >= parent.yProperty().get() && y <= parent.yProperty().add(parent.heightProperty()).subtract(rectangle.heightProperty()).get();
    }

    protected boolean checkOutSubsY(double y) {
        if (null == subs || subs.isEmpty()) {
            return true;
        }
        Optional<Rectangle> minY = subs.stream().min((a, b) -> a.yProperty().subtract(b.yProperty()).intValue());
        // 校验最小X
        return x <= minY.get().xProperty().get();
    }

    protected boolean checkInParentW(double w) {
        if (parent == null) {
            return true;
        }
        // 校验宽度
        return rectangle.xProperty().add(w).get() <= parent.xProperty().add(parent.widthProperty()).get();
    }

    protected boolean checkOutSubsW(double w) {
        if (null == subs || subs.isEmpty()) {
            return true;
        }
        Optional<Rectangle> maxEndX = subs.stream().max((a, b) -> a.xProperty().add(a.widthProperty()).subtract(b.xProperty().add(b.widthProperty())).intValue());
        // 校验最大X
        double recEx = rectangle.xProperty().add(w).get();
        double maxEx = maxEndX.get().xProperty().add(maxEndX.get().widthProperty()).get();
        return recEx >= maxEx;
    }

    protected boolean checkInParentH(double h) {
        if (parent == null) {
            return true;
        }
        // 校验高度
        return rectangle.yProperty().add(h).get() <= parent.yProperty().add(parent.heightProperty()).get();
    }

    protected boolean checkOutSubsH(double h) {
        if (null == subs || subs.isEmpty()) {
            return true;
        }
        Optional<Rectangle> maxEndY = subs.stream().max((a, b) -> a.yProperty().add(a.heightProperty()).subtract(b.yProperty().add(b.heightProperty())).intValue());
        // 校验最大X
        double recEy = rectangle.yProperty().add(h).get();
        double maxEy = maxEndY.get().yProperty().add(maxEndY.get().heightProperty()).get();
        return recEy >= maxEy;
    }
}
