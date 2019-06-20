package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.Snapshot;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class IndexCutView implements Initializable {
    public MenuButton options;
    private Configuration configuration;

    public IndexCutView(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GlobalConfigPersistence globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        int index = globalConfigPersistence.getScreenIndex();
        if (index >= configuration.getUniqueBean(ScreenCapture.class).GraphicsDeviceCount()) {
            globalConfigPersistence.setScreenIndex(0);
            configuration.storePersistence(globalConfigPersistence);
        }
        initSettings();
    }

    private void initSettings() {
        MenuItem general = new MenuItem("通用设置");
        general.setOnAction(event -> {
            // 展示通用设置页面
            toSettings();
        });

        MenuItem pwd = new MenuItem("密码管理");
        MenuItem choseScreen = new MenuItem("切换屏幕");
        choseScreen.setOnAction(event -> {
            // 展示屏幕切换页面
            toChoseScreen();
        });
        SimpleBooleanProperty show = configuration.getUniqueBean(ChoseScreenShowValue.class).show;
        show.addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                options.getItems().add(choseScreen);
            } else {
                options.getItems().remove(choseScreen);
            }
        });
        options.getItems().add(general);
        options.getItems().add(pwd);
        // 判断是否展示
        if (show.not().get()) {
            options.getItems().add(choseScreen);
        }
    }


    public void doCut() {
        configuration.getUniqueBean(Snapshot.class).cut();
    }

    public void doKeyCut(KeyEvent event) {
        if (event.getCode().equals(KeyCode.SPACE) || event.getCode().equals(KeyCode.ENTER)) {
            doCut();
        }
    }

    public void toSettings() {
        Stage stage = new Stage();
        stage.initOwner(configuration.getViewContext().getStage());
        stage.setX(Math.max(stage.getOwner().xProperty().getValue(), 0));
        stage.setY(Math.max(stage.getOwner().yProperty().getValue(), 0));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(configuration.getViewContext().getScene(SettingsView.class, true, false));
        stage.showAndWait();
    }

    public void toChoseScreen() {
        // 获取当前窗口所属的显示器
        Stage defaultStage = configuration.getViewContext().getStage();
        Stage stage = new Stage();
        stage.initOwner(defaultStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = configuration.getViewContext().getScene(ChoseScreenView.class, true, false);
        stage.resizableProperty().setValue(false);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void toKeySettings(KeyEvent event) {
        if (event.getCode().equals(KeyCode.SPACE) || event.getCode().equals(KeyCode.ENTER)) {
            toSettings();
        }
    }
}
