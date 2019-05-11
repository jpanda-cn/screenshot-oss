package cn.jpanda.screenshot.oss.core.scan;

import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogHolder;
import javafx.fxml.Initializable;

/**
 * 场景注册器
 */
public class SceneBeanRegistry implements BeanRegistry {
    private Log log = LogHolder.getInstance().getLogFactory().getLog(getClass());
    private Configuration configuration;

    public SceneBeanRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void doRegistry(Class c) {
        if (Initializable.class.isAssignableFrom(c)) {
            log.trace("registry scene bean:{}", c.getCanonicalName());
            configuration.getViewContext().registry(c);
        }
    }
}
