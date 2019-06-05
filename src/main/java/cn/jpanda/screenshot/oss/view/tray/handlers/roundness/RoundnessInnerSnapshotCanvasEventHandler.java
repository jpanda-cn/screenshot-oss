package cn.jpanda.screenshot.oss.view.tray.handlers.roundness;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.service.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.handlers.rectangle.DragRectangleEventHandler;
import cn.jpanda.screenshot.oss.view.tray.handlers.EllipseRectangleBinding;
import cn.jpanda.screenshot.oss.view.tray.handlers.RectangleAddTag2ResizeBinding;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

/**
 * 绘制圆形
 */
public class RoundnessInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    private Ellipse ellipse;
    private Group ellipseGroup;
    private Rectangle outRectangle;

    public RoundnessInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);

    }

    @Override
    public void handle(MouseEvent event) {
        // 绘制圆形
        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
            // 变更截图区域的鼠标指针
            rectangle.setCursor(Cursor.CROSSHAIR);
        }
        if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            // 鼠标按下时，确认
            if (ellipseGroup != null) {
                ellipseGroup.setMouseTransparent(true);
                if (outRectangle != null) {
                    outRectangle.visibleProperty().setValue(false);
                }
            }
            ellipse = new Ellipse(0, 0, 0, 0);
            ellipse.setStroke(Color.RED);
            ellipse.setFill(Color.rgb(0, 0, 0, 0));
            // 记录鼠标开始节点
            x = event.getScreenX();
            y = event.getScreenY();
            ellipse.centerXProperty().set(x);
            ellipse.centerYProperty().set(y);
            ellipseGroup = new Group(ellipse);
            ellipseGroup.setPickOnBounds(true);
            canvasProperties.getCutPane().getChildren().addAll(ellipseGroup);

        } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            // 鼠标拖动事件,计算当前圆形
            // 获取子面板配置数据
            // 将事件转交给对应的事件处理器来处理
            double width = event.getScreenX() - x;
            double height = event.getScreenY() - y;
            // 宽度限制，宽度半径不得大于中心x，宽度半径不得大于宽度-中心x
            if (width <= MathUtils.min(x - canvasProperties.getCutRectangle().xProperty().get(), canvasProperties.getCutRectangle().xProperty().get() + canvasProperties.getCutRectangle().widthProperty().get() - x)) {
                ellipse.radiusXProperty().set(width);
            }
            if (height <= (MathUtils.min(y - canvasProperties.getCutRectangle().yProperty().get(), canvasProperties.getCutRectangle().yProperty().get() + canvasProperties.getCutRectangle().heightProperty().get() - y))) {
                ellipse.radiusYProperty().set(height);
            }

        } else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            if (ellipse.radiusXProperty().get() < 3 || ellipse.radiusYProperty().get() < 3) {
                canvasProperties.getCutPane().getChildren().remove(ellipseGroup);
                return;
            }
            // 圆形绘制完毕，为其创建一个隐藏的矩形区域，用于拖动和变更圆形的大小
            // 点击按钮发生在边界上，生成一个矩形框，框住矩形
            outRectangle = EllipseRectangleBinding.doBind(ellipseGroup, ellipse);
            // 为矩形添加六个标点，用来拖动变更矩形的大小
            new RectangleAddTag2ResizeBinding(outRectangle, rectangle).bind();
            // 圆形转矩形
            outRectangle.addEventFilter(MouseEvent.ANY, new DragRectangleEventHandler(outRectangle, rectangle, null));
            // 移除矩形内部事件
            // 为圆形添加事件，该事件确保在鼠标移动到圆形边界时，展示移动样式的鼠标，如果此时拖动，则移动该圆形的位置
        }
    }

}
