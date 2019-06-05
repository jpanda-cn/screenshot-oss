package cn.jpanda.screenshot.oss.view.tray.handlers.drag;

import cn.jpanda.screenshot.oss.service.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.snapshot.Bounds;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

/**
 * 拖动事件
 */
public class DragInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {

    public DragInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
            rectangle.setCursor(Cursor.MOVE);
        } else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            // 记录当前位置
            x = event.getScreenX();
            y = event.getScreenY();
            ox = rectangle.getX();
            oy = rectangle.getY();
        } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            // 拖动
            // 获取需要移动的元素变更其展示位置
            double offsetX = (event.getScreenX() - x);
            double offsetY = (event.getScreenY() - y);
            double nextX = ox + offsetX;
            double nextY = oy + offsetY;
            // 统一计算屏幕边界
            Window stage = rectangle.getScene().getWindow();
            if (!(nextX < stage.getX())
                    && (!(nextX > stage.getX() + stage.getWidth())
                    && (!(nextX + rectangle.widthProperty().get() > stage.getX() + stage.getWidth())))
            ) {
                rectangle.xProperty().set(nextX);
            }

            if ((!(nextY < 0))
                    && (!(nextY > stage.getY() + stage.getHeight())
                    && (!(nextY + rectangle.heightProperty().get() > stage.getY() + stage.getHeight()))
            )
            ) {
                rectangle.yProperty().set(nextY);
            }
            canvasDrawEventHandler.draw(new Bounds(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight()));

        }
    }
}
