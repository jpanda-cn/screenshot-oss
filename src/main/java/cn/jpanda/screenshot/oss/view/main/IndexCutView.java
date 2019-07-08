package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.Snapshot;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.persistence.BootstrapPersistence;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import cn.jpanda.screenshot.oss.core.persistence.PersistenceBeanCatalogManagement;
import cn.jpanda.screenshot.oss.core.shotkey.HotKey2CutPersistence;
import cn.jpanda.screenshot.oss.core.shotkey.SettingsHotKeyPropertyHolder;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.view.fail.FailListView;
import cn.jpanda.screenshot.oss.view.password.modify.ModifyPassword;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    public RadioButton hidden;
    public Label shotKey;
    public Button cutBtn;
    private Configuration configuration;

    public IndexCutView(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSettings();
        initHidden();
        loadShotKey();
    }

    private void loadShotKey() {
        shotKey.setMouseTransparent(true);
        SettingsHotKeyPropertyHolder settingsHotKeyPropertyHolder = configuration.getUniqueBean(SettingsHotKeyPropertyHolder.class);
        ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {
            if (!newValue) {
                updateShotKey();
            }
        };
        settingsHotKeyPropertyHolder.isSettings.removeListener(listener);
        settingsHotKeyPropertyHolder.isSettings.addListener(listener);
        updateShotKey();

    }

    private void updateShotKey() {
        String shot = getShotKey();
        // 计算文字宽度
        ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                shotKey.setLayoutX(cutBtn.widthProperty().subtract(newValue.doubleValue()).divide(2).add(cutBtn.layoutXProperty()).get());
            });
        };
        shotKey.widthProperty().removeListener(listener);
        shotKey.widthProperty().addListener(listener);
        shotKey.textProperty().setValue(shot);
    }

    private String getShotKey() {
        HotKey2CutPersistence hotKey2CutPersistence = configuration.getPersistence(HotKey2CutPersistence.class);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( ");
        if (hotKey2CutPersistence.isCtrl()) {
            stringBuilder.append(KeyCode.CONTROL.getName()).append(" + ");
        }
        if (hotKey2CutPersistence.isShift()) {
            stringBuilder.append(KeyCode.SHIFT.getName()).append(" + ");
        }
        if (hotKey2CutPersistence.isAlt()) {
            stringBuilder.append(KeyCode.ALT.getName()).append(" + ");
        }
        stringBuilder.append(hotKey2CutPersistence.getCode());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }

    public void initHidden() {

        GlobalConfigPersistence configPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        hidden.selectedProperty().set(configPersistence.isHideIndexScreen());
        ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {
            GlobalConfigPersistence configPersistence1 = configuration.getPersistence(GlobalConfigPersistence.class);
            configPersistence1.setHideIndexScreen(newValue);
            configuration.storePersistence(configPersistence1);
        };
        hidden.selectedProperty().removeListener(listener);
        hidden.selectedProperty().addListener(listener);
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
            Stage stage = configuration.getViewContext().newStage();
            stage.initStyle(StageStyle.UNDECORATED);
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
        options.getItems().add(general);
        options.getItems().add(pwd);
        MenuItem failList = new MenuItem("失败列表");
        options.getItems().add(failList);
        failList.setOnAction(event -> {
            // 展示失败列表设置页面
            toFailList();
        });

    }
    public void  toFailList(){
        Stage stage = configuration.getViewContext().newStage();
        stage.initOwner(configuration.getViewContext().getStage());
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(configuration.getViewContext().getScene(FailListView.class, true, false));
        stage.showAndWait();
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
        Stage stage = configuration.getViewContext().newStage();
        stage.initOwner(configuration.getViewContext().getStage());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(configuration.getViewContext().getScene(SettingsView.class, true, false));
        stage.showAndWait();
    }
}
