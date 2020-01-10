package cn.jpanda.screenshot.oss.store.img;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.store.ExceptionType;
import cn.jpanda.screenshot.oss.store.ExceptionWrapper;
import cn.jpanda.screenshot.oss.store.ImageStoreResult;
import cn.jpanda.screenshot.oss.store.ImageStoreResultHandler;
import cn.jpanda.screenshot.oss.store.img.instances.git.GitImageStore;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

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

        HBox header = new HBox();
        Label main = new Label(getName());
        main.setStyle(" -fx-underline: true;-fx-font-weight: bold;");
        header.getChildren().addAll(main);
        Callable<Boolean, ButtonType> callable = configuration.getUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + getName());
        PopDialog.create()
                .setHeader(header)
                .setContent(scene.getRoot())
                .bindParent(configuration.getViewContext().getStage())
                .buttonTypes(ButtonType.CANCEL, ButtonType.APPLY)
                .callback(callable).showAndWait();

    }

    public abstract String getName();

    protected boolean pre() {
        // 展示一条提示
//        Alert alert = new Alert(Alert.AlertType.WARNING);
//        alert.setTitle("警告");
//        alert.setHeaderText(String.format("【%s】存储方式需要配置【%s】相关参数才可使用", getName(), getName()));
//        alert.getButtonTypes().clear();
//        alert.getButtonTypes().addAll(new ButtonType("取消", ButtonBar.ButtonData.BACK_PREVIOUS), new ButtonType("配置", ButtonBar.ButtonData.OK_DONE));
//        Optional<ButtonType> result = alert.showAndWait();

        HBox content = new HBox();
        Label main = new Label(getName());
        main.setStyle(" -fx-underline: true;-fx-font-weight: bold;");
        Label description = new Label("需要进行相关参数配置才可使用");
        content.getChildren().addAll(main, description);
        Optional<ButtonType> result = PopDialog.create().setHeader("提示").setContent(content).showAndWait();

        if (result.isPresent()) {
            if (result.get().equals(PopDialog.CONFIG)) {
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
