package cn.jpanda.screenshot.oss.core.persistence.strategy;

import cn.jpanda.screenshot.oss.common.utils.ReflectionUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Profile;
import cn.jpanda.screenshot.oss.core.interceptor.value.ValueInterceptor;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import cn.jpanda.screenshot.oss.core.persistence.visitor.PropertiesVisitor;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * 标准的数据持久化策略
 */
public class StandardPropertiesDataPersistenceStrategy implements DataPersistenceStrategy {

    private Configuration configuration;

    private PropertiesVisitor propertiesVisitor;

    public StandardPropertiesDataPersistenceStrategy(Configuration configuration, PropertiesVisitor propertiesVisitor) {
        this.configuration = configuration;
        this.propertiesVisitor = propertiesVisitor;
    }

    @Override
    public <T extends Persistence> T load(Class<T> type) {
        // 获取配置内容
        Properties properties = propertiesVisitor.loadProperties(getProfileName(type));
        return toPersistence(properties, type);
    }

    @Override
    public boolean store(Persistence p) {
        String fileName = getProfileName(p.getClass());
        Properties properties = propertiesVisitor.loadProperties(getProfileName(p.getClass()));
        propertiesVisitor.store(toProperties(properties, p), fileName);
        return true;
    }


    public <T extends Persistence> T toPersistence(Properties properties, Class<T> t) {
        // 统一前缀
        Persistence per = ReflectionUtils.newInstance(t);
        String prefix = t.getCanonicalName();

        for (Field field : t.getDeclaredFields()) {
            ReflectionUtils.makeAccessible(field);
            // 读取指定的属性
            String value = properties.getProperty(prefix + "." + field.getName());
            if (StringUtils.isNotEmpty(value)) {
                for (ValueInterceptor interceptor : configuration.getValueInterceptors()) {
                    value = interceptor.get(field, value);
                }
                ReflectionUtils.setValue(field, per, StringUtils.cast2CommonType(value, field.getType()));
            }
        }
        return (T) per;
    }

    public Properties toProperties(Properties properties, Persistence persistence) {
        String prefix = persistence.getClass().getCanonicalName();
        for (Field field : persistence.getClass().getDeclaredFields()) {
            ReflectionUtils.makeAccessible(field);
            // 读取指定的属性
            String key = prefix + "." + field.getName();
            String value = StringUtils.toString(ReflectionUtils.readValue(field, persistence));
            if (StringUtils.isNotEmpty(value)) {
                for (ValueInterceptor interceptor : configuration.getValueInterceptors()) {
                    value = interceptor.set(field, value);
                }
                properties.setProperty(key, value);
            } else {
                properties.remove(key);
            }
        }
        return properties;
    }

    protected String getProfileName(Class type) {
        Profile profile = (Profile) type.getDeclaredAnnotation(Profile.class);
        if (profile == null) {
            return Configuration.DEFAULT_COMMON_CONFIG_FILE;
        }
        if (profile.bootstrap()) {
            return Configuration.DEFAULT_BOOTSTRAP_LOAD_CONFIG_FILE;
        }
        return profile.value();
    }
}
