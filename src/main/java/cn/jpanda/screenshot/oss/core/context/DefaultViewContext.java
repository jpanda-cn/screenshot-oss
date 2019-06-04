package cn.jpanda.screenshot.oss.core.context;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.SneakyThrows;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultViewContext implements ViewContext {
    /**
     * 默认的舞台
     */
    private Stage defaultStage;

    private FXMLSearch fxmlSearch;

    public DefaultViewContext(Stage defaultStage, FXMLSearch fxmlSearch) {
        this.defaultStage = defaultStage;
        this.fxmlSearch = fxmlSearch;
    }

    private ConcurrentHashMap<String, Scene> views = new ConcurrentHashMap<>();

    @Override
    public Stage getStage() {
        return defaultStage;
    }

    @Override
    public void closeStage() {
        defaultStage.close();
    }

    @Override
    @SneakyThrows
    public Scene getScene(Class<? extends Initializable> clazz) {
        return getScene(clazz, false, false);

    }

    @Override
    @SneakyThrows
    public Scene getScene(Class<? extends Initializable> clazz, boolean isNew, boolean override) {
        String key = generatorViewKey(clazz);

        if (isNew) {
            if (override) {
                registry(clazz);
            } else {
                return new Scene(FXMLLoader.load(fxmlSearch.search(clazz)));
            }

        }
        if (views.containsKey(key)) {
            return views.get(key);
        }else {
            registry(clazz);
        }

        return views.get(key);
    }


    @Override
    @SneakyThrows
    public boolean registry(Class<? extends Initializable> clazz) {
        return null != views.put(generatorViewKey(clazz), new Scene(FXMLLoader.load(fxmlSearch.search(clazz))));
    }

    @Override
    public <T extends Scene> void showScene(T scene, Stage stage) {
        stage.setScene(scene);
        stage.show();
    }

    protected String generatorViewKey(Class clazz) {
        return clazz.getCanonicalName();
    }
}
