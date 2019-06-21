package cn.jpanda.screenshot.oss.core.persistence;

import cn.jpanda.screenshot.oss.core.BeanRegistry;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;

@Component
public class PersistenceBeanRegistry implements BeanRegistry {
    private Configuration configuration;

    public PersistenceBeanRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void doRegistry(Class c) {
        if (Persistence.class.isAssignableFrom(c)) {
            PersistenceBeanCatalogManagement persistenceBeanCatalogManagement = configuration.getUniqueBean(PersistenceBeanCatalogManagement.class);
            if (persistenceBeanCatalogManagement == null) {
                persistenceBeanCatalogManagement = new PersistenceBeanCatalogManagement();
                configuration.registryUniqueBean(PersistenceBeanCatalogManagement.class, persistenceBeanCatalogManagement);
            }
            persistenceBeanCatalogManagement.registry(c);
        }
    }
}
