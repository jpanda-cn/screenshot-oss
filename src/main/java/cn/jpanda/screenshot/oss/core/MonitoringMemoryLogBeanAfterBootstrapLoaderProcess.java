package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 监控内存使用情况
 */
@Component
public class MonitoringMemoryLogBeanAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;
    private Log log;

    public MonitoringMemoryLogBeanAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
        this.log = configuration.getLogFactory().getLog(getClass());
    }

    @Override
    public void after() {
        log.info("start monitoring memory ...");
        // 开一个新线程用来监控内存使用情况
        Log timeTaskLog = configuration.getLogFactory().getLog(getClass().getCanonicalName() + "#timeTask");

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                long total = runtime.totalMemory();
                long free = runtime.freeMemory();
                long max = runtime.maxMemory();
                timeTaskLog.info("total:{0},max:{1},free:{2}", total, max, free);
            }
        };
        // 守护线程
        Timer timer = new Timer(true);
        timer.schedule(timerTask
                , 1000, 2000);
        log.info("123");
    }

}
