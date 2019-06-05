package cn.jpanda.screenshot.oss.service.snapshot;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogHolder;
import cn.jpanda.screenshot.oss.service.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.service.snapshot.inner.RouterInnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.scene.input.MouseEvent;

public class RoutingSnapshotCanvasEventHandler extends AbstractSnapshotCanvasEventHandler {
    private InnerSnapshotCanvasEventHandler inner;
    private AbstractSnapshotCanvasEventHandler resize;
    private boolean onEdge = false;
    private Log log = LogHolder.getInstance().getLogFactory().getLog(getClass());

    public RoutingSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
        inner = new RouterInnerSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
        resize = new ResizeSnapshotCanvasEventHandler(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    public void handle(MouseEvent event) {
        // 鼠标移动的时，外部和边缘均在此处处理，
        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
            // 判断如何展示
            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();
            ox = rectangle.xProperty().get();
            oy = rectangle.yProperty().get();

            boolean onStartX = MathUtils.offset(mouseX, ox, 3);
            boolean onEndX = MathUtils.offset(mouseX, ox + rectangle.getWidth(), 3);

            boolean onStartY = MathUtils.offset(mouseY, oy, 3);
            boolean onEndY = MathUtils.offset(mouseY, oy + rectangle.heightProperty().get(), 3);

            boolean onX = onStartX || onEndX;
            boolean onY = onStartY || onEndY;
            onEdge = onX || onY;
        }
        // 判断当前是否是在边缘
        if (onEdge) {
            resize.handle(event);
        } else {
            inner.handle(event);
        }
    }
}
