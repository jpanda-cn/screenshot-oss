package cn.jpanda.screenshot.oss.core.controller;


import cn.jpanda.screenshot.oss.common.utils.ReflectionUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import javafx.util.Callback;
import lombok.SneakyThrows;

/**
 * 具有注入能力的Controller处理器，为{@link javafx.fxml.FXMLLoader} 加载{@link javafx.fxml.Initializable}实现时，提供
 * 注入{@link Configuration}的能力
 */
public class InjectControllerCallback implements Callback<Class<?>, Object> {
    private Configuration configuration;

    public InjectControllerCallback(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    @SneakyThrows
    public Object call(Class param) {
        if (param.isAnnotationPresent(Controller.class)) {
            return configuration.createBeanInstance(param).instance(param);
        }
        return ReflectionUtils.newInstance(param);
    }
}
