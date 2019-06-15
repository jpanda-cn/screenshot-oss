package cn.jpanda.screenshot.oss.newcore;

import cn.jpanda.screenshot.oss.JpandaBootstrap;
import org.junit.Test;

public class JPandaApplicationRunnerTest {

    @Test
    public void run() {
        JPandaApplicationRunner jPandaApplicationRunner = new JPandaApplicationRunner();
        jPandaApplicationRunner.run(null, JpandaBootstrap.class);
    }
}