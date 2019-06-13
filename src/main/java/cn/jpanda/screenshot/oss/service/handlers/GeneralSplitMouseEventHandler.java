package cn.jpanda.screenshot.oss.service.handlers;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * 通用的常用鼠标事件处理器，已拆分出常用的事件
 */
public class GeneralSplitMouseEventHandler implements EventHandler<MouseEvent> {
    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
            move(event);
        } else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            press(event);
        } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            drag(event);
        } else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            release(event);
        } else {
            other(event);
        }
    }

    protected void move(MouseEvent event) {
    }

    protected void press(MouseEvent event) {
    }

    protected void drag(MouseEvent event) {
    }

    protected void release(MouseEvent event) {
    }

    protected void other(MouseEvent event) {
    }
}
