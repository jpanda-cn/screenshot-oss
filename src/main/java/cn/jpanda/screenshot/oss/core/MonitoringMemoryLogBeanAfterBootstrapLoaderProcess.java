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
        Log timeTaskLog = configuration.getLogFactory().getLog(getClass().getCanonicalName());
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                long total = runtime.totalMemory();
                long free = runtime.freeMemory();
                long max = runtime.maxMemory();
                timeTaskLog.debug("TOTAL={0}MB,MAX={1}MB,FREE={2}MB", total/1024/1024, max/1024/1024, free/1024/1024);
            }
        };

        // 守护线程
        Timer timer = new Timer("Memory Monitoring Task",true);

        timer.schedule(timerTask
                , 1000, 10 * 1000);
    }

}
