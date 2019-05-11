package cn.jpanda.screenshot.oss.core.scan;

import cn.jpanda.screenshot.oss.common.utils.ReflectionUtils;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.GetValueInterceptor;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.Interceptor;
import cn.jpanda.screenshot.oss.core.persistence.interceptor.SetValueInterceptor;

public class InterceptorBeanRegistry implements BeanRegistry {

    private Configuration configuration;

    public InterceptorBeanRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void doRegistry(Class c) {
        if (Interceptor.class.isAssignableFrom(c)) {
            if (SetValueInterceptor.class.isAssignableFrom(c)) {
                Interceptor o = (Interceptor) ReflectionUtils.newInstance(c);
                configuration.registryInterceptor(SetValueInterceptor.class, o);
            } else if (GetValueInterceptor.class.isAssignableFrom(c)) {
                Interceptor o = (Interceptor) ReflectionUtils.newInstance(c);
                configuration.registryInterceptor(GetValueInterceptor.class, o);

            }
        }


    }
}
