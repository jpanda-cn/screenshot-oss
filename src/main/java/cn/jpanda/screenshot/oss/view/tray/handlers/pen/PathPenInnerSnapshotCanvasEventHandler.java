package cn.jpanda.screenshot.oss.view.tray.handlers.pen;

import cn.jpanda.screenshot.oss.service.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.handlers.TrayConfig;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * 通过{@link Path}实现画笔功能
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2019/6/12 17:05
 */
public class PathPenInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    private Group group;
    private Path path;

    public PathPenInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    protected void move(MouseEvent event) {
        rectangle.setCursor(Cursor.DISAPPEAR);
    }

    @Override
    protected void press(MouseEvent event) {

        x = event.getScreenX();
        y = event.getScreenY();
        if (group != null) {
            group.setMouseTransparent(true);
        }
        path = new Path();
        path.getElements().add(new MoveTo(x, y));
        group = new Group(path);
        canvasProperties.getCutPane().getChildren().add(group);
        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.PEN);
        path.strokeWidthProperty().set(config.getStroke().getValue());
        path.strokeWidthProperty().bind(config.getStroke());
        path.strokeProperty().set(config.getStrokeColor().getValue());
        path.strokeProperty().bind(config.getStrokeColor());
    }

    @Override
    protected void drag(MouseEvent event) {

        double cx = event.getScreenX();
        double cy = event.getScreenY();
        if (rectangle.contains(cx, cy)) {
            path.getElements().add(new LineTo(cx, cy));
        }
    }


    @Override
    protected void release(MouseEvent event) {
        clear();
    }

    protected void clear() {
        if (group != null) {
            group.setMouseTransparent(true);
            canvasProperties.putGroup(group);
        }
        if (path != null) {
            path.strokeProperty().unbind();
            path.strokeWidthProperty().unbind();
        }
    }
}
