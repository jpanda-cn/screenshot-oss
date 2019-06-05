package cn.jpanda.screenshot.oss.service.snapshot.inner;

import cn.jpanda.screenshot.oss.service.snapshot.AbstractSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.scene.input.MouseEvent;

/**
 * 截图区域内部处理器
 */
public abstract class InnerSnapshotCanvasEventHandler extends AbstractSnapshotCanvasEventHandler {

    public InnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

}
