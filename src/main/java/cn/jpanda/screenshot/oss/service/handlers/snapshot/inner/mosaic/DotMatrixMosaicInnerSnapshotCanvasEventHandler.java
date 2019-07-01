package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic;

import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.toolkits.TrayConfig;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * 点阵mosaic实现
 * 亟需优化
 */
public class DotMatrixMosaicInnerSnapshotCanvasEventHandler extends MosaicInnerSnapshotCanvasEventHandler {

    /**
     * 马赛克区域的一半宽度
     */
    protected static final Integer mosaicRegionRadius = 2;
    /**
     * 马赛克区域的宽度
     */
    protected Integer mosaicRegionWidth;

    protected WritableImage computerImage;

    public DotMatrixMosaicInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
        computerImage = canvasProperties.getBackgroundImage();
    }

    @Override
    protected void press(MouseEvent event) {
        super.press(event);
        // 确定马赛克大小
        TrayConfig trayConfig = canvasProperties.getTrayConfig(CutInnerType.MOSAIC);
        int ratio = trayConfig.getStroke().intValue();
        mosaicRegionWidth = mosaicRegionRadius * ratio + 1;
    }

    @Override
    protected void drag(MouseEvent event) {
        // 设置mosaic的半径
        if (!rectangle.contains(event.getSceneX(), event.getSceneY())) {
            return;
        }
        path.strokeWidthProperty().set(mosaicRegionWidth*2);
        path.opacityProperty().set(0);
        path.getElements().add(new LineTo(event.getSceneX(), event.getSceneY()));
        // 处理
        Bounds bounds = path.getLayoutBounds();
        updateColor((int) ((int) bounds.getMinX() - rectangle.getX()), (int) ((int) bounds.getMinY() - rectangle.getY()), (int) ((int) bounds.getMaxX() - rectangle.getX()), (int) ((int) bounds.getMaxY() - rectangle.getY()));
        path = new Path();
        group.getChildren().add(path);
        path.visibleProperty().set(false);
        path.toBack();
        path.getElements().add(new MoveTo(event.getSceneX(), event.getSceneY()));
        x = event.getSceneX();
        y = event.getSceneY();
    }


    private void updateColor(int x, int y, int ex, int ey) {
        // 特殊处理坐标
        x = x - x % mosaicRegionWidth;
        y = y - y % mosaicRegionWidth;
        for (int i = x; i < ex; i += mosaicRegionWidth) {
            for (int j = y; j < ey; j += mosaicRegionWidth) {
                updateColor(new Point2D(i + rectangle.getX(), j + rectangle.getY())
                        , new Point2D(i + rectangle.getX() + mosaicRegionWidth - 1, j + rectangle.getY() + mosaicRegionWidth - 1)
                );
            }
        }
    }

    private void updateColor(Point2D start, Point2D end) {
        Color color = getRegionColor(start, end);
        for (int i = (int) start.getX(); i <= end.getX(); i++) {
            for (int j = (int) start.getY(); j <= end.getY(); j++) {
                if (i < 0 || j < 0 || i >= canvasProperties.getBackgroundImage().getWidth() || j >= canvasProperties.getBackgroundImage().getHeight()) {
                    // 超出边界不处理
                    continue;
                }
                canvasProperties.getBackgroundImage().getPixelWriter().setColor(i, j, color);
            }
        }
    }

    /**
     * 获取指定区域内的颜色
     *
     * @param start 开始坐标
     * @param end   结束坐标
     */
    protected Color getRegionColor(Point2D start, Point2D end) {
        // 获取开始坐标和结束坐标之间所有的颜色数据
        // 筛选出颜色最深的
        Color result = Color.WHITE;
        Map<Color, Integer> colorCount = new HashMap<>();
        for (int i = (int) (start.getX()); i < end.getX(); i++) {
            for (int j = (int) (start.getY()); j < end.getY(); j++) {
                if (i < 0 || j < 0 || i >= canvasProperties.getBackgroundImage().getWidth() || j >= canvasProperties.getBackgroundImage().getHeight()) {
                    // 超出边界不处理
                    continue;
                }
                Color color = canvasProperties.getComputerImage().getPixelReader().getColor(i, j);
                if (color.equals(Color.WHITE)) {
                    continue;
                }
                Integer count = colorCount.getOrDefault(color, 0);
                colorCount.put(color, ++count);
            }
        }
        int maxCount = 0;
        for (Map.Entry<Color, Integer> ci : colorCount.entrySet()) {
            if (ci.getValue() > maxCount) {
                result = ci.getKey();
                maxCount = ci.getValue();
            }
        }
        return result;
    }


}
