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
        return robot.createScreenCapture(new Rectangle(getTargetGraphicsDeviceX(index) + x, y, width, height));
    }

    @Override
    @SneakyThrows
    public BufferedImage screenshotImage(int index, int x, int y, double percentWidth, double percentHeight) {
        GraphicsDevice graphicsDevice = getTargetGraphicsDevice(index);
        Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
        final int width = (int) (bounds.width * percentWidth);
        final int height = (int) (bounds.height * percentHeight);
        Robot robot = new Robot(graphicsDevice);
        return robot.createScreenCapture(new Rectangle(getTargetGraphicsDeviceX(index) + x, y, width, height));
    }

    @Override
    public int GraphicsDeviceCount() {
        return graphicsDevices.length;
    }

    @Override
    public GraphicsDevice getTargetGraphicsDevice(final int index) {
        if (index >= graphicsDevices.length) {
            throw new GraphicsDeviceNotFoundRuntimeException(String.format("can not find graphics device with index %d", index));
        }
        return graphicsDevices[index];
    }

    public int getTargetGraphicsDeviceX(final int index) {
        if (index >= graphicsDevices.length) {
            throw new GraphicsDeviceNotFoundRuntimeException(String.format("can not find graphics device with index %d", index));
        }
        int x = 0;
        for (int i = 0; i < index; i++) {
            x += graphicsDevices[i].getDefaultConfiguration().getBounds().getWidth();
        }
        return x;
    }

    @Override
    public int getGraphicsDeviceIndex(double x) {
        // 获取第一块屏幕
        int index = -1;
        double countX = 0;
        GraphicsDevice graphicsDevice;
        while (x > countX){
            index++;
            graphicsDevice = getTargetGraphicsDevice(index);
            Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
            countX += bounds.getWidth();
        }
        return index;
    }
}
