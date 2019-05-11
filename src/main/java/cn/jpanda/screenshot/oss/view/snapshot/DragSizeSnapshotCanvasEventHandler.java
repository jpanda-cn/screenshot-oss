package cn.jpanda.screenshot.oss.view.snapshot;

import cn.jpanda.screenshot.oss.service.snapshot.DragSnapshotCanvasEventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class DragSizeSnapshotCanvasEventHandler extends DragSnapshotCanvasEventHandler {
    private boolean resize = false;

    public DragSizeSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    protected void handlerMouse(MouseEvent event) {
        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
            // 判断如何展示
            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();
            Cursor cursor = rectangle.getCursor();
            ox = rectangle.xProperty().get();
            oy = rectangle.yProperty().get();

            boolean onStartX = offset(mouseX, ox, 3);
            boolean onEndX = offset(mouseX, ox + rectangle.getWidth(), 3);

            boolean onStartY = offset(mouseY, oy, 3);
            boolean onEndY = offset(mouseY, oy + rectangle.heightProperty().get(), 3);

            boolean onX = onStartX || onEndX;
            boolean onY = onStartY || onEndY;

            if (onStartX) {
                // left
                if (onStartY) {
                    cursor = Cursor.NW_RESIZE;
                } else if (onEndY) {
                    cursor = Cursor.SW_RESIZE;
                } else {
                    cursor = Cursor.H_RESIZE;
                }
            } else if (onEndX) {
                // right
                if (onStartY) {
                    cursor = Cursor.NE_RESIZE;
                } else if (onEndY) {
                    cursor = Cursor.SE_RESIZE;
                } else {
                    cursor = Cursor.H_RESIZE;
                }
            } else {
                if (onY) {
                    cursor = Cursor.V_RESIZE;
                }
            }
            rectangle.setCursor(cursor);
            resize = onX || onY;
        }
        if (resize) {
            // 改变大小的来完成
            resize(event);
        } else {
            super.handlerMouse(event);
        }

    }

    public void resize(MouseEvent event) {
        if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            x = event.getScreenX();
            y = event.getScreenY();
            ox = rectangle.getX();
            oy = rectangle.getY();
        } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            // 拖动
            // 获取需要移动的元素变更其展示位置
            double offsetX = (event.getScreenX() - x);
            double offsetY = (event.getScreenY() - y);
            rectangle.widthProperty().set(rectangle.widthProperty().getValue() + offsetX);
            rectangle.heightProperty().set(rectangle.heightProperty().getValue() + offsetY);
            canvasDrawEventHandler.draw(new DrawRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight()));
        }

    }

    private boolean offset(double v1, double v2, double of) {
        return v1 >= v2 - of && v1 <= v2 + of;
    }
}
