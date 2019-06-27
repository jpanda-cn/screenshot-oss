package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.core.ScreenshotsProcess;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.shotkey.DefaultGroupScreenshotsElements;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 典型的马赛克截图实现
 */
public class TypicalMosaicInnerSnapshotCanvasEventHandler extends MosaicInnerSnapshotCanvasEventHandler {
    /**
     * 马赛克的半径 实际直径为: mosaicRadius*2+1
     */
    private int mosaicRadius = 2;
    /**
     * 画笔宽度的一般，实际长度为 widthRadius*2+1
     */
    private int widthRadius = 4;
    private int width = widthRadius * 2 + 1;
    /**
     * 日志
     */
    private Log log;

    public TypicalMosaicInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
        log = canvasProperties.getConfiguration().getLogFactory().getLog(getClass());
    }

    @Override
    @SneakyThrows
    protected void press(MouseEvent event) {
        clear();
        // 获取鼠标当前位置
        x = event.getSceneX();
        y = event.getSceneY();
        // 理论上点击的时候就会直接生成一个马赛克
        if (rectangle.widthProperty().lessThan(mosaicRadius * 2 + 1).get()) {
            return;
        }
        if (rectangle.heightProperty().lessThan(mosaicRadius * 2 + 1).get()) {
            return;
        }
        // 判断xy是否处于边界上
        if (x - mosaicRadius < rectangle.xProperty().getValue()) {
            // 左侧不满足
            x = rectangle.getX() + mosaicRadius;
        }
        if (x + mosaicRadius > rectangle.xProperty().add(rectangle.widthProperty()).get()) {
            // 右侧不满足
            x = rectangle.xProperty().add(rectangle.widthProperty()).subtract(mosaicRadius).get();
        }
        // 处理高
        if (y - mosaicRadius < rectangle.getY()) {
            // 左侧不满足
            y = rectangle.getY() + mosaicRadius;
        }
        if (y + mosaicRadius > rectangle.yProperty().add(rectangle.heightProperty()).get()) {
            // 右侧不满足
            y = rectangle.yProperty().add(rectangle.heightProperty()).subtract(mosaicRadius).get();
        }

        // 获取截图区域的图片
        ScreenshotsProcess screenshotsProcess = canvasProperties.getConfiguration().getUniqueBean(ScreenshotsProcess.class);
        bufferedImage = screenshotsProcess.snapshot(canvasProperties.getCutPane().getScene(), rectangle);
        path = new Path();
        group = new Group(path);
        canvasProperties.getCutPane().getChildren().add(group);
        canvasProperties.getScreenshotsElementsHolder().putEffectiveElement(new DefaultGroupScreenshotsElements(group, canvasProperties));
        // 先生成一个马赛克
        path.strokeWidthProperty().set(width);
        path.getElements().add(new MoveTo(x, y));

    }

    @Override
    protected void drag(MouseEvent event) {
        // 获取当前鼠标坐标
        double cx = event.getSceneX();
        double cy = event.getSceneY();
        // 理论上点击的时候就会直接生成一个马赛克
        if (rectangle.widthProperty().lessThan(width).get()) {
            return;
        }
        if (rectangle.heightProperty().lessThan(width).get()) {
            return;
        }
        // 处理坐标
        // 判断xy是否处于边界上
        if (cx - mosaicRadius < rectangle.xProperty().getValue()) {
            // 左侧不满足
            cx = rectangle.getX() + mosaicRadius;
        }
        if (cx + mosaicRadius > rectangle.xProperty().add(rectangle.widthProperty()).get()) {
            // 右侧不满足
            cx = rectangle.xProperty().add(rectangle.widthProperty()).subtract(mosaicRadius).get();
        }
        // 处理高
        if (cy - mosaicRadius < rectangle.getY()) {
            // 左侧不满足
            cy = rectangle.getY() + mosaicRadius;
        }
        if (cy + mosaicRadius > rectangle.yProperty().add(rectangle.heightProperty()).get()) {
            // 右侧不满足
            cy = rectangle.yProperty().add(rectangle.heightProperty()).subtract(mosaicRadius).get();
        }

        // 画笔半径
        // 只处理操作一个马赛克的坐标
        if (MathUtils.subAbs(cx, x) >= width || MathUtils.subAbs(cy, y) >= width) {
            // 计算需要画几次
            // 获取宽度
            computerPath(x, y, cx, cy);

            x = cx + widthRadius;
            y = cy + widthRadius;
        }

    }

    protected void computerPath(double x, double y, double ex, double ey) {
        // 拆分成具体的mosaic元素

        // 获取起始坐标 ，使用起始坐标和目标坐标，生成一个闭合图形
        // 获取x和ex之间的差 y和ey之间的差 ，此处默认认为其构成的是一个矩形
        // 判断马赛克移动的方向
        boolean toTop = ey < y; // 往上走
        boolean toLeft = ex < x; // 往左走

        // 计算出mosaic的数量 x/y偏移量大的决定了mosaic的数量
        double maxOffset = MathUtils.max(MathUtils.subAbs(ex, x), MathUtils.subAbs(ey, y));

        // 通过最大偏移量计算出mosaic的数量
        int mosaicNumber = (int) (maxOffset % width == 0 ? maxOffset / width : maxOffset / width + 1);

        // mosaic的数量决定了每一次新的马赛克的x、y偏移量
        double offsetX = MathUtils.subAbs(ex, x) / mosaicNumber; // x偏移量
        double offsetY = MathUtils.subAbs(ey, y) / mosaicNumber; // y偏移量

        // 计算需要多少个offsetX才会大于等于一个格
        int minx = (int) MathUtils.min(x, ex);
        int miny = (int) MathUtils.min(y, ey);
        minx = minx - rectangle.xProperty().intValue();
        miny = miny - rectangle.yProperty().intValue();

        // 生成mosaic元素
        for (int i = 0; i < mosaicNumber; i++) {

            // 将图片场景传入方法获取到一个组装成mosaic的图片
            // 绘制mosaic
            drawOneMosaic(minx, miny);
            // 刷新 新的马赛克元素
            path = new Path();
            path.strokeWidthProperty().set(width);
            group.getChildren().add(path);

            // 处理
            if (toTop) {
                miny -= offsetY;
            } else {
                miny += offsetY;
            }
            if (toLeft) {
                minx -= offsetX;
            } else {
                minx += offsetX;
            }
            path.getElements().add(new MoveTo(minx + rectangle.getX(), miny + rectangle.getY()));
        }


    }


    @SneakyThrows
    private void drawOneMosaic(int x, int y) {
        // 获取这个点对应的图片
        BufferedImage mosaicImage = bufferedImage.getSubimage(x - widthRadius, y - widthRadius, width, width);
        Graphics2D graphics = mosaicImage.createGraphics();
        graphics.setColor(Color.RED);
        graphics.fillRect(mosaicImage.getMinX(), mosaicImage.getMinY(), mosaicImage.getWidth(), mosaicImage.getHeight());
        graphics.dispose();
//        canvasProperties.getCutPane().getChildren().add(new ImageView(SwingFXUtils.toFXImage(mosaicImage, null)));
//
//        for (int i = 0; i < mosaicImage.getWidth(); i += (mosaicRadius * 2 + 1)) {
//            for (int j = 0; j < mosaicImage.getHeight(); j += (mosaicRadius * 2 + 1)) {
//                // 每一次都是一个mosaic
//                // 获取当前mosaic中心点对应的颜色值
//                if (i + mosaicRadius > bufferedImage.getWidth() || j + mosaicRadius > bufferedImage.getHeight()) {
//                    continue;
//                }
//                int rgb = mosaicImage.getRGB(i, j);
//                // 设置颜色
//                for (int k = 0; k < (mosaicRadius * 2 + 1); k++) {
//                    for (int l = 0; l < (mosaicRadius * 2 + 1); l++) {
//                        mosaicImage.setRGB(i + k, j + l, rgb);
//                    }
//                }
//            }
//        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(mosaicImage, "png", os);
        Image image = new Image(new ByteArrayInputStream(os.toByteArray()), width, width, false, true);
        path.strokeProperty().setValue(new ImagePattern(image, 0, 0, width, width, false));
        path.getElements().add(new LineTo(x + rectangle.getX(), y + rectangle.getY()));

    }
}
