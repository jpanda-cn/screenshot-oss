package cn.jpanda.screenshot.oss.store.img;

import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.store.ImageStoreConfigBuilder;
import cn.jpanda.screenshot.oss.store.NoImageStoreConfigBuilder;
import javafx.fxml.Initializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageStoreRegisterManager {
    private Log log;
    private Configuration configuration;

    public ImageStoreRegisterManager(Configuration configuration) {
        this.configuration = configuration;
        log = configuration.getLogFactory().getLog(getClass());
    }

    /**
     * 图片存储渠道名称和实现的关系
     */
    private Map<String, ImageStore> nameMap = new HashMap<>();

    private Map<ImageType, List<String>> typeMap = new HashMap<>();

    /**
     * 渠道以及配置面板的关系
     */
    private Map<String, Class<? extends Initializable>> configs = new HashMap<>();
    private Map<String, Class<? extends ImageStoreConfigBuilder>> builders = new HashMap<>();
    private Map<String, ImageType> name2type = new HashMap<>();

    private Map<String, Boolean> canConfig = new HashMap<>();

    /**
     * 获取所有渠道名称
     */
    public List<String> getNames() {
        return new ArrayList<>(nameMap.keySet());
    }

    public void registry(ImgStore imgStore, ImageStore imageStore) {
        String name = imgStore.name();
        ImageType type = imgStore.type();
        log.debug("registry new imageStore named:{0}, handle type is {1}.", name, type);
        if (nameMap.keySet().contains(name)) {
            log.err("find two beans with the same name :{0}", name);
            return;
        }
        nameMap.put(name, imageStore);
        List<String> names = typeMap.computeIfAbsent(type, k -> new ArrayList<>());
        names.add(name);
        Class<? extends Initializable> config = imgStore.config();
        boolean can = false;
        if (!NoImageStoreConfig.class.equals(config)) {
            configs.put(name, config);
            can = true;
        }
        if (!imgStore.builder().equals(NoImageStoreConfigBuilder.class)) {
            builders.put(name, imgStore.builder());
            ImageStoreConfigBuilder builder = configuration.createBeanInstance(ImageStoreConfigBuilder.class).instance(imgStore.builder());
            configuration.registryUniqueBean(imgStore.builder(), builder);
            can = true;
        }
        canConfig.put(name, can);
        name2type.put(name, type);
    }

    public ImageStore getImageStore(String name) {
        return nameMap.get(name);
    }

    public Class<? extends Initializable> getConfig(String name) {
        return configs.get(name);
    }

    public Class<? extends ImageStoreConfigBuilder> getBuilder(String name) {
        return builders.get(name);
    }

    public List<String> getNamesByType(ImageType imageType) {
        return typeMap.getOrDefault(imageType, new ArrayList<>());
    }

    public boolean canConfig(String name) {
        return canConfig.getOrDefault(name, false);
    }

    public ImageType getType(String name) {
        return name2type.get(name);
    }
}
