package cn.jpanda.screenshot.oss.view.tray.handlers.arrow;

import cn.jpanda.screenshot.oss.service.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.handlers.TrayConfig;
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
        if (group != null) {
            group.setMouseTransparent(true);
        }
        x = event.getScreenX();
        y = event.getScreenY();
        arrow = new Arrow();
        group = arrow.getGroup();
        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.ARROW);
        // 生成一个新的矩形
        // 配置矩形颜色和宽度
        arrow.setStrokeWidth(config.getStroke());
        arrow.setStroke(config.getStrokeColor());
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
        // 获取需要移动的元素变更其展示位置
        arrow.endXProperty().set(event.getScreenX());
        arrow.endYProperty().set(event.getScreenY());
    }

    @Override
    protected void release(MouseEvent event) {
        super.release(event);
    }
}
