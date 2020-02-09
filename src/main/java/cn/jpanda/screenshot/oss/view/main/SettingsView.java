package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.common.enums.ClipboardType;
import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.img.ImageStore;
import cn.jpanda.screenshot.oss.store.img.ImageStoreRegisterManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Window;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Controller
public class SettingsView implements Initializable {
    private Configuration configuration;

    public SettingsView(Configuration configuration) {
        this.configuration = configuration;
    }

    public Button edit;
    public ComboBox imageSave;
    @FXML
    public ComboBox clipboard;

    private ImageStoreRegisterManager imageStoreRegisterManager;
    private ClipboardCallbackRegistryManager clipboardCallbackRegistryManager;
    private GlobalConfigPersistence globalConfigPersistence;
    private SimpleStringProperty cliProperty;
    private SimpleStringProperty imageProperty;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cliProperty = configuration.getUniquePropertiesHolder(GlobalConfigPersistence.class.getCanonicalName() + "-" + "clipboard-save",new SimpleStringProperty());
        imageProperty = configuration.getUniquePropertiesHolder(GlobalConfigPersistence.class.getCanonicalName() + "-" + "image-save",new SimpleStringProperty());
        // 加载配置
        imageStoreRegisterManager = configuration.getUniqueBean(ImageStoreRegisterManager.class);
        clipboardCallbackRegistryManager = configuration.getUniqueBean(ClipboardCallbackRegistryManager.class);
        globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        loadImageSave();
        loadClipboard();
        cliProperty.addListener((observable, oldValue, newValue) -> {
            //noinspection unchecked
            clipboard.getSelectionModel().select(newValue);
        });
        imageProperty.addListener((observable, oldValue, newValue) -> {
            //noinspection unchecked
            imageSave.getSelectionModel().select(newValue);
        });

    }

    @SuppressWarnings("unchecked")
    private void loadImageSave() {
        // 监听事件
        imageSave.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof String) {
                globalConfigPersistence.setImageStore((String) newValue);

                // 联动，剪切板的内容同步发生变化
                ImageStore imageStore = imageStoreRegisterManager.getImageStore((String) newValue);
                Window stage = configuration.getViewContext().getStage();
                if (Optional.ofNullable(edit.getScene()).isPresent()) {
                    stage = edit.getScene().getWindow();
                }
                if (!imageStore.check(stage)) {
                    // 会出现异常，但是不影响正常业务
                    // 2019年6月22日21:48:55 修复角标越界异常
                    // 2020年1月11日17:07:24 调整 若选择的图片存储方式不可使用，恢复到上一次选择的方式
                    Platform.runLater(new Thread(() -> imageSave.getSelectionModel().select(oldValue)));
                    return;
                }
                // 判断是否有对应的配置界面，决定是否展示配置按钮
                edit.disableProperty().setValue((!imageStoreRegisterManager.canConfig((String) newValue)));
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


                cliProperty.set(globalConfigPersistence.getClipboardCallback());
                imageProperty.set(globalConfigPersistence.getImageStore());


            }
        });

        // 初始化存储方式列表
        imageSave.getItems().clear();
        imageSave.getItems().addAll(imageStoreRegisterManager.getNames());
        imageSave.getSelectionModel().select(globalConfigPersistence.getImageStore());
        // 判断是否有对应的配置界面，决定是否展示配置按钮
        edit.visibleProperty().setValue(imageStoreRegisterManager.canConfig(globalConfigPersistence.getImageStore()));

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

                    cliProperty.set(globalConfigPersistence.getClipboardCallback());

                    imageProperty.set(globalConfigPersistence.getImageStore());
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
        imageStore.config(edit.getScene().getWindow());
    }
}