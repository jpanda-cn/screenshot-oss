package cn.jpanda.screenshot.oss.view.tray.handlers.rectangle;

import cn.jpanda.screenshot.oss.common.toolkit.DragRectangleEventHandler;
import cn.jpanda.screenshot.oss.service.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.CutInnerType;
import cn.jpanda.screenshot.oss.common.toolkit.RectangleAddTag2ResizeBinding;
import cn.jpanda.screenshot.oss.common.toolkit.RectangleBinding;
import cn.jpanda.screenshot.oss.view.tray.handlers.TrayConfig;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DrawRectangleInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    // 绘制的矩形
    private Rectangle currentRectangle;
    private Rectangle dragRectangle;
    // 矩形所属组
    private Group rectangleGroup;

    public DrawRectangleInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    protected void move(MouseEvent event) {
        rectangle.setCursor(Cursor.CROSSHAIR);
    }

    @Override
    protected void press(MouseEvent event) {
        // 鼠标按下时，清理之前生成的矩形组的事件
        clear();
        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.RECTANGLE);
        // 生成一个新的矩形
        currentRectangle = new Rectangle(0, 0);
        // 配置矩形颜色和宽度
        currentRectangle.strokeWidthProperty().bind(config.getStroke());
        currentRectangle.strokeProperty().bind(config.getStrokeColor());
        currentRectangle.setFill(Color.TRANSPARENT);
        // 记录鼠标开始节点
        x = event.getScreenX();
        y = event.getScreenY();
        currentRectangle.xProperty().set(x);
        currentRectangle.yProperty().set(y);
        rectangleGroup = new Group(currentRectangle);
        canvasProperties.getCutPane().getChildren().addAll(rectangleGroup);
    }

    @Override
    protected void drag(MouseEvent event) {
        // 获取鼠标偏移量
        double width = event.getScreenX() - x;
        double height = event.getScreenY() - y;
        // 限制矩形的大小不能超过截图区域
        currentRectangle.widthProperty().set(width);
        currentRectangle.heightProperty().set(height);

    }

    @Override
    protected void release(MouseEvent event) {
        // 矩形区域不得小于3px
        if (currentRectangle.widthProperty().get() < 3 || currentRectangle.heightProperty().get() < 3) {
            canvasProperties.getCutPane().getChildren().remove(rectangleGroup);
            return;
        }
        // 绑定拖动矩形和矩形的关系
        dragRectangle = RectangleBinding.doBind(rectangleGroup, currentRectangle);
        // 添加拖动事件
        dragRectangle.addEventFilter(MouseEvent.ANY, new DragRectangleEventHandler(dragRectangle, rectangle, null));
        // 添加变更大小事件
        new RectangleAddTag2ResizeBinding(dragRectangle, rectangle).bind();
        dragRectangle.requestFocus();
        dragRectangle.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                clear();
            }
        });
        canvasProperties.putGroup(rectangleGroup);
    }

    private void clear() {
        // 鼠标按下时，清理之前生成的矩形组的事件
        if (rectangleGroup != null) {
            rectangleGroup.setMouseTransparent(true);
            if (dragRectangle != null) {
                dragRectangle.visibleProperty().setValue(false);
                currentRectangle.strokeProperty().unbind();
                currentRectangle.strokeWidthProperty().unbind();
            }
        }
    }
}
