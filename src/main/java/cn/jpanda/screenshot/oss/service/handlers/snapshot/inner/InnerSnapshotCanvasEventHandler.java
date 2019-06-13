package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner;

import cn.jpanda.screenshot.oss.service.handlers.snapshot.AbstractSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;

/**
 * 截图区域内部处理器
 */
public abstract class InnerSnapshotCanvasEventHandler extends AbstractSnapshotCanvasEventHandler {

    public InnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

}
