package cn.jpanda.screenshot.oss.core.persistence;

import java.util.HashSet;
import java.util.Set;

/**
 * 登记一下所有具有持久化能力的实体
 */
public class PersistenceBeanCatalogManagement {

    private Set<Class<? extends Persistence>> persistenceSet = new HashSet<>();

    public void registry(Class<? extends Persistence> p) {
        persistenceSet.add(p);
    }

    public Set<Class<? extends Persistence>> list() {
        return persistenceSet;
    }

}
