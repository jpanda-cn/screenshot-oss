package cn.jpanda.screenshot.oss.core.persistence.visitor;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class CachedPropertiesVisitor implements PropertiesVisitor {

    protected Map<String, Properties> loadedProperties = new ConcurrentHashMap<>();
    /**
     * 实现委托类
     */
    private PropertiesVisitor delegation;

    public CachedPropertiesVisitor(PropertiesVisitor delegation) {
        this.delegation = delegation;
    }

    @Override
    public Properties loadProperties(String path) {
        if (loadedProperties.containsKey(path)) {
            return loadedProperties.get(path);
        }
        Properties properties = delegation.loadProperties(path);
        loadedProperties.put(path, properties);
        return properties;
    }

    @Override
    public void store(Properties properties, String propertiesURL) {
        loadedProperties.remove(propertiesURL);
        delegation.store(properties, propertiesURL);
    }
}
