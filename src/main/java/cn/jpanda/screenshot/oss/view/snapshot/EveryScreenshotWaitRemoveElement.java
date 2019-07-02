package cn.jpanda.screenshot.oss.view.snapshot;

import cn.jpanda.screenshot.oss.common.toolkit.ExternalComponentBinders;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.RoutingSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.SnapshotRegionKeyEventHandler;
import javafx.scene.Group;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;

public class EveryScreenshotWaitRemoveElement implements WaitRemoveElement {
    private Group group;
    private Pane pane;
    private Rectangle cutRec;
    private RoutingSnapshotCanvasEventHandler routingSnapshotCanvasEventHandler;
    private SnapshotRegionKeyEventHandler snapshotRegionKeyEventHandler;
    private ExternalComponentBinders externalComponentBinders;
    private Window window;

    public EveryScreenshotWaitRemoveElement(Group group, Pane pane, Rectangle cutRec, RoutingSnapshotCanvasEventHandler routingSnapshotCanvasEventHandler, SnapshotRegionKeyEventHandler snapshotRegionKeyEventHandler, ExternalComponentBinders externalComponentBinders, Window window) {
        this.group = group;
        this.pane = pane;
        this.cutRec = cutRec;
        this.routingSnapshotCanvasEventHandler = routingSnapshotCanvasEventHandler;
        this.snapshotRegionKeyEventHandler = snapshotRegionKeyEventHandler;
        this.externalComponentBinders = externalComponentBinders;
        this.window = window;
    }

    @Override
    public void remove() {
        if (group != null) {
            pane.getChildren().remove(group);
            group = null;
        }
        if (cutRec != null) {
            if (routingSnapshotCanvasEventHandler != null) {
                cutRec.removeEventHandler(MouseEvent.ANY, routingSnapshotCanvasEventHandler);
                routingSnapshotCanvasEventHandler = null;
            }
            if (window != null) {
                if (snapshotRegionKeyEventHandler != null) {
                    window.removeEventHandler(KeyEvent.KEY_PRESSED, snapshotRegionKeyEventHandler);
                    snapshotRegionKeyEventHandler = null;
                }
            }

        }
        if (externalComponentBinders != null) {
            externalComponentBinders.unbind();
            externalComponentBinders = null;
        }
    }
}
