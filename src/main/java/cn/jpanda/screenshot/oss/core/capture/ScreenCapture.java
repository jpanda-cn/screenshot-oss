package cn.jpanda.screenshot.oss.core.capture;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 屏幕图片获取接口
 */
public interface ScreenCapture {
    /**
     * 获取指定屏幕的全屏截图
     *
     * @param index 指定位置
     * @return 截图
     */
    default BufferedImage screenshotImage(int index) {
        return screenshotImage(index, 1.0D, 1.0D);
    }

    /**
     * 获取指定屏幕的全屏截图
     *
     * @param index  指定位置
     * @param width  宽度
     * @param height 高度
     * @return 截图
     */
    default BufferedImage screenshotImage(int index, int width, int height) {
        return screenshotImage(index, 0, 0, width, height);
    }

    /**
     * 获取指定屏幕的全屏截图
     *
     * @param index  指定位置
     * @param x      起始X坐标
     * @param y      起始Y坐标
     * @param width  宽度
     * @param height 高度
     * @return 截图
     */
    BufferedImage screenshotImage(int index, int x, int y, int width, int height);
    /**
     * 获取指定屏幕的全屏截图
     *
     * @param index         指定位置
     * @param percentWidth  宽度百分比
     * @param percentHeight 高度百分比
     * @return 截图
     */
    default BufferedImage screenshotImage(int index, double percentWidth, double percentHeight) {
        return screenshotImage(index, 0, 0, percentWidth, percentHeight);
    }

    /**
     * 获取指定屏幕的全屏截图
     *
     * @param index         指定位置
     * @param x             起始X坐标
     * @param y             起始Y坐标
     * @param percentWidth  宽度百分比
     * @param percentHeight 高度百分比
     * @return 截图
     */
    BufferedImage screenshotImage(int index, int x, int y, double percentWidth, double percentHeight);

    int GraphicsDeviceCount();

    GraphicsDevice getTargetGraphicsDevice(final int index);

    int getTargetGraphicsDeviceX(final int index);
}
