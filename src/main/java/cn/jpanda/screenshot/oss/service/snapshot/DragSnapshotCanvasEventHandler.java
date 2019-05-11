package cn.jpanda.screenshot.oss.service.snapshot;

import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.snapshot.DrawRectangle;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class DragSnapshotCanvasEventHandler extends SnapshotCanvasEventHandler {
    protected double x;
    protected double y;
    protected double ox;
    protected double oy;
    protected CanvasProperties canvasProperties;
    protected Rectangle rectangle;
    protected CanvasDrawEventHandler canvasDrawEventHandler;

    public DragSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        this.canvasProperties = canvasProperties;
        this.canvasDrawEventHandler = canvasDrawEventHandler;
        rectangle = canvasProperties.getCutRectangle();
        ox=rectangle.xProperty().get();
        ox=rectangle.yProperty().get();
    }

    @Override
    protected void handlerKey(KeyEvent event) {

    }

    @Override
    protected void handlerMouse(MouseEvent event) {


        if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            rectangle.setCursor(Cursor.MOVE);
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
            rectangle.xProperty().set(ox + offsetX);
            rectangle.yProperty().set(oy + offsetY);
            canvasDrawEventHandler.draw(new DrawRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight()));


        } else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {

        }
    }
}
