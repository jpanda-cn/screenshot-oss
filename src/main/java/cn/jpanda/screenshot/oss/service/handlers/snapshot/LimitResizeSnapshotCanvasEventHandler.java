package cn.jpanda.screenshot.oss.service.handlers.snapshot;

import cn.jpanda.screenshot.oss.common.toolkit.ShapeCovertHelper;
import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.common.toolkit.Bounds;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.common.enums.ResizeType;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Optional;

/**
 * 限制变更截图矩形大小
 */
public class LimitResizeSnapshotCanvasEventHandler extends AbstractSnapshotCanvasEventHandler {
    /**
     * 所属的父类矩形
     */
    protected Rectangle parent;

    /**
     * 首先的子节点
     */
    protected List<Rectangle> subs;
    private ResizeType resizeType;
    protected double x;
    protected double y;
    protected double ox;
    protected double oy;
    protected double ow;
    protected double oh;

    public LimitResizeSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    public void handle(MouseEvent event) {
        resize(event);
    }


    public void resize(MouseEvent event) {
        // 获取当前所有子节点
        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
            // 判断如何展示
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            Cursor cursor = null;
            ox = rectangle.xProperty().get();
            oy = rectangle.yProperty().get();

            boolean onStartX = MathUtils.offset(mouseX, ox, 3);
            boolean onEndX = MathUtils.offset(mouseX, ox + rectangle.getWidth(), 3);

            boolean onStartY = MathUtils.offset(mouseY, oy, 3);
            boolean onEndY = MathUtils.offset(mouseY, oy + rectangle.heightProperty().get(), 3);

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
            subs = ShapeCovertHelper.toRectangles(canvasProperties.listGroups());
            // 鼠标开始节点
            x = event.getSceneX();
            // 鼠标结束节点
            y = event.getSceneY();
            ox = rectangle.xProperty().get();
            oy = rectangle.yProperty().get();
            ow = rectangle.widthProperty().get();
            oh = rectangle.heightProperty().get();
        } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            // 拖动
            // 获取需要移动的元素变更其展示位置
            double offsetX = event.getSceneX() - x;
            double offsetY = event.getSceneY() - y;
            // 判断是否修改宽度和x坐标

            if (resizeType.isAcross()) {
                // 只有当前鼠标坐标小于之前的x坐标时才会修改x坐标
                // 修改x坐标

                if (resizeType.isLeft()) {
                    // 右侧不变，变更x
                    // 判断使用鼠标坐标为x还是使用边界为x
                    double endx = ox + ow;
                    if (event.getSceneX() > endx) {
                        // 以endx作为x，同时宽度为鼠标-endx
                        if (setRectangleX(endx)) {
                            setRectangleW(event.getSceneX() - endx);
                        }
                    } else {
                        if (setRectangleX(event.getSceneX())) {
                            setRectangleW(endx - event.getSceneX());
                        }
                    }
                } else {
                    // 移动右侧
                    if (setRectangleW(Math.abs(ow + offsetX))) {
                        setRectangleX(MathUtils.min(event.getSceneX(), ox));
                    }
                }


                // 修改宽度，宽度等于老的宽度+偏移量

            }
            // 判断是否修改高度和y坐标
            if (resizeType.isVertical()) {
                if (resizeType.isTop()) {
                    double endy = oy + oh;
                    if (event.getSceneY() < endy) {
                        if (setRectangleY(event.getSceneY())) {
                            setRectangleH(endy - event.getSceneY());
                        }
                    } else {
                        if (setRectangleY(endy)) {
                            setRectangleH(event.getSceneY() - endy);
                        }
                    }
                } else {
                    if (setRectangleH(Math.abs(oh + offsetY))) {
                        setRectangleY(MathUtils.min(event.getSceneY(), oy));
                    }
                }

            }
            // 使用LimitRectangleEventHandler来修正数据
            canvasDrawEventHandler.draw(new Bounds(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight()));
        }

    }



    protected boolean setRectangleX(double x) {
        if (checkInParentX(x) && checkOutSubsX(x)) {
            rectangle.xProperty().set(x);
            return true;
        }
        return false;
    }

    protected boolean setRectangleY(double y) {
        if (checkInParentY(y) && checkOutSubsY(y)) {
            rectangle.yProperty().set(y);
            return true;
        }
        return false;
    }

    protected boolean setRectangleW(double w) {
        if (checkInParentW(w) && checkOutSubsW(w)) {
            rectangle.widthProperty().set(w);
            return true;
        }
        return false;
    }

    protected boolean setRectangleH(double h) {
        if (checkInParentH(h) && checkOutSubsH(h)) {
            rectangle.heightProperty().set(h);
            return true;
        }
        return false;
    }

    protected boolean checkInParentX(double x) {
        if (parent == null) {
            return true;
        }
        return x >= parent.xProperty().get() && x <= parent.xProperty().add(parent.widthProperty()).subtract(rectangle.widthProperty()).get();
    }

    protected boolean checkOutSubsX(double x) {
        if (null == subs || subs.isEmpty()) {
            return true;
        }
        Optional<Rectangle> minX = subs.stream().min((a, b) -> a.xProperty().subtract(b.xProperty()).intValue());
        // 校验最小X
        return x <= minX.get().xProperty().get();
    }

    protected boolean checkInParentY(double y) {
        if (parent == null) {
            return true;
        }
        return y >= parent.yProperty().get() && y <= parent.yProperty().add(parent.heightProperty()).subtract(rectangle.heightProperty()).get();
    }

    protected boolean checkOutSubsY(double y) {
        if (null == subs || subs.isEmpty()) {
            return true;
        }
        Optional<Rectangle> minY = subs.stream().min((a, b) -> a.yProperty().subtract(b.yProperty()).intValue());
        // 校验最小X
        return y <= minY.get().yProperty().get();
    }

    protected boolean checkInParentW(double w) {
        if (parent == null) {
            return true;
        }
        // 校验宽度
        return rectangle.xProperty().add(w).get() <= parent.xProperty().add(parent.widthProperty()).get();
    }

    protected boolean checkOutSubsW(double w) {
        if (null == subs || subs.isEmpty()) {
            return true;
        }
        Optional<Rectangle> maxEndX = subs.stream().max((a, b) -> a.xProperty().add(a.widthProperty()).subtract(b.xProperty().add(b.widthProperty())).intValue());
        // 校验最大X
        double recEx = rectangle.xProperty().add(w).get();
        double maxEx = maxEndX.get().xProperty().add(maxEndX.get().widthProperty()).get();
        return recEx >= maxEx;
    }

    protected boolean checkInParentH(double h) {
        if (parent == null) {
            return true;
        }
        // 校验高度
        return rectangle.yProperty().add(h).get() <= parent.yProperty().add(parent.heightProperty()).get();
    }

    protected boolean checkOutSubsH(double h) {
        if (null == subs || subs.isEmpty()) {
            return true;
        }
        Optional<Rectangle> maxEndY = subs.stream().max((a, b) -> a.yProperty().add(a.heightProperty()).subtract(b.yProperty().add(b.heightProperty())).intValue());
        // 校验最大X
        double recEy = rectangle.yProperty().add(h).get();
        double maxEy = maxEndY.get().yProperty().add(maxEndY.get().heightProperty()).get();
        return recEy >= maxEy;
    }
}
