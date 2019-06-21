package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.Snapshot;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.persistence.BootstrapPersistence;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import cn.jpanda.screenshot.oss.core.persistence.PersistenceBeanCatalogManagement;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.view.password.modify.ModifyPassword;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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


        Menu pwd = new Menu("密码管理");
        MenuItem stopUsePwd = new MenuItem("停用密码");
        MenuItem usePwd = new MenuItem("启用密码");
        MenuItem cpwd = new MenuItem("修改密码");

        stopUsePwd.setOnAction(event -> {
            BootstrapPersistence bootstrapPersistence = configuration.getPersistence(BootstrapPersistence.class);
            // 取消密码，重新存储一下数据
            PersistenceBeanCatalogManagement persistenceBeanCatalogManagement = configuration.getUniqueBean(PersistenceBeanCatalogManagement.class);
            // 将所有数据重新加载
            List<Persistence> list =
                    persistenceBeanCatalogManagement.list().stream().filter((p) -> !BootstrapPersistence.class.isAssignableFrom(p)).map((p) -> configuration.getPersistence(p)).collect(Collectors.toList());
            // 重置使用密码标记
            bootstrapPersistence.setUsePassword(false);
            configuration.storePersistence(bootstrapPersistence);
            configuration.setPassword(null);
            // 重新存储
            list.forEach((p) -> {
                configuration.storePersistence(p);
            });
            // 完成
            pwd.getItems().remove(stopUsePwd);
            pwd.getItems().remove(cpwd);
            pwd.getItems().add(0, usePwd);
        });

        EventHandler<ActionEvent> usePwdAction = (event) -> {
            PersistenceBeanCatalogManagement persistenceBeanCatalogManagement = configuration.getUniqueBean(PersistenceBeanCatalogManagement.class);
            // 将所有数据重新加载
            List<Persistence> list =
                    persistenceBeanCatalogManagement.list().stream().filter((p) -> !BootstrapPersistence.class.isAssignableFrom(p)).map((p) -> configuration.getPersistence(p)).collect(Collectors.toList());
            // 跳转到初始化密码页面
            // 将密码页面放置到舞台中央
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("logo.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = configuration.getViewContext().getScene(ModifyPassword.class);
            stage.setScene(scene);
            stage.setTitle("配置主控密码");
            stage.toFront();
            stage.showAndWait();
            if (configuration.getPersistence(BootstrapPersistence.class).isUsePassword()) {
                // 将所有配置重新保存一下
                list.forEach((p) -> {
                    configuration.storePersistence(p);
                });
                // 完成
                pwd.getItems().remove(usePwd);
                pwd.getItems().add(0, stopUsePwd);
                pwd.getItems().add(1, cpwd);
            }
        };

        usePwd.setOnAction(usePwdAction);
        cpwd.setOnAction(usePwdAction);
        BootstrapPersistence bootstrapPersistence = configuration.getPersistence(BootstrapPersistence.class);
        pwd.getItems().addAll(bootstrapPersistence.isUsePassword() ? new MenuItem[]{stopUsePwd, cpwd} : new MenuItem[]{usePwd});


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
}