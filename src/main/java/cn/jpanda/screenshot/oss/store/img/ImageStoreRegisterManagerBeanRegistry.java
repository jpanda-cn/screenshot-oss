package cn.jpanda.screenshot.oss.store.img;

import cn.jpanda.screenshot.oss.core.BeanRegistry;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.core.toolkit.BeanInstance;
import cn.jpanda.screenshot.oss.store.ImageStoreConfigBuilder;

@Component
public class ImageStoreRegisterManagerBeanRegistry implements BeanRegistry {
    private Configuration configuration;
    private BeanInstance<ImageStore> beanInstance;

    public ImageStoreRegisterManagerBeanRegistry(Configuration configuration) {
        this.configuration = configuration;
        this.beanInstance = configuration.createBeanInstance(ImageStore.class);
    }

    @Override
    public void doRegistry(Class c) {
        if (ImageStore.class.isAssignableFrom(c)) {
            ImageStoreRegisterManager imageStoreRegisterManager = configuration.getUniqueBean(ImageStoreRegisterManager.class);
            if (imageStoreRegisterManager == null) {
                imageStoreRegisterManager = new ImageStoreRegisterManager(configuration);
                configuration.registryUniqueBean(ImageStoreRegisterManager.class, imageStoreRegisterManager);
            }

            ImgStore imgStore = (ImgStore) c.getDeclaredAnnotation(ImgStore.class);
            if (imgStore != null) {
                imageStoreRegisterManager.registry(imgStore, beanInstance.instance(c));
            }

        }
    }
}
