package cn.jpanda.screenshot.oss.core.persistence;

import cn.jpanda.screenshot.oss.common.utils.PropertiesUtils;

import java.util.Properties;

public class DefaultPropertiesVisitor implements PropertiesVisitor {

    @Override
    public Properties loadProperties(String path) {
        return PropertiesUtils.loadProperties(path);
    }

    @Override
    public void store(Properties properties, String propertiesURL) {
        PropertiesUtils.store(properties, propertiesURL);
    }
}
