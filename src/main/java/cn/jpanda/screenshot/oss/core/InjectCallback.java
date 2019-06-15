package cn.jpanda.screenshot.oss.core;


import cn.jpanda.screenshot.oss.newcore.Configuration;
import javafx.util.Callback;

public class InjectCallback implements Callback<Class, Object> {
    private Configuration configuration;

    public InjectCallback(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Object call(Class param) {
        return configuration.createBeanInstance(param).instance(param);
    }
}
