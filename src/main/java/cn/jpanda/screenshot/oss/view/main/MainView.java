package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.core.ImageStoreRegisterManager;
import cn.jpanda.screenshot.oss.core.annotations.FX;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogHolder;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

@FX
public class MainView implements Initializable {

    public Button edit;
    public ChoiceBox imageSave;
    public ChoiceBox clipboard;
    public Label shotKey;
    private Log log = LogHolder.getInstance().getLogFactory().getLog(getClass());

    private Configuration configuration = BootStrap.configuration;

    private MainViewConfig mainViewConfig;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 加载配置
        mainViewConfig = configuration.getDataPersistenceStrategy().load(MainViewConfig.class);
        loadImageSave();
        loadClipboard();
    }

    @SuppressWarnings("unchecked")
    private void loadImageSave() {
        // 初始化存储方式列表
        ImageStoreRegisterManager imageStoreRegisterManager = configuration.getImageStoreRegisterManager();
        imageSave.getItems().addAll(imageStoreRegisterManager.getNames());
        imageSave.getSelectionModel().select(configuration.getImageStore());
    }

    @SuppressWarnings("unchecked")
    private void loadClipboard() {
        // 初始化保存到剪切板的内容
        ClipboardCallbackRegistryManager clipboardCallbackRegistryManager = configuration.getClipboardCallbackRegistryManager();
        clipboard.getItems().addAll(clipboardCallbackRegistryManager.getNames());
        clipboard.getSelectionModel().select(configuration.getClipboardCallback());
    }

    public void editImageStore(MouseEvent event) {
        // 获取当前选择的图片存储方式
        String name = (String) imageSave.getValue();
        ImageStoreRegisterManager imageStoreRegisterManager = configuration.getImageStoreRegisterManager();
        Class<? extends Initializable> config = imageStoreRegisterManager.getConfig(name);
        Scene scene = configuration.getViewContext().getScene(config);
        Stage stage = new Stage();
        stage.getIcons().addAll(configuration.getViewContext().getStage().getIcons());
        stage.setTitle(name);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
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