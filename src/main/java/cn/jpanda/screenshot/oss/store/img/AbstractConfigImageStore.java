package cn.jpanda.screenshot.oss.store.img;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.store.ExceptionType;
import cn.jpanda.screenshot.oss.store.ExceptionWrapper;
import cn.jpanda.screenshot.oss.store.ImageStoreResult;
import cn.jpanda.screenshot.oss.store.ImageStoreResultHandler;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.Optional;

public abstract class AbstractConfigImageStore implements ImageStore {
    protected Configuration configuration;
    protected ImageStoreRegisterManager imageStoreRegisterManager;

    public AbstractConfigImageStore(Configuration configuration) {
        this.configuration = configuration;
        this.imageStoreRegisterManager = configuration.getUniqueBean(ImageStoreRegisterManager.class);
    }

    @Override
    public void config() {
        // 获取当前选择的图片存储方式
        Class<? extends Initializable> config = imageStoreRegisterManager.getConfig(getName());
        if (config == null) {
            return;
        }
        Scene scene = configuration.getViewContext().getScene(config);
        Stage stage = configuration.getViewContext().newStage();
        stage.getIcons().addAll(configuration.getViewContext().getStage().getIcons());
        stage.setTitle(getName());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public abstract String getName();

    protected boolean pre() {
        // 展示一条提示
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(String.format("【%s】存储方式需要配置【%s】相关参数才可使用", getName(), getName()));
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(new ButtonType("取消", ButtonBar.ButtonData.BACK_PREVIOUS), new ButtonType("配置", ButtonBar.ButtonData.OK_DONE));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                config();
                return canUse();
            } else {
                return false;
            }
        }
        return false;
    }
    protected void addException(BufferedImage image, String path, boolean success, Exception e, ExceptionType exceptionType) {
        e.printStackTrace();
        configuration.getUniqueBean(ImageStoreResultHandler.class).add(ImageStoreResult
                .builder()
                .image(new SimpleObjectProperty<>(image))
                .imageStore(new SimpleStringProperty(getName()))
                .path(new SimpleStringProperty(path))
                .success(new SimpleBooleanProperty(success))
                .exception(new SimpleObjectProperty<>(new ExceptionWrapper(e)))
                .exceptionType(exceptionType)
                .build());
    }
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean check() {
        if (!canUse()) {
            return pre();
        }
        return true;
    }
}
