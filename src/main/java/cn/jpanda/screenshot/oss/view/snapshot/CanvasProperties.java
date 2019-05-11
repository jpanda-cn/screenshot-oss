package cn.jpanda.screenshot.oss.view.snapshot;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import lombok.Data;

@Data
public class CanvasProperties {
    /**
     * 全局的画布对象
     */
    private GraphicsContext globalGraphicsContext;
    /**
     * 用户已选中的截图区域
     */
    private Rectangle cutRectangle;

    public CanvasProperties(GraphicsContext globalGraphicsContext, Rectangle cutRectangle) {
        this.globalGraphicsContext = globalGraphicsContext;
        this.cutRectangle = cutRectangle;
    }
}
