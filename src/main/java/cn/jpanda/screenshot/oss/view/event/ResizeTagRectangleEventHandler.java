package cn.jpanda.screenshot.oss.view.event;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.view.snapshot.handlers.ResizeType;
import cn.jpanda.screenshot.oss.view.tray.handlers.LimitRectangleEventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.util.List;


/**
 * 圆形调整大小以及拖动的事件处理器
 */
public class ResizeTagRectangleEventHandler extends LimitRectangleEventHandler {

    /**
     * 变化类型
     */
    private ResizeType resizeType;


    public ResizeTagRectangleEventHandler(ResizeType resizeType, Rectangle rectangle) {
        this(resizeType, rectangle, null);
    }

    public ResizeTagRectangleEventHandler(ResizeType resizeType, Rectangle rectangle, Rectangle parent) {
        this(resizeType, rectangle, parent, null);

    }

    public ResizeTagRectangleEventHandler(ResizeType resizeType, Rectangle rectangle, Rectangle parent, List<Rectangle> subs) {
        super(rectangle, parent, subs);
        this.resizeType = resizeType;

    }

    @Override
    protected void drag(MouseEvent event) {
        // 拖动
        // 获取鼠标偏移位置
        double offsetX = event.getScreenX() - x;
        double offsetY = event.getScreenY() - y;
        // 判断是否修改宽度和x坐标

        // 横向移动
        if (resizeType.isAcross()) {
            // 只有当前鼠标坐标小于之前的x坐标时才会修改x坐标
            // 修改x坐标

            if (resizeType.isLeft()) {
                // 右侧不变，变更x
                // 判断使用鼠标坐标为x还是使用边界为x
                double endx = ox + ow;
                if (event.getScreenX() > endx) {
                    // 以endx作为x，同时宽度为鼠标-endx
                    setRectangleX(endx);
                    setRectangleW(event.getScreenX() - endx);
                } else {
                    setRectangleX(event.getScreenX());
                    setRectangleW(endx - rectangle.xProperty().get());
                }
            } else {
                // 右侧
                double targetX = MathUtils.min(event.getScreenX(), ox);
                setRectangleX(targetX);
                // 如果此时x坐标不发生变更，表示碰到了边界
                if (targetX == rectangle.xProperty().get()) {
                    setRectangleW(Math.abs(ow + offsetX));
                }

            }


            // 修改宽度，宽度等于老的宽度+偏移量

        }
        // 判断是否修改高度和y坐标
        if (resizeType.isVertical()) {
            if (resizeType.isTop()) {
                double endy = oy + oh;
                if (event.getScreenY() < endy) {
                    setRectangleY(event.getScreenY());
                    setRectangleH(endy - rectangle.yProperty().get());
                } else {
                    setRectangleY(endy);
                    setRectangleH(event.getScreenY() - endy);
                }
            } else {
                double targetY = MathUtils.min(event.getScreenY(), oy);
                setRectangleY(targetY);
                if (targetY == rectangle.yProperty().get()) {
                    setRectangleH(Math.abs(oh + offsetY));

                }
            }
        }
    }


}
