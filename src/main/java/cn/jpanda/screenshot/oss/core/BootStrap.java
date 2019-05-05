package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.common.utils.JarUtils;
import cn.jpanda.screenshot.oss.core.capture.DefaultScreenCapture;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.context.DefaultViewContext;
import cn.jpanda.screenshot.oss.core.context.FXAnnotationSameNameFXMLSearch;
import cn.jpanda.screenshot.oss.core.persistence.CachedPropertiesVisitor;
import cn.jpanda.screenshot.oss.core.persistence.DefaultPropertiesVisitor;
import cn.jpanda.screenshot.oss.core.persistence.PropertiesDataPersistenceStrategy;
import cn.jpanda.screenshot.oss.core.persistence.PropertiesVisitor;
import cn.jpanda.screenshot.oss.core.scan.DefaultClassScan;
import cn.jpanda.screenshot.oss.core.scan.ViewAndImplInitClassScanFilter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public abstract class BootStrap extends Application {
    public static Configuration configuration = new Configuration();

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 初始化工作目录
        loadCurrentWorkDir();
        // 初始化主配置文件名称
        loadMainConfigFileName();
        // 加载配置文件持久操作策略类
        loadDataPersistenceStrategy();
        // 加载屏幕截图实现类
        loadScreenCapture();
        // 加载视图上下文
        loadViewContext(primaryStage);
        // 注册场景
        registryScene();

        // 启用方法
        doStart();
    }


    protected void loadCurrentWorkDir() {
        configuration.setCurrentWorkDir(JarUtils.getCurrentJarDirectory());
    }

    protected void loadMainConfigFileName() {
        configuration.setMainConfigFileName("jpanda-screenshot-oss.properties");
    }

    protected void loadDataPersistenceStrategy() {
        PropertiesVisitor propertiesVisitor = new CachedPropertiesVisitor(new DefaultPropertiesVisitor());
        configuration.setDataPersistenceStrategy(new PropertiesDataPersistenceStrategy(configuration.getMainConfigFileFullName(), propertiesVisitor));
    }

    protected void loadScreenCapture() {
        configuration.setScreenCapture(new DefaultScreenCapture());
    }

    protected void loadViewContext(Stage primaryStage) {
        configuration.setViewContext(new DefaultViewContext(primaryStage, new FXAnnotationSameNameFXMLSearch(FXMLLoader.getDefaultClassLoader())));
    }

    protected void registryScene() {
        // 注册所有的场景
        for (Class clazz : new DefaultClassScan(new ViewAndImplInitClassScanFilter()).scan(getClass()).loadResult()) {
            if (Initializable.class.isAssignableFrom(clazz)) {
                System.out.println(clazz);
                configuration.getViewContext().registry(clazz);
            }
        }
    }

    protected abstract void doStart();
}
