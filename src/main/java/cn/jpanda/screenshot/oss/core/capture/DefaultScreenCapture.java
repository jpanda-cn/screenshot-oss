package cn.jpanda.screenshot.oss.core.capture;

import cn.jpanda.screenshot.oss.core.exceptions.GraphicsDeviceNotFoundRuntimeException;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 默认的截图实现类
 */
public class DefaultScreenCapture implements ScreenCapture {

    protected static final GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();


    @Override
    @SneakyThrows
    public BufferedImage screenshotImage(int index, int x, int y, int width, int height) {
        // 获取指定的屏幕设备
        GraphicsDevice graphicsDevice = getTargetGraphicsDevice(index);
        Robot robot = new Robot(graphicsDevice);
        return robot.createScreenCapture(new Rectangle(x, y, width, height));
    }

    @Override
    @SneakyThrows
    public BufferedImage screenshotImage(int index, int x, int y, double percentWidth, double percentHeight) {
        GraphicsDevice graphicsDevice = getTargetGraphicsDevice(index);
        Dimension dimension = graphicsDevice.getDefaultConfiguration().getBounds().getSize();
        final int width = (int) (dimension.width * percentWidth);
        final int height = (int) (dimension.height * percentHeight);
        Robot robot = new Robot(graphicsDevice);
        return robot.createScreenCapture(new Rectangle(x, y, width, height));
    }

    @Override
    public int GraphicsDeviceCount() {
        return graphicsDevices.length;
    }

    protected GraphicsDevice getTargetGraphicsDevice(final int index) {
        if (index >= graphicsDevices.length) {
            throw new GraphicsDeviceNotFoundRuntimeException(String.format("can not find graphics device with index %d", index));
        }
        return graphicsDevices[index];
    }
}
