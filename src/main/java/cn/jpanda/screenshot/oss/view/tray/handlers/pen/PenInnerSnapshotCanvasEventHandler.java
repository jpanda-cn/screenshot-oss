package cn.jpanda.screenshot.oss.view.tray.handlers.pen;

import cn.jpanda.screenshot.oss.service.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.handlers.TrayConfig;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;


/**
 * 画笔绘制事件
 * 在截图区域内，生成一个Canvas
 */
public class PenInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    private Group group;
    private Canvas canvas;

    public PenInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
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
        canvas = new Canvas();
        canvas.layoutXProperty().bind(rectangle.xProperty());
        canvas.layoutYProperty().bind(rectangle.yProperty());
        canvas.widthProperty().bind(rectangle.widthProperty());
        canvas.heightProperty().bind(rectangle.heightProperty());

        group = new Group(canvas);
        canvasProperties.getCutPane().getChildren().add(group);

        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.PEN);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // 生成一个新的矩形
        // 配置矩形颜色和宽度
        gc.setLineWidth(config.getStroke());
        gc.setStroke(config.getStrokeColor());

    }

    @Override
    protected void drag(MouseEvent event) {
        double cx = event.getScreenX();
        double cy = event.getScreenY();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.strokeLine(x - canvas.getLayoutX(), y - canvas.getLayoutY(), cx - canvas.getLayoutX(), cy - canvas.getLayoutY());
        x = cx;
        y = cy;
    }

    @Override
    protected void release(MouseEvent event) {
        if (group != null) {
            group.setMouseTransparent(true);
        }
    }
}
