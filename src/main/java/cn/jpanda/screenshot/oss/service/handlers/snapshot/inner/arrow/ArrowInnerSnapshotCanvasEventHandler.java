package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.arrow;

import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.core.shotkey.DefaultGroupScreenshotsElements;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.shape.Arrow;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.toolkits.TrayConfig;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;

public class ArrowInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    /**
     * 直线
     */
    private Arrow arrow;
    /**
     * 直线所属组
     */
    private Group group;

    public ArrowInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }


    @Override
    protected void move(MouseEvent event) {
        rectangle.setCursor(Cursor.CROSSHAIR);
    }

    @Override
    protected void press(MouseEvent event) {
        clear();
        DestroyGroupBeanHolder destroyGroupBeanHolder = canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        if (group != null) {
            group.setMouseTransparent(true);
        }
        x = event.getSceneX();
        y = event.getSceneY();
        arrow = new Arrow();
        group = arrow.getGroup();
        canvasProperties.getScreenshotsElementsHolder().putEffectiveElement(new DefaultGroupScreenshotsElements(group, canvasProperties));
        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.ARROW);
        // 生成一个新的矩形
        // 配置矩形颜色和宽度
        arrow.strokeWidthProperty().bind(config.getStroke());
        arrow.strokeProperty().bind(config.getStrokeColor());
        arrow.startXProperty().set(x);
        arrow.startYProperty().set(y);
        arrow.endXProperty().set(x);
        arrow.endYProperty().set(y);
        ox = rectangle.getX();
        oy = rectangle.getY();
        canvasProperties.getCutPane().getChildren().addAll(group);
    }

    @Override
    protected void drag(MouseEvent event) {
        // 获取需要移动的元素变更其展示位置,不得超出外部区域
        if (event.getSceneX() >= rectangle.getX() && event.getSceneX() <= rectangle.getX() + rectangle.getWidth()) {
            arrow.endXProperty().set(event.getSceneX());
        }
        if (event.getSceneY() >= rectangle.getY() && event.getSceneY() <= rectangle.getY() + rectangle.getHeight()) {
            arrow.endYProperty().set(event.getSceneY());
        }
    }

    @Override
    protected void release(MouseEvent event) {
    }

    private void clear() {
        canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class).set(() -> {
            if (group != null) {
                group.setMouseTransparent(true);
                canvasProperties.putGroup(group);
            }
        });
    }

}
