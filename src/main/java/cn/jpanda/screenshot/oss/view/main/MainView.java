package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogHolder;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.save.ImageStoreRegisterManager;
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
import java.util.ResourceBundle;

public class MainView implements Initializable {

    public Button edit;
    public ChoiceBox imageSave;
    public ChoiceBox clipboard;
    public Label shotKey;
    /**
     * 截图预览
     */
    @FXML
    public CheckBox preview;

    private Log log = LogHolder.getInstance().getLogFactory().getLog(getClass());

    private Configuration configuration = BootStrap.configuration;

    private GlobalConfigPersistence globalConfigPersistence;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 加载配置
        globalConfigPersistence = configuration.getDataPersistenceStrategy().load(GlobalConfigPersistence.class);
        loadImageSave();
        loadClipboard();
        loadPreView();
    }

    @SuppressWarnings("unchecked")
    private void loadImageSave() {
        // 初始化存储方式列表
        ImageStoreRegisterManager imageStoreRegisterManager = configuration.getImageStoreRegisterManager();
        imageSave.getItems().addAll(imageStoreRegisterManager.getNames());
        imageSave.getSelectionModel().select(configuration.getImageStore());
        // 判断是否有对应的配置界面，决定是否展示配置按钮
        edit.visibleProperty().setValue(imageStoreRegisterManager.getConfig(configuration.getImageStore()) != null);
        imageSave.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof String) {
                if (!configuration.getImageStore().equalsIgnoreCase((String) newValue)) {
                    configuration.setImageStore((String) newValue);
                    // 判断是否有对应的配置界面，决定是否展示配置按钮
                    edit.visibleProperty().setValue(imageStoreRegisterManager.getConfig((String) newValue) != null);
                }
            }
        });
    }
    @SuppressWarnings("unchecked")
    private void loadClipboard() {
        // 初始化保存到剪切板的内容
        ClipboardCallbackRegistryManager clipboardCallbackRegistryManager = configuration.getClipboardCallbackRegistryManager();
        clipboard.getItems().addAll(clipboardCallbackRegistryManager.getNames());
        clipboard.getSelectionModel().select(configuration.getClipboardCallback());
        clipboard.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof String) {
                if (!configuration.getClipboardCallback().equalsIgnoreCase((String) newValue)) {
                    configuration.setClipboardCallback((String) newValue);
                    // 刷新配置
                }
            }
        });
    }

    private void loadPreView() {
        preview.selectedProperty().setValue(globalConfigPersistence.isPreview());
    }

    public void editImageStore() {
        // 获取当前选择的图片存储方式
        String name = (String) imageSave.getValue();
        ImageStoreRegisterManager imageStoreRegisterManager = configuration.getImageStoreRegisterManager();
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