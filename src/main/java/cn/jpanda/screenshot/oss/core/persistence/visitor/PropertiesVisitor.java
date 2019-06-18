package cn.jpanda.screenshot.oss.core.persistence.visitor;

import java.util.Properties;

/**
 * Properties文件访问器
 */
public interface PropertiesVisitor {
    /**
     * 加载指定的Properties文件
     * @param path Properties文件路径
     */
    Properties loadProperties(String path);

    /**
     * 将Properties保存到指定的文件中
     * @param properties  Properties
     * @param propertiesURL Properties文件路径
     */
    void store(Properties properties, String propertiesURL);
}
