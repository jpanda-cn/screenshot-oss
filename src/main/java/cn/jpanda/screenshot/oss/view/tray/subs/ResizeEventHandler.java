package cn.jpanda.screenshot.oss.view.tray.subs;

import cn.jpanda.screenshot.oss.common.enums.ResizeType;
import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * 限制变更截图矩形大小
 */
public class ResizeEventHandler implements EventHandler<MouseEvent> {
    /**
     * 所属的父类矩形
     */
    protected Stage stage;
    protected Rectangle rectangle;
    /**
     * 首先的子节点
     */
    private ResizeType resizeType;
    protected double x;
    protected double y;
    protected double ox;
    protected double oy;
    protected double ow;
    protected double oh;

    public ResizeEventHandler(Stage stage, Rectangle rectangle) {
        this.stage = stage;
        this.rectangle = rectangle;
        bind();
    }

    public void bind() {
        ChangeListener<Number> up = (observable, oldValue, newValue) -> {
            rectangle.widthProperty().set(stage.getWidth());
            rectangle.heightProperty().set(stage.getHeight());
        };
        stage.widthProperty().addListener(up);
        stage.heightProperty().addListener(up);
    }

    @Override
    public void handle(MouseEvent event) {
        resize(event);
    }


    public void resize(MouseEvent event) {

        // 获取当前所有子节点
        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
            Cursor cursor = null;
            // 鼠标位置
            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();

            double stageWidth = stage.getWidth();
            double stageHeight = stage.getHeight();


            ox = stage.getX();
            oy = stage.getY();
            System.out.println(ox);
            System.out.println(oy);
            System.out.println(stageWidth);
            System.out.println(stageHeight);
            System.out.println(mouseX);
            System.out.println(mouseY);
            boolean onStartX = MathUtils.offset(mouseX, ox, 3);
            boolean onEndX = MathUtils.offset(mouseX, ox + stageWidth, 3);

            boolean onStartY = MathUtils.offset(mouseY, oy, 3);
            boolean onEndY = MathUtils.offset(mouseY, oy + stageHeight, 3);

            boolean onX = onStartX || onEndX;
            boolean onY = onStartY || onEndY;
            if (onStartX) {
                // left
                if (onStartY) {
                    cursor = Cursor.NW_RESIZE;
                    resizeType = ResizeType.NW_OPPOSITE;
                } else if (onEndY) {
                    cursor = Cursor.SW_RESIZE;
                    resizeType = ResizeType.SW_OPPOSITE;
                } else {
                    cursor = Cursor.H_RESIZE;
                    resizeType = ResizeType.W_CROSSWISE;
                }
            } else if (onEndX) {
                // right
                if (onStartY) {
                    cursor = Cursor.NE_RESIZE;
                    resizeType = ResizeType.NE_OPPOSITE;
                } else if (onEndY) {
                    cursor = Cursor.SE_RESIZE;
                    resizeType = ResizeType.SE_OPPOSITE;
                } else {
                    cursor = Cursor.H_RESIZE;
                    resizeType = ResizeType.E_CROSSWISE;
                }
            } else if (onY) {
                cursor = Cursor.V_RESIZE;
                resizeType = onStartY ? ResizeType.N_VERTICAL : ResizeType.S_VERTICAL;
            }
            if (cursor != null) {
                rectangle.setCursor(cursor);
            }
        } else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            // 鼠标开始节点
            x = event.getScreenX();
            y = event.getScreenY();


            ox = stage.getX();
            oy = stage.getY();
            ow = rectangle.widthProperty().get();
            oh = rectangle.heightProperty().get();
        } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            // 拖动
            // 获取需要移动的元素变更其展示位置
            double offsetX = event.getScreenX() - x;
            double offsetY = event.getScreenY() - y;
            // 判断是否修改宽度和x坐标

            if (resizeType.isAcross()) {
                // 只有当前鼠标坐标小于之前的x坐标时才会修改x坐标
                // 修改x坐标

                if (resizeType.isLeft()) {
                    // 右侧不变，变更x
                    // 判断使用鼠标坐标为x还是使用边界为x
                    double endx = ox + ow;
                    if (event.getScreenX() > endx) {
                        // 以endx作为x，同时宽度为鼠标-endx
                        stage.setX(endx);
                        stage.setWidth(event.getScreenX() - endx);
                    } else {
                        stage.setX(event.getScreenX());
                        stage.setWidth(endx - event.getScreenX());
                    }
                } else {
                    // 移动右侧
                    stage.setWidth(Math.abs(ow + offsetX));
                    stage.setX(MathUtils.min(event.getScreenX(), ox));
                }


                // 修改宽度，宽度等于老的宽度+偏移量

            }
            // 判断是否修改高度和y坐标
            if (resizeType.isVertical()) {
                if (resizeType.isTop()) {
                    double endy = oy + oh;
                    if (event.getScreenY() < endy) {
                        stage.setY(event.getScreenY());
                        stage.setHeight(endy - event.getScreenY());
                    } else {
                        stage.setY(endy);
                        stage.setHeight(event.getScreenY() - endy);
                    }
                } else {
                    stage.setY(MathUtils.min(event.getScreenY(), oy));
                    stage.setHeight(Math.abs(oh + offsetY));
                }

            }
            // 使用LimitRectangleEventHandler来修正数据
//            canvasDrawEventHandler.draw(new Bounds(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight()));
        }

    }

}
