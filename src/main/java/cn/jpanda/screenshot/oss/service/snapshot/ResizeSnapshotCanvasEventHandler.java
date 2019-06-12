package cn.jpanda.screenshot.oss.service.snapshot;

import cn.jpanda.screenshot.oss.common.toolkit.ShapeCovertHelper;
import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.view.snapshot.Bounds;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.snapshot.handlers.ResizeType;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class ResizeSnapshotCanvasEventHandler extends AbstractSnapshotCanvasEventHandler {
    private double ow;
    private double oh;
    private ResizeType resizeType;

    public ResizeSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    public void handle(MouseEvent event) {
        resize(event);
    }


    public void resize(MouseEvent event) {
        // 获取当前所有子节点
        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
            // 判断如何展示
            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();
            Cursor cursor = null;
            ox = rectangle.xProperty().get();
            oy = rectangle.yProperty().get();

            boolean onStartX = MathUtils.offset(mouseX, ox, 3);
            boolean onEndX = MathUtils.offset(mouseX, ox + rectangle.getWidth(), 3);

            boolean onStartY = MathUtils.offset(mouseY, oy, 3);
            boolean onEndY = MathUtils.offset(mouseY, oy + rectangle.heightProperty().get(), 3);

            boolean onX = onStartX || onEndX;
            boolean onY = onStartY || onEndY;
            if (onStartX) {
                // left
                if (onStartY) {
                    cursor = Cursor.NW_RESIZE;
                    resizeType = ResizeType.NW_OPPOSITE;
                } else if (onEndY) {
                    cursor = Cursor.SW_RESIZE;
                    resizeType = ResizeType.SW_OPPOSITE;
                } else {
                    cursor = Cursor.H_RESIZE;
                    resizeType = ResizeType.W_CROSSWISE;
                }
            } else if (onEndX) {
                // right
                if (onStartY) {
                    cursor = Cursor.NE_RESIZE;
                    resizeType = ResizeType.NE_OPPOSITE;
                } else if (onEndY) {
                    cursor = Cursor.SE_RESIZE;
                    resizeType = ResizeType.SE_OPPOSITE;
                } else {
                    cursor = Cursor.H_RESIZE;
                    resizeType = ResizeType.E_CROSSWISE;
                }
            } else if (onY) {
                cursor = Cursor.V_RESIZE;
                resizeType = onStartY ? ResizeType.N_VERTICAL : ResizeType.S_VERTICAL;
            }
            if (cursor != null) {
                rectangle.setCursor(cursor);
            }
        } else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            x = event.getScreenX();
            y = event.getScreenY();
            ow = rectangle.widthProperty().get();
            oh = rectangle.heightProperty().get();
        } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            // 拖动
            // 获取需要移动的元素变更其展示位置
            double offsetX = event.getScreenX() - x;
            double offsetY = event.getScreenY() - y;
            // 判断是否修改宽度和x坐标

            if (resizeType.isAcross()) {
                // 只有当前鼠标坐标小于之前的x坐标时才会修改x坐标
                // 修改x坐标

                if (resizeType.isLeft()) {
                    // 右侧不变，变更x
                    // 判断使用鼠标坐标为x还是使用边界为x
                    double endx = ox + ow;
                    if (event.getScreenX() > endx) {
                        // 以endx作为x，同时宽度为鼠标-endx
                        rectangle.xProperty().set(endx);
                        rectangle.widthProperty().set(event.getScreenX() - endx);
                    } else {
                        rectangle.xProperty().set(event.getScreenX());
                        rectangle.widthProperty().set(endx - event.getScreenX());
                    }
                } else {
                    rectangle.xProperty().set(MathUtils.min(event.getScreenX(), ox));
                    rectangle.widthProperty().set(Math.abs(ow + offsetX));
                }


                // 修改宽度，宽度等于老的宽度+偏移量

            }
            // 判断是否修改高度和y坐标
            if (resizeType.isVertical()) {
                if (resizeType.isTop()) {
                    double endy = oy + oh;
                    if (event.getScreenY() < endy) {
                        rectangle.yProperty().set(event.getScreenY());
                        rectangle.heightProperty().set(endy - event.getScreenY());
                    } else {
                        rectangle.yProperty().set(endy);
                        rectangle.heightProperty().set(event.getScreenY() - endy);
                    }
                } else {
                    rectangle.yProperty().set(MathUtils.min(event.getScreenY(), oy));
                    rectangle.heightProperty().set(Math.abs(oh + offsetY));
                }

            }
            // 使用LimitRectangleEventHandler来修正数据
            canvasDrawEventHandler.draw(new Bounds(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight()));
        }

    }


}
