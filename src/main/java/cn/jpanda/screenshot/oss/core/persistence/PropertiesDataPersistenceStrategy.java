package cn.jpanda.screenshot.oss.core.persistence;

import cn.jpanda.screenshot.oss.common.utils.ReflectionUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.Properties;

public class PropertiesDataPersistenceStrategy implements DataPersistenceStrategy {

    /**
     * 对应的配置文件名称
     */
    private String propertiesFileName;
    /**
     * 配置文件操作类
     */
    private PropertiesVisitor propertiesVisitor;

    public PropertiesDataPersistenceStrategy(String propertiesFileName, PropertiesVisitor propertiesVisitor) {
        this.propertiesFileName = propertiesFileName;
        this.propertiesVisitor = propertiesVisitor;
    }
    public Persistence load(Class<? extends Persistence> type) {
        Properties properties = propertiesVisitor.loadProperties(propertiesFileName);
        return readProperties2TargetType(properties, type);
    }

    public boolean store(Persistence persistence) {
        Properties properties = propertiesVisitor.loadProperties(propertiesFileName);
        propertiesVisitor.store(storeTargetObj2Properties(properties, persistence), propertiesFileName);
        return true;
    }

    protected Persistence readProperties2TargetType(Properties properties, Class<? extends Persistence> t) {
        // 统一前缀
        Persistence obj = ReflectionUtils.newInstance(t);
        String prefix = t.getCanonicalName();

        for (Field field : t.getDeclaredFields()) {
            ReflectionUtils.makeAccessible(field);
            // 读取指定的属性
            String value = properties.getProperty(prefix + "." + field.getName());
            if (StringUtils.isNotEmpty(value)) {
                ReflectionUtils.setValue(field, obj, StringUtils.cast2BasicType(value, field.getType()));
            }
        }
        return obj;
    }

    protected Properties storeTargetObj2Properties(Properties properties, Persistence persistence) {
        String prefix = persistence.getClass().getCanonicalName();
        for (Field field : persistence.getClass().getDeclaredFields()) {
            ReflectionUtils.makeAccessible(field);
            // 读取指定的属性
            String key = prefix + "." + field.getName();
            String value = StringUtils.toString(ReflectionUtils.readValue(field, persistence));
            if (StringUtils.isNotEmpty(value)) {
                properties.setProperty(key, value);
            } else {
                properties.remove(key);
            }
        }
        return properties;
    }
}
