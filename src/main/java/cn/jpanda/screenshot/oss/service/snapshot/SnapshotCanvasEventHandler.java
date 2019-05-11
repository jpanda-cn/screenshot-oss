package cn.jpanda.screenshot.oss.service.snapshot;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public abstract class SnapshotCanvasEventHandler implements EventHandler<Event> {
    @Override
    public void handle(Event event) {
        if (event instanceof KeyEvent) {
            handlerKey((KeyEvent) event);
        } else if (event instanceof MouseEvent) {
            handlerMouse((MouseEvent) event);
        }
    }

    protected abstract void handlerKey(KeyEvent event);

    protected abstract void handlerMouse(MouseEvent event);
}
