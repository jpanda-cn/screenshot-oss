package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.drag;

import cn.jpanda.screenshot.oss.common.toolkit.Bounds;
import cn.jpanda.screenshot.oss.common.toolkit.ShapeCovertHelper;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class LimitDragInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
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

    public LimitDragInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
            rectangle.setCursor(Cursor.MOVE);
        } else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            DestroyGroupBeanHolder destroyGroupBeanHolder = canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class);
            destroyGroupBeanHolder.destroy();
            subs = ShapeCovertHelper.toRectanglesUseGroup(canvasProperties.listGroups());
            parent = ShapeCovertHelper.toRectangle(rectangle.getScene().getWindow());
            // 记录当前位置
            // 鼠标开始节点
            x = event.getSceneX();
            // 鼠标结束节点
            y = event.getSceneY();
            ox = rectangle.xProperty().get();
            oy = rectangle.yProperty().get();
            ow = rectangle.widthProperty().get();
            oh = rectangle.heightProperty().get();
        } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            double offsetX = event.getSceneX() - x;
            double offsetY = event.getSceneY() - y;
            setRectangleX(ox + offsetX);
            setRectangleY(oy + offsetY);
            canvasDrawEventHandler.draw(new Bounds(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight()));

        }
    }


    protected boolean setRectangleX(double x) {
        if (checkInParentX(x) && checkOutSubsX(x)) {
            rectangle.xProperty().set(x);
            return true;
        }
        return false;
    }

    protected boolean setRectangleY(double y) {
        if (checkInParentY(y) && checkOutSubsY(y)) {
            rectangle.yProperty().set(y);
            return true;
        }
        return false;
    }

    protected boolean checkInParentX(double x) {
        if (parent == null) {
            return true;
        }
        double px = parent.xProperty().get() - rectangle.getScene().getWindow().getX();
        return x >= px && x <= px + (parent.widthProperty()).subtract(rectangle.widthProperty()).get();
    }

    protected boolean checkOutSubsX(double x) {
        if (null == subs || subs.isEmpty()) {
            return true;
        }
        Optional<Double> minX = subs.stream().map((r) -> r.getLayoutBounds().getMinX()).min(Comparator.comparingDouble(a -> a));
        Optional<Double> maxX = subs.stream().map((r) -> r.getLayoutBounds().getMaxX()).max(Comparator.comparingDouble(a -> a));
        // 校验最小X
        return maxX.map(aDouble -> x <= minX.get() && x + rectangle.widthProperty().get() > aDouble).orElse(true);
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
        Optional<Double> minY = subs.stream().map((r) -> r.getLayoutBounds().getMinY()).min(Comparator.comparingDouble(a -> a));
        Optional<Double> maxY = subs.stream().map((r) -> r.getLayoutBounds().getMaxY()).max(Comparator.comparingDouble(a -> a));
        // 校验最小X
        return maxY.map(aDouble -> y <= minY.get() && y + rectangle.heightProperty().get() > aDouble).orElse(true);
    }

}
