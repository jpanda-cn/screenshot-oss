package cn.jpanda.screenshot.oss.view.snapshot;

import cn.jpanda.screenshot.oss.service.snapshot.SnapshotCanvasEventHandler;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class RoutingSnapshotCanvasEventHandler extends SnapshotCanvasEventHandler {

    protected Cursor cursor;

    @Override
    protected void handlerKey(KeyEvent event) {

    }

    @Override
    protected void handlerMouse(MouseEvent event) {
        // 根据鼠标移动事件判断执行什么操作
        // 鼠标点击时处于边界展示拖动按钮，事件转发给拖动事件处理器处理

        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {

        }
    }
}
