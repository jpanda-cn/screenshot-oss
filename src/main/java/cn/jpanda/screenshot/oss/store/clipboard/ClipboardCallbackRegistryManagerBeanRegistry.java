package cn.jpanda.screenshot.oss.store.clipboard;

import cn.jpanda.screenshot.oss.core.BeanRegistry;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ClipType;
import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.toolkit.BeanInstance;

/**
 * 剪切板回调类注册器
 */
@Component
public class ClipboardCallbackRegistryManagerBeanRegistry implements BeanRegistry {
    private Configuration configuration;
    private BeanInstance<ClipboardCallback> beanInstance;

    public ClipboardCallbackRegistryManagerBeanRegistry(Configuration configuration) {
        this.configuration = configuration;
        this.beanInstance = configuration.createBeanInstance(ClipboardCallback.class);
    }

    @Override
    public void doRegistry(Class c) {
        if (ClipboardCallback.class.isAssignableFrom(c)) {
            ClipboardCallbackRegistryManager clipboardCallbackRegistryManager = configuration.getUniqueBean(ClipboardCallbackRegistryManager.class);
            if (clipboardCallbackRegistryManager == null) {
                clipboardCallbackRegistryManager = new ClipboardCallbackRegistryManager(configuration);
                configuration.registryUniqueBean(ClipboardCallbackRegistryManager.class, clipboardCallbackRegistryManager);
            }

            ClipType clipType = (ClipType) c.getDeclaredAnnotation(ClipType.class);
            if (clipType != null) {
                clipboardCallbackRegistryManager.registry(clipType, beanInstance.instance(c));
            }
        }


    }
}
