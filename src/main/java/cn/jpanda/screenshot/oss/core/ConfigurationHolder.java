package cn.jpanda.screenshot.oss.core;

/**
 * 配置持有类，将会在生成Configuration对象之后，调用他的setConfiguration方法来完成Configuration对象的持有。
 */
public class ConfigurationHolder {
    private Configuration configuration;
    private static ConfigurationHolder ourInstance = new ConfigurationHolder();

    public static ConfigurationHolder getInstance() {
        return ourInstance;
    }

    private ConfigurationHolder() {
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
