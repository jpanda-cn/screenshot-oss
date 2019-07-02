package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;

/**
 * 绑定工具托盘和截图区域的数据
 * 根据截图区域的变化动态展示
 */
public class ExternalComponentBinders {
    private Rectangle cutRec;
    private Parent toolbar;

    public ExternalComponentBinders(Rectangle cutRec, Parent toolbar) {
        this.cutRec = cutRec;
        this.toolbar = toolbar;

    }

    public ExternalComponentBinders doRegistry() {
        toolbar.visibleProperty().bind(cutRec.visibleProperty());
        // 绑定数据
        cutRec.yProperty().addListener((observable, oldValue, newValue) -> {
            response();
        });
        cutRec.xProperty().addListener((observable, oldValue, newValue) -> {
            response();
        });
        cutRec.widthProperty().addListener((observable, oldValue, newValue) -> {
            response();
        });
        cutRec.heightProperty().addListener((observable, oldValue, newValue) -> {
            response();
        });
        return this;
    }

    public void unbind() {
        toolbar.visibleProperty().unbind();
        cutRec.yProperty().unbind();
        cutRec.xProperty().unbind();
        cutRec.widthProperty().unbind();
        cutRec.heightProperty().unbind();
        toolbar = null;
        cutRec = null;
    }

    public void response() {
        // 展示位置依次是外部右测下方，外部下右，外部下左，外部左下，外部上右，外部上左
        // 内部下右
        // 判断内部展示还是外部展示
        Window window = cutRec.getScene().getWindow();
        double y = cutRec.yProperty().getValue();
        double h = cutRec.heightProperty().get();
        double x = cutRec.xProperty().get();
        double w = cutRec.widthProperty().get();
        // 截图区域的左下角 x
        double endX = x + w;
        // 截图区域的左下角 y
        double endY = y + h;
        // 屏幕的高度
        double windowEndY = window.getY() + window.getHeight();
        // 屏幕的宽度
        double windowEndX = window.getX() + window.getWidth();

        Bounds toolBarBounds = toolbar.layoutBoundsProperty().get();
        double toolW = toolBarBounds.getWidth();
        double toolH = toolBarBounds.getHeight();
        // 工具无法放置在截图区域下方，同时无法放在截图区域上方
        boolean unableVertical = toolH + endY > windowEndY && toolH > y;
        // 工具无法放置在截图区域左侧，同时无法放在截图区域右侧
        boolean unableAcross = toolW > x && toolW + endX > windowEndX;
        // 工具无法放在截图区域外
        boolean isInner = unableVertical && unableAcross;
        if (isInner) {
            // 内部展示 内部下右,不考虑高度不够
            toolbar.layoutYProperty().set(endY - toolH);
            toolbar.layoutXProperty().set(endX - toolW);
        } else {
            if (!unableVertical) {
                // 垂直可以放
                if (toolW < w) {
                    // 工具宽度小于截图区域
                    toolbar.layoutXProperty().set(endX - toolW);
                } else if (toolW == w) {
                    // 工具的宽度等于截图区域的宽度
                    toolbar.layoutXProperty().set(x);
                } else {
                    // 工具的宽度大于截图区域的宽度
                    // 这里有两种场景,一种是在右侧，一种是在左侧
                    if (endX - toolW < 0) {
                        // 在左侧长度不够
                        toolbar.layoutXProperty().set(window.xProperty().getValue());
                    } else {
                        toolbar.layoutXProperty().set(endX - toolW);
                    }
                }

                if (toolH + endY <= windowEndY) {
                    toolbar.layoutYProperty().set(endY);
                } else {
                    toolbar.layoutYProperty().set(y - toolH);
                }
            } else {
                // 左右位置够用
                // 先右后左
                toolbar.layoutYProperty().set(endY - toolH);
                if (toolW + endX < windowEndX) {
                    toolbar.layoutXProperty().set(endX);
                } else {
                    toolbar.layoutXProperty().set(x - toolW);
                }
            }
        }
    }
}
