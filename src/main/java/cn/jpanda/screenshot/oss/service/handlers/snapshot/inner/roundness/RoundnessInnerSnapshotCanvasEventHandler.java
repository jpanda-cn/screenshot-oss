package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.roundness;

import cn.jpanda.screenshot.oss.common.toolkit.DragRectangleEventHandler;
import cn.jpanda.screenshot.oss.common.toolkit.EllipseRectangleBinding;
import cn.jpanda.screenshot.oss.common.toolkit.RectangleAddTag2ResizeBinding;
import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.toolkits.TrayConfig;
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
    protected void move(MouseEvent event) {
        // 变更截图区域的鼠标指针
        rectangle.setCursor(Cursor.CROSSHAIR);
    }

    @Override
    protected void press(MouseEvent event) {
        // 鼠标按下时，确认
        clear();
        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.ROUNDNESS);
        ellipse = new Ellipse(0, 0, 0, 0);
        ellipse.setFill(Color.rgb(0, 0, 0, 0));
        ellipse.strokeProperty().bind(config.getStrokeColor());
        ellipse.strokeWidthProperty().bind(config.getStroke());
        // 记录鼠标开始节点
        x = event.getSceneX();
        y = event.getSceneY();
        ellipse.centerXProperty().set(x);
        ellipse.centerYProperty().set(y);
        ellipseGroup = new Group(ellipse);
        ellipseGroup.setPickOnBounds(true);
        canvasProperties.getCutPane().getChildren().addAll(ellipseGroup);

    }

    @Override
    protected void drag(MouseEvent event) {
        // 鼠标拖动事件,计算当前圆形
        // 获取子面板配置数据
        // 将事件转交给对应的事件处理器来处理
        // 判断当前鼠标位置是否需要特殊处理
        // 【圆心 + 宽 】超过起始边界或者结束边界


        double width = MathUtils.subAbs(event.getSceneX(), x);
        double height = MathUtils.subAbs(event.getSceneY(), y);
        // 宽度限制，宽度半径不得大于中心x，宽度半径不得大于宽度-中心x
        if (event.isShiftDown()) {
            width = height = MathUtils.max(width, height);
        }
        if (width <= MathUtils.min(x - canvasProperties.getCutRectangle().xProperty().get(), canvasProperties.getCutRectangle().xProperty().get() + canvasProperties.getCutRectangle().widthProperty().get() - x)) {
            ellipse.radiusXProperty().set(width);
        }
        if (height <= (MathUtils.min(y - canvasProperties.getCutRectangle().yProperty().get(), canvasProperties.getCutRectangle().yProperty().get() + canvasProperties.getCutRectangle().heightProperty().get() - y))) {
            ellipse.radiusYProperty().set(height);
        }
    }

    @Override
    protected void release(MouseEvent event) {
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
        outRectangle.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                clear();
            }
        });
        canvasProperties.putGroup(ellipseGroup);
        // 移除矩形内部事件
        // 为圆形添加事件，该事件确保在鼠标移动到圆形边界时，展示移动样式的鼠标，如果此时拖动，则移动该圆形的位置
    }

    private void clear() {
        canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class).set(() -> {
            // 鼠标按下时，清理之前生成的矩形组的事件
            if (ellipseGroup != null) {
                ellipseGroup.setMouseTransparent(true);
                if (outRectangle != null) {
                    outRectangle.visibleProperty().setValue(false);
                    ellipse.strokeProperty().unbind();
                    ellipse.strokeWidthProperty().unbind();
                }
            }
        });
    }
}
