package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.common.utils.JarUtils;
import cn.jpanda.screenshot.oss.core.capture.DefaultScreenCapture;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.context.DefaultViewContext;
import cn.jpanda.screenshot.oss.core.context.FXAnnotationSameNameFXMLSearch;
import cn.jpanda.screenshot.oss.core.log.*;
import cn.jpanda.screenshot.oss.core.persistence.*;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.GetValueInterceptor;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.SetValueInterceptor;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.encrypt.EncryptGetValueInterceptor;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.encrypt.EncryptSetValueInterceptor;
import cn.jpanda.screenshot.oss.core.scan.BootStrapClassScanRegistry;
import cn.jpanda.screenshot.oss.core.scan.SceneBeanRegistry;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegister;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.clipboard.ImageClipboardCallback;
import cn.jpanda.screenshot.oss.store.clipboard.LocalPathClipboardCallback;
import cn.jpanda.screenshot.oss.store.save.ImageStoreRegister;
import cn.jpanda.screenshot.oss.store.save.ImageStoreRegisterManager;
import cn.jpanda.screenshot.oss.store.save.NothingImageStore;
import cn.jpanda.screenshot.oss.view.image.LocalFileImageStoreConfig;
import cn.jpanda.screenshot.oss.store.save.LocalImageStore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.nio.file.Paths;

public abstract class BootStrap extends Application {
    public static Configuration configuration = new Configuration();

    @Override
    public void start(Stage primaryStage) {
        // 加载视图上下文
        loadViewContext(primaryStage);
        bootstrap();
        // 初始化日志
        if (doBootStrap()) {
            // 启用方法
            doStart();
        }
    }

    /**
     * 程序引导入口
     */
    protected void bootstrap() {
        // 初始化日志工具
        initLog();
        // 初始化工作目录
        loadCurrentWorkDir();
        // 初始化主配置文件名称
        loadMainConfigFileName();
        // 注册属性拦截器
        registryValueInterceptor();
        // 初始化类加载器，获取类加载器并执行注册操作
        // 初始化全局配置文件
        configuration.setBootstrapDataPersistenceStrategy(new PropertiesDataPersistenceStrategy(configuration.getConfigFile("bootsrap.properties"), new DefaultPropertiesVisitor(), configuration));
        configuration.setBootstrapPersistence(configuration.getBootstrapDataPersistenceStrategy().load(BootstrapPersistence.class));
        // 加载配置文件持久操作策略类
        loadDataPersistenceStrategy();
        // 加载屏幕截图实现类
        loadScreenCapture();
        registryImageStoreChannel(configuration.getImageStoreRegisterManager());
        registryClipboardCallback(configuration.getClipboardCallbackRegistryManager());
        // 类扫描加载注册器
        doBeanRegistry();

    }

    protected void registryClipboardCallback(ClipboardCallbackRegistryManager clipboardCallbackRegistryManager) {
        clipboardCallbackRegistryManager.registry(ClipboardCallbackRegister.builder().name("地址").clipboardCallback(new LocalPathClipboardCallback()).build());
        clipboardCallbackRegistryManager.registry(ClipboardCallbackRegister.builder().name("图片").clipboardCallback(new ImageClipboardCallback()).build());
    }

    protected void registryImageStoreChannel(ImageStoreRegisterManager imageStoreRegisterManager) {
        imageStoreRegisterManager.registry(ImageStoreRegister.builder().name("本地存储").imageStore(new LocalImageStore()).imageConfig(LocalFileImageStoreConfig.class).build());
        imageStoreRegisterManager.registry(ImageStoreRegister.builder().name("不存储").imageStore(new NothingImageStore()).build());
    }

    protected void doBeanRegistry() {
        BootStrapClassScanRegistry bootStrapClassScanRegistry = new BootStrapClassScanRegistry();
        registryBeanRegistry(bootStrapClassScanRegistry);
        bootStrapClassScanRegistry.doRegistry(getClass());
    }

    protected void registryValueInterceptor() {
        configuration.registryInterceptor(SetValueInterceptor.class, new EncryptSetValueInterceptor(configuration));
        configuration.registryInterceptor(GetValueInterceptor.class, new EncryptGetValueInterceptor(configuration));
    }

    protected void registryBeanRegistry(BootStrapClassScanRegistry bootStrapClassScanRegistry) {
        bootStrapClassScanRegistry.add(new SceneBeanRegistry(configuration));
    }

    protected void initLog() {
        DefaultOutLogConfig defaultOutLogConfig = new DefaultOutLogConfig(Loglevel.TRACE);
        LogFactory logFactory = new DefaultOutLogFactory(defaultOutLogConfig);
        LogHolder.getInstance().initLogFactory(logFactory);
    }

    protected void loadCurrentWorkDir() {
        configuration.setCurrentWorkDir(Paths.get(JarUtils.getCurrentJarDirectory()).toFile().getAbsolutePath());
    }

    protected void loadMainConfigFileName() {
        configuration.setMainConfigFileName("jpanda-screenshot-oss.properties");
    }

    protected void loadDataPersistenceStrategy() {
        PropertiesVisitor propertiesVisitor = new CachedPropertiesVisitor(new DefaultPropertiesVisitor());
        configuration.setDataPersistenceStrategy(new PropertiesDataPersistenceStrategy(configuration.getMainConfigFileFullName(), propertiesVisitor, configuration));
    }

    protected void loadScreenCapture() {
        configuration.setScreenCapture(new DefaultScreenCapture());
    }

    protected void loadViewContext(Stage primaryStage) {
        configuration.setViewContext(new DefaultViewContext(primaryStage, new FXAnnotationSameNameFXMLSearch(FXMLLoader.getDefaultClassLoader())));
    }

    protected abstract boolean doBootStrap();

    protected abstract void doStart();
}
