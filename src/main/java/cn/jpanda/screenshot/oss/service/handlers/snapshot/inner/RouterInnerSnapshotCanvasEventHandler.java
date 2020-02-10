package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner;

import cn.jpanda.screenshot.oss.core.shotkey.shortcut.CanvasShortcutManager;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.arrow.ArrowInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.drag.LimitDragInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic.DotMatrixMosaicInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic.MosaicInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.pen.PathPenInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.rectangle.DrawRectangleInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.rgb.RgbInnerSnapshotCanvasEventHandler;
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

    /**
     * RGB
     */
    private RgbInnerSnapshotCanvasEventHandler rgbInnerSnapshotCanvasEventHandler;

    public RouterInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler, CanvasShortcutManager canvasShortcutManager) {
        super(canvasProperties, canvasDrawEventHandler,canvasShortcutManager);
        dragSnapshotCanvasEventHandler = new LimitDragInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler,canvasShortcutManager);
        roundnessInnerSnapshotCanvasEventHandler = new RoundnessInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler,canvasShortcutManager);
        rectangleInnerSnapshotCanvasEventHandler = new DrawRectangleInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler,canvasShortcutManager);
        arrowInnerSnapshotCanvasEventHandler = new ArrowInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler,canvasShortcutManager);
        penInnerSnapshotCanvasEventHandler = new PathPenInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler,canvasShortcutManager);
        textInnerSnapshotCanvasEventHandler = new TextInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler,canvasShortcutManager);
        mosaicInnerSnapshotCanvasEventHandler = new DotMatrixMosaicInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler,canvasShortcutManager);
        rgbInnerSnapshotCanvasEventHandler = new RgbInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler, canvasShortcutManager);
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
            case RGB: {
                rgbInnerSnapshotCanvasEventHandler.handle(event);
                break;
            }
            case RESIZE: {

                break;
            }
            default:{
                break;
            }
        }
    }
}
