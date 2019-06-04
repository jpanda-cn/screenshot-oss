package cn.jpanda.screenshot.oss.core.configuration;

import cn.jpanda.screenshot.oss.core.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.core.ImageStoreRegisterManager;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.context.ViewContext;
import cn.jpanda.screenshot.oss.core.persistence.BootstrapPersistence;
import cn.jpanda.screenshot.oss.core.persistence.DataPersistenceStrategy;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.Interceptor;
import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {
    @Getter
    @Setter
    private String password;
    /**
     * 当前工作目录
     */
    @Getter
    @Setter
    private String currentWorkDir;
    /**
     * 主配置文件名称
     */
    @Getter
    @Setter
    private String mainConfigFileName;

    /**
     * 系统引导配置类
     */
    @Getter
    @Setter
    private BootstrapPersistence bootstrapPersistence;

    /**
     * 数据持久化策略
     */
    @Getter
    @Setter
    private DataPersistenceStrategy dataPersistenceStrategy;
    @Getter
    @Setter
    private DataPersistenceStrategy bootstrapDataPersistenceStrategy;

    @Getter
    @Setter
    private Persistence persistence;

    /**
     * 桌面截图获取接口
     */
    @Getter
    @Setter
    private ScreenCapture screenCapture;

    /**
     * 视图上下文
     */
    @Getter
    @Setter
    private ViewContext viewContext;

    // 当前使用的图片存储类
    @Getter
    @Setter
    private String imageStore = "本地存储";
    @Getter
    @Setter
    private String clipboardCallback = "地址";
    @Getter
    @Setter
    private String imageSuffix = "png";

    @Getter
    private ImageStoreRegisterManager imageStoreRegisterManager = new ImageStoreRegisterManager();

    @Getter
    private ClipboardCallbackRegistryManager clipboardCallbackRegistryManager = new ClipboardCallbackRegistryManager();

    @Getter
    @Setter
    private Map<Class<? extends Persistence>, Persistence> persistences = new ConcurrentHashMap<>();

    private Map<Class<? extends Interceptor>, List<Interceptor>> interceptorMap = new ConcurrentHashMap<>();

    public <T extends Interceptor> void registryInterceptor(Class<? extends Interceptor> clazz, T interceptor) {
        List<Interceptor> interceptors = getInterceptor(clazz);
        if (!interceptors.contains(interceptor)) {
            interceptors.add(interceptor);
        }
        interceptorMap.put(clazz, interceptors);
    }

    @SuppressWarnings("unchecked")
    public <T extends Interceptor> List<T> getInterceptor(Class<? extends Interceptor> c) {
        return (List<T>) interceptorMap.getOrDefault(c, new ArrayList<>());
    }

    /**
     * 获取主配置文件的完全名称
     */
    public String getMainConfigFileFullName() {
        return currentWorkDir + File.separator + mainConfigFileName;
    }

    /**
     * 获取配置文件全路径
     *
     * @param name 配置文件名称
     */
    public String getConfigFile(String name) {
        return currentWorkDir + File.separator + name;
    }

    @SuppressWarnings("unchecked")
    public <T extends Persistence> T getPersistence(Class<T> p) {
        if (persistences.containsKey(p)) {
            return (T) persistences.get(p);
        }
        T persistence = dataPersistenceStrategy.load(p);
        persistences.put(p, persistence);
        return persistence;
    }

    public void storePersistence(Persistence p) {
        dataPersistenceStrategy.store(p);
    }

    public boolean usePassword() {
        return bootstrapPersistence.isUsePassword();
    }

    public void setUsePassword(boolean use) {
        bootstrapPersistence.setUsePassword(use);
        getBootstrapDataPersistenceStrategy().store(bootstrapPersistence);
    }

    public Integer getUserCount() {
        return bootstrapPersistence.getUseCount();
    }

    public void updateUserCount() {
        bootstrapPersistence.updateUseCount();
        getBootstrapDataPersistenceStrategy().store(bootstrapPersistence);
    }

    public void store(BufferedImage image) {
        imageStoreRegisterManager.getImageStore(imageStore).store(image);
    }
}
