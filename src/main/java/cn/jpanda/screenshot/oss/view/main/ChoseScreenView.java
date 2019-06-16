package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.newcore.Configuration;
import cn.jpanda.screenshot.oss.newcore.annotations.Controller;
import cn.jpanda.screenshot.oss.newcore.capture.DefaultScreenCapture;
import cn.jpanda.screenshot.oss.newcore.capture.ScreenCapture;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 选择屏幕
 */
@Controller
public class ChoseScreenView implements Initializable {
    /**
     * 该属性会自动注入
     */
    private Configuration configuration;

    public ChoseScreenView() {
    }

    public ChoseScreenView(Configuration configuration) {
        this.configuration = configuration;
    }

    @FXML
    public TabPane tabs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ScreenCapture screenCapture = new DefaultScreenCapture();
        for (int i = 0; i < screenCapture.GraphicsDeviceCount(); i++) {
            Tab tab = new Tab();
            tab.setText(String.format("屏幕%d", i));
            WritableImage writableImage = SwingFXUtils.toFXImage(screenCapture.screenshotImage(i), null);
            ImageView view = new ImageView(writableImage);
            view.setFitHeight(540);
            view.setFitWidth(960);
            tab.setContent(view);
            tabs.getTabs().add(tab);
        }

    }
}
