package cn.jpanda.screenshot.oss.service.snapshot.inner;

import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.handlers.arrow.ArrowInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.tray.handlers.drag.LimitDragInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.tray.handlers.pen.PathPenInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.tray.handlers.rectangle.DrawRectangleInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.tray.handlers.roundness.RoundnessInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.tray.handlers.text.TextInnerSnapshotCanvasEventHandler;
import javafx.scene.input.MouseEvent;

public class RouterInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    // 拖动
    private LimitDragInnerSnapshotCanvasEventHandler dragSnapshotCanvasEventHandler;
    /**
     * 绘制圆形
     */
    private RoundnessInnerSnapshotCanvasEventHandler roundnessInnerSnapshotCanvasEventHandler;

    /**
     * 绘制矩形
     */
    private DrawRectangleInnerSnapshotCanvasEventHandler rectangleInnerSnapshotCanvasEventHandler;

    /**
     * 绘制箭头
     */
    private ArrowInnerSnapshotCanvasEventHandler arrowInnerSnapshotCanvasEventHandler;
    /**
     * 画笔
     */
    private PathPenInnerSnapshotCanvasEventHandler penInnerSnapshotCanvasEventHandler;

    /**
     * 文字
     */
    private TextInnerSnapshotCanvasEventHandler textInnerSnapshotCanvasEventHandler;

    public RouterInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
        dragSnapshotCanvasEventHandler = new LimitDragInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        roundnessInnerSnapshotCanvasEventHandler = new RoundnessInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        rectangleInnerSnapshotCanvasEventHandler = new DrawRectangleInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        arrowInnerSnapshotCanvasEventHandler = new ArrowInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        penInnerSnapshotCanvasEventHandler = new PathPenInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        textInnerSnapshotCanvasEventHandler = new TextInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    public void handle(MouseEvent event) {
        switch (canvasProperties.getCutInnerType()) {
            case DRAG: {
                dragSnapshotCanvasEventHandler.handle(event);
                break;
            }
            case ROUNDNESS: {
                roundnessInnerSnapshotCanvasEventHandler.handle(event);
                break;
            }
            case RECTANGLE: {
                rectangleInnerSnapshotCanvasEventHandler.handle(event);
                break;
            }
            case ARROW: {
                arrowInnerSnapshotCanvasEventHandler.handle(event);
                break;
            }
            case PEN: {
                penInnerSnapshotCanvasEventHandler.handle(event);
                break;
            }
            case TEXT: {
                textInnerSnapshotCanvasEventHandler.handle(event);
                break;
            }
            case RESIZE: {

                break;
            }
        }
    }
}
