package cn.jpanda.screenshot.oss.common.toolkit;

import cn.jpanda.screenshot.oss.common.toolkit.LimitRectangleEventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * 矩形拖动功能
 */
public class DragRectangleEventHandler extends LimitRectangleEventHandler {


    public DragRectangleEventHandler(Rectangle rectangle) {
        super(rectangle);
    }

    public DragRectangleEventHandler(Rectangle rectangle, Rectangle parent) {
        super(rectangle, parent);
    }

    public DragRectangleEventHandler(Rectangle rectangle, Rectangle parent, List<Rectangle> subs) {
        super(rectangle, parent, subs);
        rectangle.setCursor(Cursor.MOVE);
    }

    @Override
    protected void drag(MouseEvent event) {
        // 拖动
        // 获取鼠标偏移位置
        double offsetX = event.getScreenX() - x;
        double offsetY = event.getScreenY() - y;
        setRectangleX(ox+offsetX);
        setRectangleY(oy+offsetY);
    }
}
