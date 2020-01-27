package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic;

import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.core.shotkey.DefaultGroupScreenshotsElements;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.CanvasShortcutManager;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import lombok.SneakyThrows;

/**
 * 对于马赛克的实现目前有两种思路：
 * 一种是: 获取固定区域内的平均RGB，或者中心RGB，然后渲染到该固定区域内。
 * 另一种是:采用图片贴纸的方式来完成
 */
public class MosaicInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    /**
     * 当前操作的马赛克
     */
    protected Group group;
    /**
     * 当前路径
     */
    protected Path path;


    public MosaicInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler, CanvasShortcutManager canvasShortcutManager) {
        super(canvasProperties, canvasDrawEventHandler,canvasShortcutManager);
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
    }


    @Override
    protected void release(MouseEvent event) {
        clear();
    }

    protected void clear() {
        canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class).set(() -> {
            if (group != null) {
                group.setMouseTransparent(true);
            }
        });
    }


}
