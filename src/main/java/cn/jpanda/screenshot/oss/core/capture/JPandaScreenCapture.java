package cn.jpanda.screenshot.oss.core.capture;

import javafx.collections.ObservableList;
import javafx.stage.Screen;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.image.BufferedImage;

public class JPandaScreenCapture implements ScreenCapture {
    @Override
    @SneakyThrows
    public BufferedImage screenshotImage() {
        //所有的 显示器
        ObservableList<Screen> screens = Screen.getScreens();
        int minX = minx();
        int minY = miny();
        int countWidth = screens.stream().mapToInt((s) -> (int) s.getBounds().getWidth()).sum();
        int maxHeight = screens.stream().mapToInt((s) -> (int) s.getBounds().getHeight()).min().orElse(0);
        return new Robot().createScreenCapture(new Rectangle(minX, minY, countWidth, maxHeight));
    }

    @Override
    public Screen first() {
        return Screen.getScreens().get(0);
    }

    @Override
    public Screen main() {
        return Screen.getPrimary();
    }

    @Override
    public ObservableList<Screen> screens() {
        return Screen.getScreens();
    }

    @Override
    public int minx() {
        return screens().stream().mapToInt((s) -> (int) s.getBounds().getMinX()).min().orElse(0);
    }

    @Override
    public int miny() {
        return screens().stream().mapToInt((s) -> (int) s.getBounds().getMinY()).min().orElse(0);
    }

}
