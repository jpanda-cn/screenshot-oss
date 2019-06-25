package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.core.ScreenshotsProcess;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.core.shotkey.DefaultGroupScreenshotsElements;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.toolkits.TrayConfig;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

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
//        path.strokeWidthProperty().set(config.getStroke().getValue());
//        path.strokeWidthProperty().bind(config.getStroke());
//        path.strokeProperty().set(config.getStrokeColor().getValue());
//        path.strokeProperty().bind(config.getStrokeColor());
    }

    @Override
    protected void drag(MouseEvent event) {
        double cx = event.getSceneX();
        double cy = event.getSceneY();
        List<PathElement> elements = path.getElements();
        PathElement element = elements.get(elements.size() - 1);
        double width = path.getStrokeWidth();
        if (element instanceof LineTo) {
            double lx = ((LineTo) element).getX();
            double ly = ((LineTo) element).getY();
            double w = MathUtils.subAbs(cx, lx);
            double h = MathUtils.subAbs(cy, ly);
            lx = MathUtils.min(lx, lx + w);
            ly = MathUtils.min(ly, ly + h);

            BufferedImage image = canvasProperties.getConfiguration().getUniqueBean(ScreenshotsProcess.class)
                    .snapshot(canvasProperties.getCutPane().getScene()
                            , new Rectangle(lx, ly, w, h));
            int rgb = 0;
            for (double sx = image.getMinX(); sx < image.getMinX() + image.getWidth(); sx++) {
                for (double sy = image.getMinY(); sy < image.getMinY() + image.getHeight(); sy++) {
                    rgb += image.getRGB((int) sx, (int) sy);
                }
            }
            rgb = (int) (rgb / (w * h));
            Color color = new Color(rgb);
            path.setStroke(javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue()));
        }
        if (rectangle.contains(cx, cy)) {
            path.getElements().add(new LineTo(cx, cy));
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
