package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.pen;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.core.shotkey.DefaultGroupScreenshotsElements;
import cn.jpanda.screenshot.oss.core.shotkey.ScreenshotsElementsHolder;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.toolkits.TrayConfig;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.*;

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
        clear();
        x = event.getSceneX();
        y = event.getSceneY();
        if (group != null) {
            group.setMouseTransparent(true);
        }
        path = new Path();
        path.getElements().add(new MoveTo(x, y));
        group = new Group(path);
        canvasProperties.getCutPane().getChildren().add(group);
        canvasProperties.getScreenshotsElementsHolder().putEffectiveElement(new DefaultGroupScreenshotsElements(group,  canvasProperties));
        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.PEN);
        path.strokeWidthProperty().set(config.getStroke().getValue());
        path.strokeWidthProperty().bind(config.getStroke());
        path.strokeProperty().set(config.getStrokeColor().getValue());
        path.strokeProperty().bind(config.getStrokeColor());
    }

    @Override
    protected void drag(MouseEvent event) {

        double cx = event.getSceneX();
        double cy = event.getSceneY();


        if (rectangle.contains(cx, cy)) {
            PathElement line;
            if (event.isShiftDown()) {
                boolean iss = MathUtils.subAbs(cx, x) > MathUtils.subAbs(cy, y);
                line = iss ? new HLineTo(cx) : new VLineTo(cy);
            } else {
                line = new LineTo(cx, cy);
            }

            path.getElements().add(line);
        }
    }


    @Override
    protected void release(MouseEvent event) {
//        clear();
    }

    protected void clear() {
        canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class).set(() -> {
            if (group != null) {
                group.setMouseTransparent(true);
                canvasProperties.putGroup(group);
            }
            if (path != null) {
                path.strokeProperty().unbind();
                path.strokeWidthProperty().unbind();
            }
        });
    }

}
