package cn.jpanda.screenshot.oss.core.shotkey;

import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

public class MosaicScreenshotsElements extends DefaultGroupScreenshotsElements {
    private Path path;
    private Rectangle rectangle;
    private Integer mosaicRegionWidth;

    public MosaicScreenshotsElements(Group group, CanvasProperties canvasProperties, Path path, Rectangle rectangle, Integer mosaicRegionWidth) {
        super(group, canvasProperties);
        this.path = path;
        this.rectangle = rectangle;
        this.mosaicRegionWidth = mosaicRegionWidth;
    }

    @Override
    public Node getTopNode() {
        return path;
    }

    @Override
    public void active() {
        Bounds bounds = path.getLayoutBounds();
        updateColor((int) ((int) bounds.getMinX() - rectangle.getX()), (int) ((int) bounds.getMinY() - rectangle.getY()), (int) ((int) bounds.getMaxX() - rectangle.getX()), (int) ((int) bounds.getMaxY() - rectangle.getY()));

    }

    @Override
    public void destroy() {
        // 处理
        Bounds bounds = path.getLayoutBounds();
        recoverColor((int) ((int) bounds.getMinX() - rectangle.getX()), (int) ((int) bounds.getMinY() - rectangle.getY()), (int) ((int) bounds.getMaxX() - rectangle.getX()), (int) ((int) bounds.getMaxY() - rectangle.getY()));
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

    private void recoverColor(int x, int y, int ex, int ey) {
        // 特殊处理坐标
        x = x - x % mosaicRegionWidth;
        y = y - y % mosaicRegionWidth;
        for (int i = x; i < ex; i += mosaicRegionWidth) {
            for (int j = y; j < ey; j += mosaicRegionWidth) {
                recoverColor(new Point2D(i + rectangle.getX(), j + rectangle.getY())
                        , new Point2D(i + rectangle.getX() + mosaicRegionWidth - 1, j + rectangle.getY() + mosaicRegionWidth - 1)
                );
            }
        }
    }

    private void recoverColor(Point2D start, Point2D end) {
        for (int i = (int) start.getX(); i <= end.getX(); i++) {
            for (int j = (int) start.getY(); j <= end.getY(); j++) {
                if (i < 0 || j < 0 || i >= canvasProperties.getBackgroundImage().getWidth() || j >= canvasProperties.getBackgroundImage().getHeight()) {
                    // 超出边界不处理
                    continue;
                }
                canvasProperties.getBackgroundImage().getPixelWriter().setColor(i, j, canvasProperties.getComputerImage().getPixelReader().getColor(i, j));
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
