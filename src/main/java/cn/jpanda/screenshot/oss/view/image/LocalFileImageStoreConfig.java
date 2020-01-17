package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.persistences.LocalImageStorePersistence;
import cn.jpanda.screenshot.oss.store.img.instances.LocalImageStore;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * 本地文件——图片存储配置
 */
@Controller
public class LocalFileImageStoreConfig implements Initializable {
    private Configuration configuration;
    public TextField show;
    public Button chose;
    private LocalImageStorePersistence config;

    public LocalFileImageStoreConfig(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // 加载配置文件
        config = configuration.getPersistence(LocalImageStorePersistence.class);
        if (StringUtils.isEmpty(config.getPath())) {

            config.setPath(Paths.get(configuration.getWorkPath(), "images", "saves").toFile().getAbsolutePath());
            configuration.storePersistence(config);
        }
        show.textProperty().setValue(config.getPath());
        show.editableProperty().setValue(false);
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(show.textProperty());
        show.tooltipProperty().setValue(tooltip);
        configuration.registryUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + LocalImageStore.NAME, (Callable<Boolean, ButtonType>) a -> {
            System.out.println(a);
            if (a.equals(ButtonType.APPLY)) {
                return save();
            }
            return true;
        });
    }

    public void chose() {
        // 获取当前地址
        String path = show.textProperty().get();
        File dir = Paths.get(path).toFile();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (dir.exists() && dir.isDirectory()) {
            directoryChooser.setInitialDirectory(dir);
        }
        directoryChooser.setTitle("请选择本地图片保存地址");

        File file = directoryChooser.showDialog(configuration.getViewContext().newStage());
        if (file == null) {
            return;
        }
        String newPath = file.getAbsolutePath();
        if (StringUtils.isEmpty(newPath)) {
            return;
        }
        if (newPath.equals(path)) {
            return;
        }
        show.textProperty().setValue(newPath);
    }

    public void cancel() {
        // 取消
        ((Stage) show.getScene().getWindow()).close();
    }

    public boolean save() {
        String path = config.getPath();
        String newPath = show.textProperty().get();
        if (StringUtils.isEmpty(newPath)) {
            PopDialogShower.message("未选择路径", show.getScene().getWindow());
        }
        if (newPath.equals(path)) {
            PopDialogShower.message("当前目录未发生变化", show.getScene().getWindow());
            return false;
        }
        config.setPath(newPath);
        configuration.storePersistence(config);
        // 取消
        return true;
    }
}
