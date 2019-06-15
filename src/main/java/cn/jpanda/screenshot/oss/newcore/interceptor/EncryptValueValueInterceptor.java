package cn.jpanda.screenshot.oss.newcore.interceptor;

import cn.jpanda.screenshot.oss.common.utils.DESUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.newcore.Configuration;
import cn.jpanda.screenshot.oss.newcore.annotations.Encrypt;
import cn.jpanda.screenshot.oss.newcore.annotations.Interceptor;
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
        return field.isAnnotationPresent(Encrypt.class) && configuration.isUsePassword() && StringUtils.isNotEmpty(configuration.getPassword());
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
