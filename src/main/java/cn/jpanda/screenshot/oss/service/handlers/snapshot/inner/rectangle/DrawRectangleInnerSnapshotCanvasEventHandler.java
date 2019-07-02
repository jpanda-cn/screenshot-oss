package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.rectangle;

import cn.jpanda.screenshot.oss.common.toolkit.DragRectangleEventHandler;
import cn.jpanda.screenshot.oss.common.toolkit.RectangleAddTag2ResizeBinding;
import cn.jpanda.screenshot.oss.common.toolkit.RectangleBinding;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.core.shotkey.DefaultGroupScreenshotsElements;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.toolkits.TrayConfig;
import javafx.event.EventHandler;
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
    private RectangleAddTag2ResizeBinding rectangleAddTag2ResizeBinding;
    private EventHandler<MouseEvent> dragRectangleEventHandler;

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
        x = event.getSceneX();
        y = event.getSceneY();
        currentRectangle.xProperty().set(x);
        currentRectangle.yProperty().set(y);
        rectangleGroup = new Group(currentRectangle);
        canvasProperties.getScreenshotsElementsHolder().putEffectiveElement(new DefaultGroupScreenshotsElements(rectangleGroup, canvasProperties));
        canvasProperties.getCutPane().getChildren().addAll(rectangleGroup);
    }

    @Override
    protected void drag(MouseEvent event) {
        double cx = event.getSceneX();
        double cy = event.getSceneY();
        // 限制鼠标位置
        if (cx > rectangle.xProperty().add(rectangle.widthProperty()).get()) {
            cx = rectangle.xProperty().add(rectangle.widthProperty()).get();
        } else if (cx < rectangle.xProperty().get()) {
            cx = rectangle.xProperty().get();
        }
        if (cy > rectangle.yProperty().add(rectangle.heightProperty()).get()) {
            cy = rectangle.yProperty().add(rectangle.heightProperty()).get();
        } else if (cy < rectangle.yProperty().get()) {
            cy = rectangle.yProperty().get();
        }

        // 获取鼠标偏移量
        double width = cx - x;
        double height = cy - y;

        if (cx < x) {
            width = x - cx;
            currentRectangle.xProperty().set(cx);

        }
        if (cy < y) {
            height = y - cy;
            currentRectangle.yProperty().set(cy);

        }
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
        dragRectangleEventHandler = new DragRectangleEventHandler(dragRectangle, rectangle, null);
        dragRectangle.addEventFilter(MouseEvent.ANY, dragRectangleEventHandler);
        // 添加变更大小事件
        rectangleAddTag2ResizeBinding = new RectangleAddTag2ResizeBinding(dragRectangle, rectangle).bind();
        dragRectangle.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                clear();
            }
        });
    }

    private void clear() {
        canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class).set(() -> {
            // 鼠标按下时，清理之前生成的矩形组的事件
            if (rectangleGroup != null) {
                canvasProperties.putGroup(rectangleGroup);
                rectangleGroup.setMouseTransparent(true);
                if (dragRectangle != null) {
                    dragRectangle.visibleProperty().setValue(false);
                    currentRectangle.strokeProperty().unbind();
                    currentRectangle.strokeWidthProperty().unbind();
                    if (dragRectangleEventHandler != null) {
                        dragRectangle.removeEventFilter(MouseEvent.ANY, dragRectangleEventHandler);
                        dragRectangleEventHandler = null;
                    }
                    dragRectangle = null;
                }
            }
            if (rectangleAddTag2ResizeBinding != null) {
                rectangleAddTag2ResizeBinding.unbind();
                rectangleAddTag2ResizeBinding = null;
            }
        });
    }
}
