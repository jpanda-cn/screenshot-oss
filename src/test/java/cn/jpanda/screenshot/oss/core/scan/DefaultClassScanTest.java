package cn.jpanda.screenshot.oss.core.scan;

import org.junit.Test;

import java.util.Set;

public class DefaultClassScanTest {

    @Test
    public void loadResult() {
        ClassScan classScan = new DefaultClassScan(new ViewClassScanFilter());
        classScan.scan(DefaultClassScanTest.class.getPackage().getName());
        Set<Class> classes = classScan.loadResult();
        assert classes.contains(DefaultClassScanTest.class);
    }
}