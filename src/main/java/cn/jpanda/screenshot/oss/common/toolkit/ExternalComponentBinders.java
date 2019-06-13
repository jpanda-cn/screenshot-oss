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

    public void doRegistry() {
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
        double endX = x + w;
        double endY = y + h;
        double windowEndY = window.getY() + window.getHeight();
        double windowEndX = window.getX() + window.getWidth();
        Bounds toolBarBounds = toolbar.layoutBoundsProperty().get();
        double toolW = toolBarBounds.getWidth();
        double toolH = toolBarBounds.getHeight();
        boolean unableVertical = toolH + endY > windowEndY && toolH > y;
        boolean unableAcross = toolW > x && toolW + endX > windowEndX;
        boolean isInner = unableVertical && unableAcross;
        if (isInner) {
            // 内部展示 内部下右,不考虑高度不够
            toolbar.layoutYProperty().set(endY - toolH);
            toolbar.layoutXProperty().set(endX - toolW);
        } else {
            if (!unableVertical) {
                if (toolW < w) {
                    // 截图区域大于工具宽度
                    toolbar.layoutXProperty().set(endX - toolW);
                } else {
                    toolbar.layoutXProperty().set(x);
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
