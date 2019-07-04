package cn.jpanda.screenshot.oss.core.interceptor.value;

import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.BeanRegistry;
import cn.jpanda.screenshot.oss.core.toolkit.BeanInstance;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;

@Component
public class ValueInterceptorBeanRegistry implements BeanRegistry {
    private Log log;
    private Configuration configuration;
    private BeanInstance<ValueInterceptor> beanInstance;

    public ValueInterceptorBeanRegistry(Configuration configuration) {
        this.configuration = configuration;
        log = configuration.getLogFactory().getLog(getClass());
        beanInstance = configuration.createBeanInstance(ValueInterceptor.class);
    }

    @Override
    public void doRegistry(Class c) {
        if (ValueInterceptor.class.isAssignableFrom(c)) {
            ValueInterceptor o = beanInstance.instance(c);
            // 注册该拦截器
            configuration.registryInterceptor(o);
            log.info("registry new interceptor named : {0}", c.getSimpleName());

        }
    }
}
