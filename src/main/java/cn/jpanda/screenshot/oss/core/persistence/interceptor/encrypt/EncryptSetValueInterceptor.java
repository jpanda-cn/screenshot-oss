package cn.jpanda.screenshot.oss.core.persistence.interceptor.encrypt;

import cn.jpanda.screenshot.oss.common.utils.DESUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.annotations.Order;
import cn.jpanda.screenshot.oss.core.annotations.ValueInterceptor;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.SetValueInterceptor;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.encrypt.Encrypt;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * 在Set方法中执行加密操作
 */
@Order(1)
@ValueInterceptor(isSet = true)
public class EncryptSetValueInterceptor implements SetValueInterceptor {

    private final Configuration configuration;

    public EncryptSetValueInterceptor(Configuration configuration) {
        this.configuration = configuration;
    }

    public boolean should(Field field) {
        return field.isAnnotationPresent(Encrypt.class);
    }

    @Override
    @SneakyThrows
    public String interceptor(Field field, String object) {
        if (should(field) && configuration.usePassword() && StringUtils.isNotEmpty(getEncryptKey())) {
            return DESUtils.encrypt(object.getBytes(), getEncryptKey().getBytes());
        }
        return object;
    }

    private String getEncryptKey() {
        return configuration.getPassword();
    }
}
