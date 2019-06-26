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
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.image.BufferedImage;

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
    @SneakyThrows
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

    }

    @Override
    @SneakyThrows
    protected void drag(MouseEvent event) {
        double cx = event.getSceneX();
        double cy = event.getSceneY();
        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.MOSAIC);
        double width = config.getStroke().get() * 2;
        if (width < 5) {
            width = 5;
        }
        // 获取指定索引范围内的图片
        ScreenshotsProcess screenshotsProcess = canvasProperties.getConfiguration().getUniqueBean(ScreenshotsProcess.class);

        BufferedImage bufferedImage = screenshotsProcess.snapshot(canvasProperties.getCutPane().getScene(), new Rectangle(MathUtils.min(cx, x), MathUtils.min(cy, y), width, width));
        // 计算平均RGBA
        Color color = new Color(bufferedImage.getRGB((int) (width/2), (int) (width/2)));
        path.setStroke(javafx.scene.paint.Color.valueOf(String.format("rgb(%d,%d,%d)", color.getRed(), color.getGreen(), color.getBlue())));
        path.setStrokeWidth(width);
        path = new Path();
        group.getChildren().add(path);
        path.getElements().add(new MoveTo(cx, cy));
        x = cx;
        y = cy;
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
