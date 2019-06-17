package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.common.enums.ClipboardType;
import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.newcore.Configuration;
import cn.jpanda.screenshot.oss.newcore.annotations.Controller;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.img.ImageStoreRegisterManager;
import cn.jpanda.screenshot.oss.store.img.NoImageStoreConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class MainView implements Initializable {
    private Configuration configuration;

    public MainView(Configuration configuration) {
        this.configuration = configuration;
    }

    public Button edit;
    public ChoiceBox imageSave;
    @FXML
    public ChoiceBox clipboard;
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
    }

    @SuppressWarnings("unchecked")
    private void loadImageSave() {
        // 监听事件
        imageSave.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof String) {
                if (!globalConfigPersistence.getImageStore().equalsIgnoreCase((String) newValue)) {
                    globalConfigPersistence.setImageStore((String) newValue);
                    // 判断是否有对应的配置界面，决定是否展示配置按钮
                    Class<? extends Initializable> conf = imageStoreRegisterManager.getConfig((String) newValue);
                    edit.disableProperty().setValue((conf == null || conf.equals(NoImageStoreConfig.class)));
                    // 联动，剪切板的内容同步发生变化
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
                    }
                }
            }
        });

        // 初始化存储方式列表
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
                    }
                }
            }
        });
        // 初始化保存到剪切板的内容
        clipboard.getItems().addAll(clipboardCallbackRegistryManager.getNames());
        clipboard.getSelectionModel().select(globalConfigPersistence.getClipboardCallback());

    }

    private void loadPreView() {
        preview.selectedProperty().setValue(globalConfigPersistence.isPreview());
    }

    public void editImageStore() {
        // 获取当前选择的图片存储方式
        String name = (String) imageSave.getValue();
        Class<? extends Initializable> config = imageStoreRegisterManager.getConfig(name);
        if (config == null) {
            return;
        }
        Scene scene = configuration.getViewContext().getScene(config);
        Stage stage = new Stage();
        stage.getIcons().addAll(configuration.getViewContext().getStage().getIcons());
        stage.setTitle(name);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
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
}