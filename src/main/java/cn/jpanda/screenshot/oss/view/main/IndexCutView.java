package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.common.enums.ClipboardType;
import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.Snapshot;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.persistence.BootstrapPersistence;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import cn.jpanda.screenshot.oss.core.persistence.PersistenceBeanCatalogManagement;
import cn.jpanda.screenshot.oss.core.shotkey.HotKey2CutPersistence;
import cn.jpanda.screenshot.oss.core.shotkey.SettingsHotKeyPropertyHolder;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.shape.ModelDialog;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.img.ImageStore;
import cn.jpanda.screenshot.oss.store.img.ImageStoreRegisterManager;
import cn.jpanda.screenshot.oss.store.img.NoImageStoreConfig;
import cn.jpanda.screenshot.oss.view.fail.FailListView;
import cn.jpanda.screenshot.oss.view.models.CloseModelView;
import cn.jpanda.screenshot.oss.view.password.modify.ModifyPassword;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Shadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class IndexCutView implements Initializable {

    /**
     * 选型
     */
    public MenuButton options;
    /**
     * 截图时是否隐藏该菜单
     */
    public RadioButton hidden;
    /**
     * 快捷键
     */
    public Label shotKeyShower;
    /**
     * 截图按钮
     */
    public Button cutBtn;
    /**
     * 主容器
     */
    public AnchorPane mainContain;

    /**
     * 右侧容器
     */
    public AnchorPane rightContain;
    public ProgressBar loadStatus;
    @FXML
    public AnchorPane containTop;
    public Button exit;
    /**
     * 全局配置对象
     */
    private Configuration configuration;

    /**
     * 快捷键输入框
     */
    public TextField hotKey;

    /**
     * 编辑存储方式
     */
    public Button storeEdit;
    /**
     * 图片存储方式复选框
     */
    public ComboBox imageSave;
    /**
     * 剪切板内容复选框
     */
    @FXML
    public ComboBox clipboard;
    /**
     * 快键键
     */
    public Label shotKeyL;

    private ImageStoreRegisterManager imageStoreRegisterManager;
    private ClipboardCallbackRegistryManager clipboardCallbackRegistryManager;
    private GlobalConfigPersistence globalConfigPersistence;

    public IndexCutView(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initExit();

        // 加载配置
        imageStoreRegisterManager = configuration.getUniqueBean(ImageStoreRegisterManager.class);
        clipboardCallbackRegistryManager = configuration.getUniqueBean(ClipboardCallbackRegistryManager.class);
        globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        initSettings();
        initHidden();
        loadShotKey();

        loadImageSave();
        loadClipboard();
        loadHotKey();
        loadStatus.progressProperty().setValue(0.5);

        addDrag();

    }

    private void initExit() {
        exit.setPickOnBounds(true);
    }


    private void loadShotKey() {
        shotKeyShower.setMouseTransparent(true);
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
        String shot = getShotKeyShower();
        // 计算文字宽度
        ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                shotKeyShower.setLayoutX(cutBtn.widthProperty().subtract(newValue.doubleValue()).divide(2).add(cutBtn.layoutXProperty()).get());
            });
        };
        shotKeyShower.widthProperty().removeListener(listener);
        shotKeyShower.widthProperty().addListener(listener);
        shotKeyShower.textProperty().setValue(shot);
    }

    private String getShotKeyShower() {
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

        Menu pwd = new Menu("密码管理");
        MenuItem stopUsePwd = new MenuItem("停用密码");
        MenuItem usePwd = new MenuItem("启用密码");
        MenuItem cpwd = new MenuItem("修改密码");

//        addShadow(pwd,stopUsePwd,usePwd,cpwd);
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
            Scene scene = configuration.getViewContext().getScene(ModifyPassword.class);
            ModelDialog<Void> modelDialog = new ModelDialog<>(containTop.getScene().getWindow());
            modelDialog.initModality(Modality.APPLICATION_MODAL);
            modelDialog.setContent(scene.getRoot());
            modelDialog.showAndWait();
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

        options.getItems().add(pwd);
        MenuItem failList = new MenuItem("失败任务列表");
        options.getItems().add(failList);
        failList.setOnAction(event -> {
            // 展示失败列表设置页面
            toFailList();
        });

    }


    public void toFailList() {
        // 使用动画，加载至右侧
        Stage stage = configuration.getViewContext().newStage();
        stage.setTitle("失败任务列表");
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

    private void loadHotKey() {
        SettingsHotKeyPropertyHolder settingsHotKeyPropertyHolder = configuration.getUniqueBean(SettingsHotKeyPropertyHolder.class);
        settingsHotKeyPropertyHolder.isSettings.unbind();
        settingsHotKeyPropertyHolder.isSettings.bind(hotKey.focusedProperty());

        hotKey.editableProperty().setValue(false);
        HotKey2CutPersistence hotKey2CutPersistence = configuration.getPersistence(HotKey2CutPersistence.class);

        StringBuilder stringBuilder = new StringBuilder();
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
        hotKey.textProperty().setValue(stringBuilder.toString());
    }

    @SuppressWarnings("unchecked")
    private void loadImageSave() {
        // 监听事件
        imageSave.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof String) {
                globalConfigPersistence.setImageStore((String) newValue);

                // 联动，剪切板的内容同步发生变化
                ImageStore imageStore = imageStoreRegisterManager.getImageStore((String) newValue);
                if (!imageStore.check()) {
                    // 会出现异常，但是不影响正常业务
                    // 2019年6月22日21:48:55 修复角标越界异常
                    Platform.runLater(new Thread(() -> imageSave.getSelectionModel().selectNext()));
                    return;
                }
                // 判断是否有对应的配置界面，决定是否展示配置按钮
                Class<? extends Initializable> conf = imageStoreRegisterManager.getConfig((String) newValue);
                storeEdit.disableProperty().setValue((conf == null || conf.equals(NoImageStoreConfig.class)));
                ImageType type = imageStoreRegisterManager.getType((String) newValue);
                List<String> cls;
                switch (type) {
                    case NO_PATH: {
                        cls = clipboardCallbackRegistryManager.getNamesByType(ClipboardType.NOT_NEED);
                        break;
                    }
                    case HAS_PATH: {
                        cls = clipboardCallbackRegistryManager.getNames();
                        break;
                    }
                    default: {
                        cls = clipboardCallbackRegistryManager.getNames();
                    }
                }
                // 更新剪切板
                clipboard.getItems().clear();
                clipboard.getItems().addAll(cls);
                // 校验之前选中的是否还可用，不可用使用第一个
                if (cls.contains(globalConfigPersistence.getClipboardCallback())) {
                    clipboard.getSelectionModel().select(globalConfigPersistence.getClipboardCallback());
                } else {
                    clipboard.getSelectionModel().select(0);
                    globalConfigPersistence.setClipboardCallback((String) clipboard.getItems().get(0));
                }
                configuration.storePersistence(globalConfigPersistence);
            }
        });

        // 初始化存储方式列表
        imageSave.getItems().clear();
        imageSave.getItems().addAll(imageStoreRegisterManager.getNames());
        imageSave.getSelectionModel().select(globalConfigPersistence.getImageStore());
        // 判断是否有对应的配置界面，决定是否展示配置按钮
        storeEdit.visibleProperty().setValue(imageStoreRegisterManager.getConfig(globalConfigPersistence.getImageStore()) != null);

    }

    @SuppressWarnings("unchecked")
    private void loadClipboard() {
        clipboard.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof String) {
                if (!globalConfigPersistence.getClipboardCallback().equalsIgnoreCase((String) newValue)) {
                    globalConfigPersistence.setClipboardCallback((String) newValue);
                    // 刷新配置
                    // 联动
                    ClipboardType clipboardType = clipboardCallbackRegistryManager.getType((String) newValue);
                    List<String> is;
                    switch (clipboardType) {
                        case NOT_NEED: {
                            is = imageStoreRegisterManager.getNames();
                            break;
                        }
                        case NEED_PATH: {
                            is = imageStoreRegisterManager.getNamesByType(ImageType.HAS_PATH);
                            break;
                        }
                        default: {
                            is = imageStoreRegisterManager.getNamesByType(ImageType.HAS_PATH);
                            break;
                        }
                    }
                    imageSave.getItems().clear();
                    imageSave.getItems().addAll(is);
                    // 校验之前选中的是否还可用，不可用使用第一个
                    if (is.contains(globalConfigPersistence.getImageStore())) {
                        imageSave.getSelectionModel().select(globalConfigPersistence.getImageStore());
                    } else {
                        imageSave.getSelectionModel().select(0);
                        globalConfigPersistence.setImageStore((String) imageSave.getItems().get(0));
                    }
                    configuration.storePersistence(globalConfigPersistence);
                }
            }
        });
        clipboard.getItems().clear();
        // 初始化保存到剪切板的内容
        clipboard.getItems().addAll(clipboardCallbackRegistryManager.getNames());
        clipboard.getSelectionModel().select(globalConfigPersistence.getClipboardCallback());

    }


    public void editImageStore() {
        // 获取当前选择的图片存储方式
        String name = (String) imageSave.getValue();
        ImageStore imageStore = imageStoreRegisterManager.getImageStore(name);
        imageStore.config();
    }


    public void back() {
        // 取消
        ((Stage) storeEdit.getScene().getWindow()).close();
    }

    public void keyBack(KeyEvent e) {
        if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.SPACE)) {
            // 执行保存操作
            back();
        }
    }

    public void changeHotKey(KeyEvent event) {

        HotKey2CutPersistence hotKey2CutPersistence = configuration.getPersistence(HotKey2CutPersistence.class);
        StringBuilder stringBuilder = new StringBuilder();
        hotKey2CutPersistence.setShift(event.isShiftDown());
        hotKey2CutPersistence.setAlt(event.isAltDown());
        hotKey2CutPersistence.setCtrl(event.isControlDown());
        if (hotKey2CutPersistence.isCtrl()) {
            stringBuilder.append(KeyCode.CONTROL.getName()).append(" + ");
        }
        if (hotKey2CutPersistence.isShift()) {
            stringBuilder.append(KeyCode.SHIFT.getName()).append(" + ");
        }
        if (hotKey2CutPersistence.isAlt()) {
            stringBuilder.append(KeyCode.ALT.getName()).append(" + ");
        }

        hotKey.textProperty().setValue(stringBuilder.toString());
        if (event.getCode().equals(KeyCode.SHIFT)
                || event.getCode().equals(KeyCode.ALT)
                || event.getCode().equals(KeyCode.CONTROL)) {
            return;
        }
        String name = event.getCode().getName();
        stringBuilder.append(name).append(" ");
        hotKey2CutPersistence.setCode(name);
        hotKey.textProperty().setValue(stringBuilder.toString());
        configuration.storePersistence(hotKey2CutPersistence);
    }

    public void addDrag() {

        containTop.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            private double xOffset = 0;
            private double yOffset = 0;
            Stage stage;

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {

                    stage = (Stage) containTop.getScene().getWindow();
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            }
        });


    }

    public void doClose() {
        // 获取关闭视图
        Scene scene = configuration.getViewContext().getScene(CloseModelView.class);
        ModelDialog<String> modelDialog = new ModelDialog<>(containTop.getScene().getWindow());
        modelDialog.initModality(Modality.APPLICATION_MODAL);
        modelDialog.setContent(scene.getRoot());
        if ("min".equals(modelDialog.showAndWait().orElse("min"))) {
            ((Stage) containTop.getScene().getWindow()).setIconified(true);
        } else {
            Platform.exit();
        }
    }


}
