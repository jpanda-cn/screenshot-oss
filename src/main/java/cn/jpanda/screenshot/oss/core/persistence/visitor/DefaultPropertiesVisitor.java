package cn.jpanda.screenshot.oss.core.persistence.visitor;

import cn.jpanda.screenshot.oss.common.utils.PropertiesUtils;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogHolder;

import java.util.Properties;

public class DefaultPropertiesVisitor implements PropertiesVisitor {

    private Log log = LogHolder.getInstance().getLogFactory().getLog(getClass());

    @Override
    public Properties loadProperties(String path) {
        log.trace("load profile:{}",path);
        return PropertiesUtils.loadProperties(path);
    }

    @Override
    public void store(Properties properties, String propertiesURL) {
        PropertiesUtils.store(properties, propertiesURL);
    }
}
