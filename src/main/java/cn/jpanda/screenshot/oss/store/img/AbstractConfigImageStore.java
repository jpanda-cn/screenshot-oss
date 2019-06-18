package cn.jpanda.screenshot.oss.store.img;

import cn.jpanda.screenshot.oss.core.Configuration;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class AbstractConfigImageStore implements ImageStore{
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
        Stage stage = new Stage();
        stage.getIcons().addAll(configuration.getViewContext().getStage().getIcons());
        stage.setTitle(getName());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }
    public abstract String getName();
}
