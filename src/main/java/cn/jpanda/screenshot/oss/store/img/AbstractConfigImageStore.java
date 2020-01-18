package cn.jpanda.screenshot.oss.store.img;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.store.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

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
    public void config(Window stage) {
        if (stage == null) {
            stage = configuration.getViewContext().getStage();
        }
        // 获取当前选择的图片存储方式
        if (!imageStoreRegisterManager.canConfig(getName())) {
            return;
        }

        Class<? extends ImageStoreConfigBuilder> builder = imageStoreRegisterManager.getBuilder(getName());
        Parent parent = null;
        if (builder != null) {
            parent = configuration.getUniqueBean(builder).load();
        }
        if (parent == null) {
            Class<? extends Initializable> config = imageStoreRegisterManager.getConfig(getName());
            parent = configuration.getViewContext().getScene(config).getRoot();
        }

        HBox header = new HBox();
        Label main = new Label(getName());
        main.setStyle(" -fx-underline: true;-fx-font-weight: bold;");
        header.getChildren().addAll(main);
        Callable<Boolean, ButtonType> callable = configuration.getUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + getName());
        PopDialog.create()
                .setHeader(header)
                .setContent(parent)
                .bindParent(stage)
                .buttonTypes(ButtonType.CANCEL, ButtonType.APPLY)
                .callback(callable).showAndWait();

    }

    public abstract String getName();

    protected boolean pre(Window stage) {
        HBox content = new HBox();
        Label main = new Label(getName());
        main.setStyle(" -fx-underline: true;-fx-font-weight: bold;");
        Label description = new Label("需要进行相关参数配置才可使用");
        content.getChildren().addAll(main, description);
        Optional<ButtonType> result = PopDialog.create().setHeader("提示").setContent(content).bindParent(stage).showAndWait();

        if (result.isPresent()) {
            if (result.get().equals(PopDialog.CONFIG)) {
                config(stage);
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
    public boolean check(Window stage) {
        if (!canUse()) {
            return pre(stage);
        }
        return true;
    }
}
