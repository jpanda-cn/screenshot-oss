package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.common.enums.ClipboardType;
import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.shotkey.HotKey2CutPersistence;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.img.ImageStore;
import cn.jpanda.screenshot.oss.store.img.ImageStoreRegisterManager;
import cn.jpanda.screenshot.oss.store.img.NoImageStoreConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class SettingsView implements Initializable {
    public TextField hotKey;
    public CheckBox screenshotMouseFollow;
    public Label screenshotMouseFollowLabel;
    private Configuration configuration;

    public SettingsView(Configuration configuration) {
        this.configuration = configuration;
    }

    public Button edit;
    public ComboBox imageSave;
    @FXML
    public ComboBox clipboard;
    public Label shotKey;
    /**
     * 截图预览
     */
    @FXML
    public CheckBox preview;

    private ImageStoreRegisterManager imageStoreRegisterManager;
    private ClipboardCallbackRegistryManager clipboardCallbackRegistryManager;
    private GlobalConfigPersistence globalConfigPersistence;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 加载配置
        imageStoreRegisterManager = configuration.getUniqueBean(ImageStoreRegisterManager.class);
        clipboardCallbackRegistryManager = configuration.getUniqueBean(ClipboardCallbackRegistryManager.class);
        globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        loadImageSave();
        loadClipboard();
        loadPreView();
        loadScreenshotMouseFollow();
        loadHotKey();

    }

    private void loadHotKey() {
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
                edit.disableProperty().setValue((conf == null || conf.equals(NoImageStoreConfig.class)));
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
        edit.visibleProperty().setValue(imageStoreRegisterManager.getConfig(globalConfigPersistence.getImageStore()) != null);

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

    private void loadPreView() {
        preview.selectedProperty().setValue(globalConfigPersistence.isPreview());
    }

    private void loadScreenshotMouseFollow() {
        screenshotMouseFollow.selectedProperty().addListener((observable, oldValue, newValue) -> {
            globalConfigPersistence.setScreenshotMouseFollow(newValue);
            configuration.getUniqueBean(ChoseScreenShowValue.class).show.set(newValue);
            configuration.storePersistence(globalConfigPersistence);
        });
        Tooltip tooltip = new Tooltip("开启该功能，使用截图功能时,将会获取鼠标所在屏幕的图像.");
        Tooltip.install(screenshotMouseFollowLabel, tooltip);
        Tooltip.install(screenshotMouseFollow, tooltip);
        screenshotMouseFollow.setSelected(globalConfigPersistence.isScreenshotMouseFollow());
    }

    public void editImageStore() {
        // 获取当前选择的图片存储方式
        String name = (String) imageSave.getValue();
        ImageStore imageStore = imageStoreRegisterManager.getImageStore(name);
        imageStore.config();
    }

    public void editPreView() {
        boolean isPreview = preview.selectedProperty().get();
        if (globalConfigPersistence.isPreview() != isPreview) {
            globalConfigPersistence.setPreview(isPreview);
            configuration.storePersistence(globalConfigPersistence);
        }
    }

    public void back() {
        // 取消
        ((Stage) edit.getScene().getWindow()).close();
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
}