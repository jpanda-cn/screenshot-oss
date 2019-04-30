package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.common.utils.JarUtils;
import cn.jpanda.screenshot.oss.core.capture.DefaultScreenCapture;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.persistence.CachedPropertiesVisitor;
import cn.jpanda.screenshot.oss.core.persistence.DefaultPropertiesVisitor;
import cn.jpanda.screenshot.oss.core.persistence.PropertiesDataPersistenceStrategy;
import cn.jpanda.screenshot.oss.core.persistence.PropertiesVisitor;

public class BootStrap {
    protected Configuration configuration = new Configuration();

    public void init() {
        // 初始化工作目录
        loadCurrentWorkDir();
        // 初始化主配置文件名称
        loadMainConfigFileName();
        // 加载配置文件持久操作策略类
        loadDataPersistenceStrategy();


        loadScreenCapture();
    }


    protected void loadCurrentWorkDir() {
        configuration.setCurrentWorkDir(JarUtils.getCurrentJarDirectory());
    }

    protected void loadMainConfigFileName() {
        configuration.setMainConfigFileName("jpanda-screenshot-oss.properties");
    }

    protected void loadDataPersistenceStrategy() {
        PropertiesVisitor propertiesVisitor = new CachedPropertiesVisitor(new DefaultPropertiesVisitor());
        configuration.setDataPersistenceStrategy(new PropertiesDataPersistenceStrategy(configuration.getMainConfigFileFullName(), propertiesVisitor));
    }

    protected void loadScreenCapture() {
        configuration.setScreenCapture(new DefaultScreenCapture());
    }
}
