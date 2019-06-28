package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.mosaic;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.core.ScreenshotsProcess;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.shotkey.DefaultGroupScreenshotsElements;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 典型的马赛克截图实现
 * 鼠标的xy坐标向外扩展{@link #mosaicRadius}构成一个mosaic
 * 画笔的宽度决定x、y轴上有几个mosaic对象
 */
@Deprecated
public class TypicalMosaicInnerSnapshotCanvasEventHandler extends MosaicInnerSnapshotCanvasEventHandler {
    private Map<String, Integer> indexRgb = new HashMap<>();
    /**
     * 马赛克的半径 实际直径为: mosaicRadius*2+1
     */
    private int mosaicRadius = 20;
    /**
     * 画笔宽度的一半，实际长度为 widthRadius*2+1
     */
    private int widthSize = 2;

    private int mosaicWidth = mosaicRadius * 2 + 1;
    /**
     * 画笔宽度半径
     */
    private int widthRadius = mosaicWidth * widthSize;
    /**
     * 画笔宽度
     */
    private int width = widthRadius * 2 + mosaicWidth;

    protected BufferedImage computerBufferedImage;

    protected ImageView showImage = new ImageView();
    /**
     * 日志
     */
    private Log log;

    public TypicalMosaicInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
        log = canvasProperties.getConfiguration().getLogFactory().getLog(getClass());
        canvasProperties.getCutPane().getChildren().add(showImage);
    }

    @Override
    @SneakyThrows
    protected void press(MouseEvent event) {
        clear();
        // 获取鼠标当前位置
        x = event.getSceneX();
        y = event.getSceneY();
        // 理论上点击的时候就会直接生成一个马赛克

        // 处理左侧
        if (x - widthRadius < rectangle.getX()) {
            x = rectangle.getX() + widthRadius;
        }
        if (y - widthRadius < rectangle.getY()) {
            y = rectangle.getY() + widthRadius;
        }

        if (x + widthRadius > rectangle.xProperty().add(rectangle.widthProperty()).get()) {
            // 右侧不满足
            x = rectangle.xProperty().add(rectangle.widthProperty()).subtract(widthRadius).get();
        }
        // 处理高
        if (y + widthRadius > rectangle.yProperty().add(rectangle.heightProperty()).get()) {
            // 右侧不满足
            y = rectangle.yProperty().add(rectangle.heightProperty()).subtract(widthRadius).get();
        }

        // 获取截图区域的整个图片
        ScreenshotsProcess screenshotsProcess = canvasProperties.getConfiguration().getUniqueBean(ScreenshotsProcess.class);
        // 真正处理的图片
        bufferedImage = screenshotsProcess.snapshot(canvasProperties.getCutPane().getScene(), rectangle);
        computerBufferedImage = screenshotsProcess.snapshot(canvasProperties.getCutPane().getScene(), rectangle);
        // 用来计算 永远不会变更

        // 新建一个路径
        path = new Path();
        group = new Group(path);
        canvasProperties.getCutPane().getChildren().add(group);
        // 注册操作
        canvasProperties.getScreenshotsElementsHolder().putEffectiveElement(new DefaultGroupScreenshotsElements(group, canvasProperties));
        // 设置宽度
        path.strokeWidthProperty().set(width);

        // 移动坐标
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
        if (cx - widthRadius < rectangle.xProperty().getValue()) {
            // 左侧不满足
            cx = rectangle.getX() + widthRadius;
        }
        if (cx + widthRadius > rectangle.xProperty().add(rectangle.widthProperty()).get()) {
            // 右侧不满足
            cx = rectangle.xProperty().add(rectangle.widthProperty()).subtract(widthRadius).get();
        }
        // 处理高
        if (cy - widthRadius < rectangle.getY()) {
            // 左侧不满足
            cy = rectangle.getY() + widthRadius;
        }
        if (cy + widthRadius > rectangle.yProperty().add(rectangle.heightProperty()).get()) {
            // 右侧不满足
            cy = rectangle.yProperty().add(rectangle.heightProperty()).subtract(widthRadius).get();
        }

        if (MathUtils.subAbs(x, cx) < width && MathUtils.subAbs(y, cy) < width) {
            return;
        }
        // 坐标处理完毕，准备进行马赛克的绘制
        // 计算出当前画笔需要

        // 画笔半径
        // 只处理操作一个马赛克的坐标
        // 计算需要画几次
        // 处理路径
        // 水平偏移量
        double hOffset = MathUtils.subAbs(cx, x);
        // 垂直偏移量
        double vOffset = MathUtils.subAbs(cy, y);

        if (hOffset < mosaicWidth && vOffset < mosaicWidth) {
            return;
        }


        handlerFullPath(x, y, cx, cy);

        // 刷新xy坐标
        x = hOffset % mosaicWidth == 0 ? cx : cx - hOffset % mosaicWidth;

        y = vOffset % mosaicWidth == 0 ? cy : cy - vOffset % mosaicWidth;

    }

    @SneakyThrows
    protected void handlerFullPath(double x, double y, double ex, double ey) {
        // 拆分成具体的mosaic元素

        // 水平偏移量
        double hOffset = MathUtils.subAbs(ex, x);
        // 垂直偏移量
        double vOffset = MathUtils.subAbs(ey, y);

        // 重置偏移量为有效值
        hOffset = hOffset % mosaicWidth == 0 ? hOffset : (hOffset - hOffset % mosaicWidth);
        vOffset = vOffset % mosaicWidth == 0 ? vOffset : (vOffset - vOffset % mosaicWidth);

        // 水平移动时，鼠标指针位于x轴末端，y轴中点
        // 垂直移动时，鼠标指针位于y轴末端，x轴中点
        // 斜角移动时，鼠标指针为于y轴中点，x轴中点。
        // 垂直移动
        boolean toV = vOffset > 0 && hOffset == 0;
        // 水平移动
        boolean toH = hOffset > 0 && vOffset == 0;

        // 获取x/y轴最大偏移量，该偏移量最终决定了画笔的数量
        double maxOffset = MathUtils.max(hOffset, vOffset);


        // 计算需要多少个offsetX才会大于等于一个格
        int minx = (int) MathUtils.min(x, ex);
        int miny = (int) MathUtils.min(y, ey);

        // 获取相对坐标
        minx = minx - rectangle.xProperty().intValue();
        miny = miny - rectangle.yProperty().intValue();

        // 判断鼠标行动防线
        BufferedImage mosaicImage;
        BufferedImage computerMosaicImage;
        if (toH) {
            // 横着走，截图位置的起始: x为x，y为y-widthRadius-1,h=width,w=hOffset
            // 获取截图
            mosaicImage = bufferedImage.getSubimage(minx, (miny - widthRadius - 1), (int) hOffset, width);
            computerMosaicImage = computerBufferedImage.getSubimage(minx, (miny - widthRadius - 1), (int) hOffset, width);
            // 转储图片为mosaic，前面已经处理过偏移量了，此处图片必然为整数个mosaic
            // 将图片mosaic化处理
//            handlerMosaic(computerMosaicImage, mosaicImage);
            // 展示
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(mosaicImage, "PNG", os);
            Image image = new Image(new ByteArrayInputStream(os.toByteArray()), (int) hOffset, width, true, true);
            path.strokeProperty().setValue(new ImagePattern(image, 0, 0, (int) hOffset, width, false));
        } else if (toV) {
            // 竖着走，截图位置的起始: x为x-widthRadius-1，y为y,h=vOffset,w=width
            mosaicImage = bufferedImage.getSubimage((minx - widthRadius - 1), (miny), width, (int) vOffset);
            computerMosaicImage = computerBufferedImage.getSubimage((minx - widthRadius - 1), (miny), width, (int) vOffset);
            // 转储图片为mosaic，前面已经处理过偏移量了，此处图片必然为整数个mosaic
            // 将图片mosaic化处理
            handlerMosaic(computerMosaicImage, mosaicImage);
            // 展示
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(mosaicImage, "PNG", os);
            Image image = new Image(new ByteArrayInputStream(os.toByteArray()), width, (int) vOffset, true, true);
            path.strokeProperty().setValue(new ImagePattern(image, 0, 0, width, (int) vOffset, false));
        } else {
            if ((minx - widthRadius - 1) < 1 || (miny - widthRadius - 1) < 1 || hOffset < 1 || vOffset < 1) {
                return;
            }
            // 竖着走，截图位置的起始: x为x-widthRadius-1，y为y,h=vOffset,w=width
            mosaicImage = bufferedImage.getSubimage((minx - widthRadius - 1), (miny - widthRadius - 1), (int) hOffset, (int) vOffset);
            computerMosaicImage = computerBufferedImage.getSubimage((minx - widthRadius - 1), (miny - widthRadius - 1), (int) hOffset, (int) vOffset);
            // 转储图片为mosaic，前面已经处理过偏移量了，此处图片必然为整数个mosaic
            // 将图片mosaic化处理
            handlerMosaic(computerMosaicImage, mosaicImage);
            // 展示
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(mosaicImage, "PNG", os);
            Image image = new Image(new ByteArrayInputStream(os.toByteArray()), (int) hOffset, (int) vOffset, true, true);
            path.strokeProperty().setValue(new ImagePattern(image, 0, 0, (int) hOffset, (int) vOffset, false));
        }

        Image computerImage = SwingFXUtils.toFXImage(computerMosaicImage, null);

        // TODO debug模式下在左上角展示获取到的图片
        showImage.setImage(computerImage);

        // 完成了此次图片写入操作
        // 完成添加操作
        LineTo lineTo = new LineTo(rectangle.xProperty().add(minx).get(), rectangle.yProperty().add(miny).get());
        lineTo.setAbsolute(true);
        path.getElements().add(lineTo);
        // 每次均刷新
        path.getElements().add(new ClosePath());
        path = new Path();

        group.getChildren().add(path);

        path.setStrokeWidth(width);
        MoveTo moveTo = new MoveTo(rectangle.xProperty().add(minx).get(), rectangle.yProperty().add(miny).get());
        moveTo.setAbsolute(true);
        path.getElements().add(moveTo);
    }

    private void handlerMosaic(BufferedImage computer, BufferedImage show) {
        // 两个图片是一致的
        for (int i = computer.getMinX(); i < computer.getMinX() + computer.getWidth(); i += mosaicWidth) {
            for (int j = computer.getMinY(); j < computer.getMinY() + computer.getHeight(); j += mosaicWidth) {
                // 获取其 中点 RGB
                int rgb = computer.getRGB(i + mosaicRadius, j + mosaicRadius);
                // 将该颜色写入到show中的指定位置中
                updateColor(show, i, j, i + mosaicWidth, j + mosaicWidth, rgb);
            }
        }
    }


    private void updateColor(BufferedImage bufferedImage, int x, int ex, int y, int ey, int rgb) {
        for (int a = x; a < ex; a++) {
            for (int b = y; b < ey; b++) {
                setColor(bufferedImage, a, b, rgb);
            }
        }
    }

    /**
     * 设置颜色
     */
    private void setColor(BufferedImage bufferedImage, int x, int y, int newRgb) {
        int rgb = getOrSetRgb((int) (x + rectangle.getX()), (int) (y + rectangle.getY()), newRgb);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(new Color(rgb));
        graphics2D.fillRect(x, y, 1, 1);
        graphics2D.dispose();
    }

    private int getOrSetRgb(int x, int y, int newRgb) {
        String name = x + ":" + y;
        indexRgb.put(name, newRgb);
        log.trace("name:{}.rgb:{}", name, indexRgb.get(name));
        return indexRgb.get(name);
    }
}
