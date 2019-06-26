package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.core.shotkey.DefaultGroupScreenshotsElements;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.toolkits.TrayConfig;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;

public class MosaicInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    private Group group;
    private Path path;

    public MosaicInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
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
        canvasProperties.getScreenshotsElementsHolder().putEffectiveElement(new DefaultGroupScreenshotsElements(group, canvasProperties));
        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.MOSAIC);
        double width = config.getStroke().get() * 2;
        if (width < 5) {
            width = 5;
        }
        Image image = new Image("/images/mosaic.png", width, width, true, true);
        ImagePattern imagePattern = new ImagePattern(image, 0, 0, width, width, false);
        path.setStroke(imagePattern);
        path.setStrokeWidth(width);
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
        });
    }

}
