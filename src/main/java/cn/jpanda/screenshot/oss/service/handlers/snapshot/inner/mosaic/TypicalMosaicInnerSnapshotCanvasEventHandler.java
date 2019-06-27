package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 典型的马赛克截图实现
 */
public class TypicalMosaicInnerSnapshotCanvasEventHandler extends MosaicInnerSnapshotCanvasEventHandler {
    private int mosaicRadius = 33;
    private int width = 33;
    private Log log;

    public TypicalMosaicInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
        log = canvasProperties.getConfiguration().getLogFactory().getLog(getClass());
    }

    @Override
    protected void drag(MouseEvent event) {
        // 获取当前鼠标坐标
        double cx = event.getSceneX();
        double cy = event.getSceneY();
        // 画笔半径
        // 按照每33个像素的标准生成
        if (MathUtils.subAbs(cx, x) >= width || MathUtils.subAbs(cy, y) >= width) {
            // 计算需要画几次
            // 获取宽度
            computerPath(x, y, cx, cy);
            x = cx+width;
            y = cy;
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
        double minx = MathUtils.min(x, ex);
        double miny = MathUtils.min(y, ey);
        minx = (int) minx - rectangle.getX();
        miny = (int) miny - rectangle.getY();
        // 生成mosaic元素
        for (int i = 0; i < mosaicNumber; i++) {

            // 获取当前mosaic区域对应的图片场景
            BufferedImage tmp = bufferedImage.getSubimage((int) minx-width/2 , (int) miny-width/2 , width, width);
            // TODO 展示出获取到的图片内容
            canvasProperties.getCutPane().getChildren().add(new ImageView(SwingFXUtils.toFXImage(tmp, null)));
            // 将图片场景传入方法获取到一个组装成mosaic的图片
            // 绘制mosaic
            doDrawMosaic(minx, miny, tmp);
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

        }


    }

    /**
     * 在指定节点上绘制马赛克
     */
    protected void doDrawMosaic(double x, double y, BufferedImage bufferedImage) {
        // 将bufferedImage转换为mosaic

        // 一次只能绘制一个马赛克
        path.strokeWidthProperty().set(width);
        path.strokeProperty().setValue(toMosaic(bufferedImage));
        // 绘制
        PathElement line = new LineTo(x + rectangle.getX(), y + rectangle.getY());
        path.getElements().add(line);

        // 刷新 新的马赛克元素
        path = new Path();
        group.getChildren().add(path);
        path.getElements().add(new MoveTo(x + rectangle.getX(), y + rectangle.getY()));
    }

    @SneakyThrows
    private ImagePattern toMosaic(BufferedImage bufferedImage) {
        // 默认mosaic的宽度为11
        // 取中间作为整个区域的颜色
        //  开始执行马赛克元素绘制操作
//        for (int i = 0; i < bufferedImage.getWidth(); i += mosaicRadius) {
//            for (int j = 0; j < bufferedImage.getHeight(); j += mosaicRadius) {
//                // 每一次都是一个mosaic
//                // 获取当前mosaic中心点对应的颜色值
//                if (i + mosaicRadius / 2 > bufferedImage.getWidth() || j + mosaicRadius / 2 > bufferedImage.getHeight()) {
//                    continue;
//                }
//                int rgb = bufferedImage.getRGB(i, j);
//                // 设置颜色
//                for (int k = 0; k < mosaicRadius; k++) {
//                    for (int l = 0; l < mosaicRadius; l++) {
//                        bufferedImage.setRGB(i + k, j + l, rgb);
//                    }
//                }
//            }
//        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", os);
        Image image = new Image(new ByteArrayInputStream(os.toByteArray()), width, width, false, true);
        return new ImagePattern(image, 0, 0, width, width, false);
    }
}
