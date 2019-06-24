package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.common.toolkit.Bounds;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Screen;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * 选择屏幕,获取当前屏幕的一般大小居中展示
 */
@Controller
public class ChoseScreenView implements Initializable {
    /**
     * 该属性会自动注入
     */
    private Configuration configuration;

    public ChoseScreenView(Configuration configuration) {
        this.configuration = configuration;
    }

    @FXML
    public TabPane tabs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ScreenCapture screenCapture = configuration.getUniqueBean(ScreenCapture.class);
        GlobalConfigPersistence globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        List<Screen> originalScreens = screenCapture.listScreen();
        List<Screen> sortedScreens = originalScreens.stream().sorted(Comparator.comparingDouble(p -> p.getBounds().getMinX())).collect(Collectors.toList());
        int index = 0;
        for (Screen screen : sortedScreens) {
            Tab tab = new Tab();
            tab.setText(String.format("屏幕%d", index));
            WritableImage writableImage = SwingFXUtils.toFXImage(screenCapture.screenshotImage(originalScreens.indexOf(screen)), null);
            ImageView view = new ImageView(writableImage);
            // 设置为屏幕的一半大
            // x坐标为负数的话，则表示 主屏幕并不在最左侧

            int screenIndex = screenCapture.getScreenIndex(configuration.getViewContext().getStage().xProperty().get());
            // 获取该显示器
            Bounds graphicsDevice = screenCapture.getTargetScreen(screenIndex);
            // 通过该显示器的起始x坐标
            // 获取该显示器的宽度和高度的一般，用来展示
            double width = graphicsDevice.getWidth();
            double height = graphicsDevice.getHeight();
            view.setFitHeight(height / 2);
            view.setFitWidth(width / 2);
            tab.setContent(view);
            tabs.getTabs().add(tab);
            tab.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    globalConfigPersistence.setScreenIndex(originalScreens.indexOf(screen));
                    configuration.storePersistence(globalConfigPersistence);
                }
            });
            index++;
        }
        if (globalConfigPersistence.getScreenIndex() >= screenCapture.screensCount()) {
            globalConfigPersistence.setScreenIndex(0);
            configuration.storePersistence(globalConfigPersistence);
        }
        tabs.selectionModelProperty().get().select(globalConfigPersistence.getScreenIndex());
    }
}
