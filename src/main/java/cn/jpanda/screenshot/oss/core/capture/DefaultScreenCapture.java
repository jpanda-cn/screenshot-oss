package cn.jpanda.screenshot.oss.core.capture;

import cn.jpanda.screenshot.oss.core.exceptions.GraphicsDeviceNotFoundRuntimeException;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * 默认的截图实现类
 */
public class DefaultScreenCapture implements ScreenCapture {

    /**
     * 所有的屏幕设备
     */
    protected static GraphicsDevice[] GRAPHICS_DEVICES = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

    /**
     * 当前使用的默认屏幕(主显示器)
     */
    protected static GraphicsDevice DEFAULT_GRAPHICS_DEVICE = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    /**
     * 默认屏幕(主显示器)在所有显示器设备中的位置
     */
    protected static int DEFAULT_GRAPHICS_DEVICE_INDEX;

    static {
        DEFAULT_GRAPHICS_DEVICE_INDEX = Arrays.asList(GRAPHICS_DEVICES).indexOf(DEFAULT_GRAPHICS_DEVICE);
    }

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
        return GRAPHICS_DEVICES.length;
    }

    @Override
    public GraphicsDevice getTargetGraphicsDevice(final int index) {
        if (index >= GRAPHICS_DEVICES.length) {
            throw new GraphicsDeviceNotFoundRuntimeException(String.format("can not find graphics device with index %d", index));
        }
        return GRAPHICS_DEVICES[index];
    }

    public int getTargetGraphicsDeviceX(final int index) {
        // 根据指定的屏幕索引获取该屏幕起始位置x坐标的值
        // 主屏幕左侧/主屏幕/主屏幕右侧
        if (index == DEFAULT_GRAPHICS_DEVICE_INDEX) {
            return 0;
        }
        int x = 0;
        if (index < DEFAULT_GRAPHICS_DEVICE_INDEX) {
            // 左侧
            for (int i = index; i < DEFAULT_GRAPHICS_DEVICE_INDEX; i++) {
                Rectangle bounds = GRAPHICS_DEVICES[i].getDefaultConfiguration().getBounds();
                x -= bounds.getWidth();
            }
        } else {
            // 右侧
            for (int i = 0; i < index; i++) {
                x += GRAPHICS_DEVICES[i].getDefaultConfiguration().getBounds().getWidth();
            }
        }
        return x;
    }

    @Override
    public int getGraphicsDeviceIndex(double x) {
        // 根据x坐标获取对应的显示器索引
        // 获取第一块屏幕
        if (x == 0) {
            return DEFAULT_GRAPHICS_DEVICE_INDEX;
        }
        if (x < 0) {
            double startX = 0;
            // 左侧
            for (int i = DEFAULT_GRAPHICS_DEVICE_INDEX - 1; i > -1; i--) {
                startX -= GRAPHICS_DEVICES[i].getDefaultConfiguration().getBounds().getWidth();
                if (startX < x) {
                    return i;
                }
            }
        } else {
            // 右侧
            double endX = 0;
            for (int i = DEFAULT_GRAPHICS_DEVICE_INDEX; i < GRAPHICS_DEVICES.length; i++) {
                endX += GRAPHICS_DEVICES[i].getDefaultConfiguration().getBounds().getWidth();
                if (endX > x) {
                    return i;
                }

            }
        }
        throw new GraphicsDeviceNotFoundRuntimeException("can not find graphics device with x " +x);
    }
}
