package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.newcore.Configuration;
import cn.jpanda.screenshot.oss.newcore.annotations.Controller;
import cn.jpanda.screenshot.oss.persistences.LocalImageStorePersistence;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
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
    public Button cancel;
    public Button save;
    private LocalImageStorePersistence config;

    public LocalFileImageStoreConfig(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // 加载配置文件
        config = configuration.getPersistence(LocalImageStorePersistence.class);
        if (StringUtils.isEmpty(config.getPath())) {
            config.setPath(configuration.getWorkPath() + File.separator + "images/saves" + File.separator);
            configuration.storePersistence(config);
        }
        show.textProperty().setValue(config.getPath());
        show.editableProperty().setValue(false);
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(show.textProperty());
        show.tooltipProperty().setValue(tooltip);
    }

    public void chose() {
        // 获取当前地址
        String path = show.textProperty().get();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(Paths.get(path).toFile());
        directoryChooser.setTitle("请选择本地图片保存地址");
        String newPath = directoryChooser.showDialog(new Stage()).getAbsolutePath();
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

    public void save() {
        String path = config.getPath();
        String newPath = show.textProperty().get();
        if (StringUtils.isEmpty(newPath)) {
            return;
        }
        if (newPath.equals(path)) {
            return;
        }
        config.setPath(newPath);
        configuration.storePersistence(config);
        // 取消
        ((Stage) show.getScene().getWindow()).close();
    }
}
