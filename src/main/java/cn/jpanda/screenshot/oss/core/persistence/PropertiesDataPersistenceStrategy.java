package cn.jpanda.screenshot.oss.core.persistence;

import cn.jpanda.screenshot.oss.common.utils.ReflectionUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogHolder;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.GetValueInterceptor;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.SetValueInterceptor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

public class PropertiesDataPersistenceStrategy implements DataPersistenceStrategy {
    private Log log = LogHolder.getInstance().getLogFactory().getLog(getClass());
    /**
     * 对应的配置文件名称
     */
    private String propertiesFileName;
    /**
     * 配置文件操作类
     */
    private PropertiesVisitor propertiesVisitor;

    private Configuration configuration;
    private List<SetValueInterceptor> setValueInterceptors;
    private List<GetValueInterceptor> getValueInterceptors;

    public PropertiesDataPersistenceStrategy(String propertiesFileName, PropertiesVisitor propertiesVisitor, Configuration configuration) {
        this.propertiesFileName = propertiesFileName;
        this.propertiesVisitor = propertiesVisitor;
        this.configuration = configuration;
    }

    public <T extends Persistence> T load(Class<T> type) {
        log.trace("will load profile:{}", propertiesFileName);
        Properties properties = propertiesVisitor.loadProperties(propertiesFileName);
        return (T) readProperties2TargetType(properties, type);
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
                for (GetValueInterceptor interceptor : getGetValueInterceptors()) {
                    value = interceptor.interceptor(field, value);
                }
                ReflectionUtils.setValue(field, obj, StringUtils.cast2CommonType(value, field.getType()));
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
                for (SetValueInterceptor interceptor : getSetValueInterceptors()) {
                    value = interceptor.interceptor(field, value);
                }
                properties.setProperty(key, value);
            } else {
                properties.remove(key);
            }
        }
        return properties;
    }

    public List<SetValueInterceptor> getSetValueInterceptors() {
        return configuration.getInterceptor(SetValueInterceptor.class);
    }

    public List<GetValueInterceptor> getGetValueInterceptors() {
        return getValueInterceptors = configuration.getInterceptor(GetValueInterceptor.class);
    }
}
