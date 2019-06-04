package cn.jpanda.screenshot.oss.core.persistence.interceptor.encrypt;

import cn.jpanda.screenshot.oss.common.utils.DESUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.GetValueInterceptor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class EncryptGetValueInterceptor implements GetValueInterceptor {
    private Configuration configuration;

    public EncryptGetValueInterceptor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    @SneakyThrows
    public String interceptor(Field field, String object) {
        if (field.isAnnotationPresent(Encrypt.class)) {
            if (configuration.usePassword() && StringUtils.isNotEmpty(configuration.getPassword())) {
                return DESUtils.decrypt(object.getBytes(),configuration.getPassword().getBytes() );
            }
        }
        return object;
    }
}
