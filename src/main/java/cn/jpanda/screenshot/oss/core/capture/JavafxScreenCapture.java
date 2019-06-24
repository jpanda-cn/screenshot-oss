package cn.jpanda.screenshot.oss.core.capture;

import cn.jpanda.screenshot.oss.common.toolkit.Bounds;
import com.sun.javafx.util.Utils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class JavafxScreenCapture implements ScreenCapture {

    /**
     * 所有的 显示器
     */
    private static ObservableList<Screen> SCREENS = Screen.getScreens();

    /**
     * 默认显示器
     */
    private static Screen SCREEN = Screen.getPrimary();

    /**
     * 默认显示器索引
     */
    private static SimpleIntegerProperty DEFAULT_SCREEN_INDEX = new SimpleIntegerProperty(SCREENS.indexOf(SCREEN));

    static {
        // 设置有序的显示器列表
        SCREENS.addListener((ListChangeListener<Screen>) c -> {
            SCREEN = Screen.getPrimary();
            DEFAULT_SCREEN_INDEX.set(SCREENS.indexOf(SCREEN));
        });
    }

    @Override
    @SneakyThrows
    public BufferedImage screenshotImage(int index, int x, int y, int width, int height) {
        Screen screen = SCREENS.get(index);
        Rectangle2D rectangle2D = screen.getBounds();
        return new Robot().createScreenCapture(new Rectangle((int) rectangle2D.getMinX(), (int) rectangle2D.getMinY(), (int) rectangle2D.getWidth(), (int) rectangle2D.getHeight()));
    }

    @Override
    @SneakyThrows
    public BufferedImage screenshotImage(int index, int x, int y, double percentWidth, double percentHeight) {
        Screen screen = SCREENS.get(index);
        Rectangle2D rectangle2D = screen.getBounds();
        return new Robot().createScreenCapture(new Rectangle((int) rectangle2D.getMinX(), (int) rectangle2D.getMinY(), (int) (rectangle2D.getWidth() * percentWidth), (int) (rectangle2D.getHeight() * percentHeight)));
    }

    @Override
    public int screensCount() {
        return SCREENS.size();
    }

    @Override
    public Bounds getTargetScreen(int index) {
        Screen screen = SCREENS.get(index);
        double x = screen.getBounds().getMinX();
        double y = screen.getBounds().getMinY();
        double w = screen.getBounds().getWidth();
        double h = screen.getBounds().getHeight();
        return new Bounds(x, y, w, h);
    }

    @Override
    public int getTargetScreenX(int index) {
        return (int) SCREENS.get(index).getBounds().getMinX();
    }

    @Override
    public int getScreenIndex(double x) {
        return SCREENS.indexOf(Utils.getScreenForPoint(x, 0));
    }

    @Override
    public List<Screen> listScreen() {
        return SCREENS;
    }
}