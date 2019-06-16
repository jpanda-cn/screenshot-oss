package cn.jpanda.screenshot.oss.core.configuration;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.newcore.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.newcore.controller.ViewContext;
import cn.jpanda.screenshot.oss.newcore.persistence.BootstrapPersistence;
import cn.jpanda.screenshot.oss.newcore.persistence.strategy.DataPersistenceStrategy;
import cn.jpanda.screenshot.oss.newcore.persistence.Persistence;
import cn.jpanda.screenshot.oss.newcore.interceptor.ValueInterceptor;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.save.ImageStoreRegisterManager;
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
    private String DEFAULT_IMAGE_STORE = "本地存储";
    private String DEFAULT_CLIPBOARD_CALLBACK = "地址";
    private String DEFAULT_IMAGE_SUFFIX = "png";

    @Getter
    private ImageStoreRegisterManager imageStoreRegisterManager = new ImageStoreRegisterManager();

    @Getter
    private ClipboardCallbackRegistryManager clipboardCallbackRegistryManager = new ClipboardCallbackRegistryManager();

    @Getter
    @Setter
    private Map<Class<? extends Persistence>, Persistence> persistences = new ConcurrentHashMap<>();

    private Map<Class<? extends ValueInterceptor>, List<ValueInterceptor>> interceptorMap = new ConcurrentHashMap<>();

    public <T extends ValueInterceptor> void registryInterceptor(Class<? extends ValueInterceptor> clazz, T interceptor) {
        List<ValueInterceptor> valueInterceptors = getInterceptor(clazz);
        if (!valueInterceptors.contains(interceptor)) {
            valueInterceptors.add(interceptor);
        }
        interceptorMap.put(clazz, valueInterceptors);
    }

    @SuppressWarnings("unchecked")
    public <T extends ValueInterceptor> List<T> getInterceptor(Class<? extends ValueInterceptor> c) {
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
        // 存储图片
        String path = imageStoreRegisterManager.getImageStore(getImageStore()).store(image);
        // 回调剪切板
        getClipboardCallbackRegistryManager().get(getClipboardCallback()).callback(image, path);
    }

    public String getImageStore() {

        GlobalConfigPersistence globalConfigPersistence = getPersistence(GlobalConfigPersistence.class);
        String imageStore = globalConfigPersistence.getImageStore();
        if (StringUtils.isNotEmpty(imageStore) && imageStoreRegisterManager.getNames().contains(imageStore)) {
            return imageStore;
        }
        return this.DEFAULT_IMAGE_STORE;
    }

    public void setImageStore(String imageStore) {
        GlobalConfigPersistence globalConfigPersistence = getPersistence(GlobalConfigPersistence.class);
        if (StringUtils.isNotEmpty(imageStore) && imageStoreRegisterManager.getNames().contains(imageStore)) {
            globalConfigPersistence.setImageStore(imageStore);
            storePersistence(globalConfigPersistence);
        }
    }

    public String getClipboardCallback() {
        GlobalConfigPersistence globalConfigPersistence = getPersistence(GlobalConfigPersistence.class);
        String clipboardCallback = globalConfigPersistence.getClipboardCallback();
        if (StringUtils.isNotEmpty(clipboardCallback) && clipboardCallbackRegistryManager.getNames().contains(clipboardCallback)) {
            return clipboardCallback;
        }
        return this.DEFAULT_CLIPBOARD_CALLBACK;
    }

    public void setClipboardCallback(String clipboardCallback) {
        GlobalConfigPersistence globalConfigPersistence = getPersistence(GlobalConfigPersistence.class);
        if (StringUtils.isNotEmpty(clipboardCallback) && clipboardCallbackRegistryManager.getNames().contains(clipboardCallback)) {
            globalConfigPersistence.setClipboardCallback(clipboardCallback);
            storePersistence(globalConfigPersistence);
        }
    }

    public String getImageSuffix() {
        GlobalConfigPersistence globalConfigPersistence = getPersistence(GlobalConfigPersistence.class);
        String imageSuffix = globalConfigPersistence.getImageSuffix();
        if (StringUtils.isNotEmpty(imageSuffix)) {
            return imageSuffix;
        }
        return DEFAULT_IMAGE_SUFFIX;
    }

    public void setImageSuffix(String imageSuffix) {
        GlobalConfigPersistence globalConfigPersistence = getPersistence(GlobalConfigPersistence.class);
        if (StringUtils.isNotEmpty(imageSuffix)) {
            globalConfigPersistence.setImageSuffix(imageSuffix);
            storePersistence(globalConfigPersistence);
        }
    }
}
