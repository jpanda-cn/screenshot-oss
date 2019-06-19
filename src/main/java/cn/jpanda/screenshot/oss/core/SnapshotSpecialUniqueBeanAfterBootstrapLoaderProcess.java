package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;

@Component
public class SnapshotSpecialUniqueBeanAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {

    private Configuration configuration;

    public SnapshotSpecialUniqueBeanAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void after() {
        configuration.registryUniqueBean(Snapshot.class, new ClassicScreenshot(configuration));
    }
}
