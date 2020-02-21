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
    public ComboBox<IconLabel> imageSave;
    @FXML
    public ComboBox<IconLabel> clipboard;

    private ImageStoreRegisterManager imageStoreRegisterManager;
    private ClipboardCallbackRegistryManager clipboardCallbackRegistryManager;
    private GlobalConfigPersistence globalConfigPersistence;
    private SimpleStringProperty cliProperty;
    private SimpleStringProperty imageProperty;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cliProperty = configuration.getUniquePropertiesHolder(GlobalConfigPersistence.class.getCanonicalName() + "-" + "clipboard-save", new SimpleStringProperty());
        imageProperty = configuration.getUniquePropertiesHolder(GlobalConfigPersistence.class.getCanonicalName() + "-" + "image-save", new SimpleStringProperty());
        // 加载配置
        imageStoreRegisterManager = configuration.getUniqueBean(ImageStoreRegisterManager.class);
        clipboardCallbackRegistryManager = configuration.getUniqueBean(ClipboardCallbackRegistryManager.class);
        globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        loadImageSave();
        loadClipboard();
        cliProperty.addListener((observable, oldValue, newValue) -> {
            clipboard.getSelectionModel().select(clipboardCallbackRegistryManager.getIconLabel(newValue));
        });
        imageProperty.addListener((observable, oldValue, newValue) -> {
            imageSave.getSelectionModel().select(imageStoreRegisterManager.getIconLabel(newValue));
        });

    }

    @SuppressWarnings("unchecked")
    private void loadImageSave() {
        imageSave.setCellFactory(c -> new IconListCell());
        imageSave.setButtonCell(new IconListCell());

        // 监听事件
        imageSave.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
             if(newValue==null){
                 // 修复清空存储方式导致的空指针异常
                 return;
             }
            String name=imageStoreRegisterManager.getName(newValue);
            globalConfigPersistence.setImageStore(name);
            // 联动，剪切板的内容同步发生变化
            ImageStore imageStore = imageStoreRegisterManager.getImageStore(name);
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
            boolean canConfig=imageStoreRegisterManager.canConfig(name);
            edit.disableProperty().setValue((!canConfig));
            edit.visibleProperty().setValue((canConfig));
            ImageType type = imageStoreRegisterManager.getType(name);
            List<IconLabel> cls;
            switch (type) {
                case NO_PATH: {
                    cls = clipboardCallbackRegistryManager.getNamesByType(ClipboardType.NOT_NEED);
                    break;
                }
                case HAS_PATH: {
                    cls = clipboardCallbackRegistryManager.getIconLabels();
                    break;
                }
                default: {
                    cls = clipboardCallbackRegistryManager.getIconLabels();
                }
            }
            // 更新剪切板
            clipboard.getItems().clear();
            clipboard.getItems().addAll(cls);
            // 校验之前选中的是否还可用，不可用使用第一个
            IconLabel cli=clipboardCallbackRegistryManager.getIconLabel(globalConfigPersistence.getClipboardCallback());
            if (cls.contains(cli)) {
                clipboard.getSelectionModel().select(cli);
            } else {
                clipboard.getSelectionModel().select(0);
                globalConfigPersistence.setClipboardCallback( clipboard.getItems().get(0).getText());
            }
            configuration.storePersistence(globalConfigPersistence);


            cliProperty.set(globalConfigPersistence.getClipboardCallback());
            imageProperty.set(globalConfigPersistence.getImageStore());


        });

        // 初始化存储方式列表
        imageSave.getItems().clear();
        imageSave.getItems().addAll(imageStoreRegisterManager.getIconLabels());
        imageSave.getSelectionModel().select(imageStoreRegisterManager.getIconLabel(globalConfigPersistence.getImageStore()));
        // 判断是否有对应的配置界面，决定是否展示配置按钮
        boolean canConfig=imageStoreRegisterManager.canConfig(globalConfigPersistence.getImageStore());
        edit.disableProperty().setValue((!canConfig));
        edit.visibleProperty().setValue((canConfig));
    }

    @SuppressWarnings("unchecked")
    private void loadClipboard() {
        clipboard.setCellFactory(c -> new IconListCell());
        clipboard.setButtonCell(new IconListCell());
        clipboard.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String name=newValue.getText();
                if (!globalConfigPersistence.getClipboardCallback().equalsIgnoreCase(name)) {
                    globalConfigPersistence.setClipboardCallback(name);
                    // 刷新配置
                    // 联动
                    ClipboardType clipboardType = clipboardCallbackRegistryManager.getType(name);
                    List<IconLabel> is;
                    switch (clipboardType) {
                        case NOT_NEED: {
                            is = imageStoreRegisterManager.getIconLabels();
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
                    IconLabel b=imageStoreRegisterManager.getIconLabel(globalConfigPersistence.getImageStore());
                    if (is.contains( b)) {
                        imageSave.getSelectionModel().select( b);
                    } else {
                        imageSave.getSelectionModel().select(0);
                        globalConfigPersistence.setImageStore(imageStoreRegisterManager.getName(imageSave.getItems().get(0)) );
                    }
                    configuration.storePersistence(globalConfigPersistence);

                    cliProperty.set(globalConfigPersistence.getClipboardCallback());

                    imageProperty.set(globalConfigPersistence.getImageStore());
                }
            }
        });
        clipboard.getItems().clear();
        // 初始化保存到剪切板的内容
        clipboard.getItems().addAll(clipboardCallbackRegistryManager.getIconLabels());
        clipboard.getSelectionModel().select(clipboardCallbackRegistryManager.getIconLabel(globalConfigPersistence.getClipboardCallback()));

    }


    public void editImageStore() {
        // 获取当前选择的图片存储方式
        IconLabel box=imageSave.getValue();

        String name = imageStoreRegisterManager.getName(box);
        ImageStore imageStore = imageStoreRegisterManager.getImageStore(name);
        imageStore.config(edit.getScene().getWindow());
    }


}