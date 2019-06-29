package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner;

import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.arrow.ArrowInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.drag.LimitDragInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic.DotMatrixMosaicInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic.MosaicInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic.TypicalMosaicInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.pen.PathPenInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.rectangle.DrawRectangleInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.roundness.RoundnessInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.text.TextInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
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
    /**
     * 马赛克
     */
    private MosaicInnerSnapshotCanvasEventHandler mosaicInnerSnapshotCanvasEventHandler;

    public RouterInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
        dragSnapshotCanvasEventHandler = new LimitDragInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        roundnessInnerSnapshotCanvasEventHandler = new RoundnessInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        rectangleInnerSnapshotCanvasEventHandler = new DrawRectangleInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        arrowInnerSnapshotCanvasEventHandler = new ArrowInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        penInnerSnapshotCanvasEventHandler = new PathPenInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        textInnerSnapshotCanvasEventHandler = new TextInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        mosaicInnerSnapshotCanvasEventHandler = new DotMatrixMosaicInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
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
            case MOSAIC: {
                mosaicInnerSnapshotCanvasEventHandler.handle(event);
                break;
            }
            case RESIZE: {

                break;
            }
        }
    }
}
