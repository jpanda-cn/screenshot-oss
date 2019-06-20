package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;

@Component
public class ChoseScreenShowValueAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;

    public ChoseScreenShowValueAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void after() {
        ChoseScreenShowValue choseScreenShowValue = new ChoseScreenShowValue();
        choseScreenShowValue.show.setValue(configuration.getPersistence(GlobalConfigPersistence.class).isScreenshotMouseFollow());
        configuration.registryUniqueBean(ChoseScreenShowValue.class, choseScreenShowValue);
    }
}
