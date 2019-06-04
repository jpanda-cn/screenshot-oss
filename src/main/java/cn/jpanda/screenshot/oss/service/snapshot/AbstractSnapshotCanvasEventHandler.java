package cn.jpanda.screenshot.oss.service.snapshot;

import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public abstract class AbstractSnapshotCanvasEventHandler extends SnapshotCanvasEventHandler {
    protected double x;
    protected double y;
    protected double ox;
    protected double oy;
    protected CanvasProperties canvasProperties;
    protected Rectangle rectangle;
    protected CanvasDrawEventHandler canvasDrawEventHandler;

    public AbstractSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        this.canvasProperties = canvasProperties;
        this.canvasDrawEventHandler = canvasDrawEventHandler;
        rectangle = canvasProperties.getCutRectangle();
        ox = rectangle.xProperty().get();
        ox = rectangle.yProperty().get();
    }
}
