package cn.jpanda.screenshot.oss.core.controller;

import cn.jpanda.screenshot.oss.core.Configuration;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.SneakyThrows;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultViewContext implements ViewContext {
    /**
     * 默认的舞台
     */
    private Stage defaultStage;

    private FXMLSearch fxmlSearch;

    private Configuration configuration;

    public DefaultViewContext(Stage defaultStage, FXMLSearch fxmlSearch, Configuration configuration) {
        this.defaultStage = defaultStage;
        this.fxmlSearch = fxmlSearch;
        this.configuration = configuration;
    }

    private ConcurrentHashMap<String, Scene> views = new ConcurrentHashMap<>();

    @Override
    public Stage getStage() {
        return defaultStage;
    }

    @Override
    public Stage newStage() {
        Stage stage = new Stage();
        stage.getIcons().add(new Image("/images/icon.png"));
        return stage;
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

                return loadScene(clazz);
            }

        }
        if (views.containsKey(key)) {
            return views.get(key);
        } else {
            registry(clazz);
        }

        return views.get(key);
    }


    @Override
    @SneakyThrows
    public boolean registry(Class<? extends Initializable> clazz) {
        return null != views.put(generatorViewKey(clazz), loadScene(clazz));
    }

    @Override
    public <T extends Scene> void showScene(T scene, Stage stage) {

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    protected String generatorViewKey(Class clazz) {
        return clazz.getCanonicalName();
    }

    @SneakyThrows
    protected Parent load(Class<? extends Initializable> clazz) {
        // 主要作用是为其提供注入Configuration对象的能力
        return FXMLLoader.load(fxmlSearch.search(clazz), null, null, new InjectControllerCallback(configuration));
    }

    protected Scene loadScene(Class<? extends Initializable> clazz) {
        return new Scene(load(clazz));
    }
}
