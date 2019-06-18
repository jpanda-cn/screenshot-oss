package cn.jpanda.screenshot.oss.core.interceptor.value;

import cn.jpanda.screenshot.oss.common.utils.DESUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Encrypt;
import cn.jpanda.screenshot.oss.core.annotations.Interceptor;
import cn.jpanda.screenshot.oss.core.persistence.BootstrapPersistence;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * 加密字段拦截器
 */
@Interceptor
public class EncryptValueValueInterceptor implements ValueInterceptor {
    private Configuration configuration;

    public EncryptValueValueInterceptor(Configuration configuration) {
        this.configuration = configuration;
    }

    public boolean should(Field field) {
        BootstrapPersistence bootstrapPersistence = configuration.getPersistence(BootstrapPersistence.class);
        return field.isAnnotationPresent(Encrypt.class) && bootstrapPersistence.isUsePassword() && StringUtils.isNotEmpty(configuration.getPassword());
    }

    @Override
    @SneakyThrows
    public String get(Field field, String object) {
        if (field.isAnnotationPresent(Encrypt.class)) {
            if (should(field)) {
                return DESUtils.decrypt(object.getBytes(), configuration.getPassword().getBytes());
            }
        }
        return object;
    }

    @Override
    @SneakyThrows
    public String set(Field field, String object) {
        if (should(field)) {
            return DESUtils.encrypt(object.getBytes(), configuration.getPassword().getBytes());
        }
        return object;
    }
}
