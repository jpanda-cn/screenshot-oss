package cn.jpanda.screenshot.oss.service.snapshot;

import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class RoutingCanvasEventHandler extends SnapshotCanvasEventHandler {

    private CanvasProperties canvasProperties;

    private SnapshotCanvasEventHandler delegate;

    @Override
    protected void handlerKey(KeyEvent event) {
    }

    @Override
    protected void handlerMouse(MouseEvent event) {

    }
}
