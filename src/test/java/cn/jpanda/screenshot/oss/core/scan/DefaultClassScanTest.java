//package cn.jpanda.screenshot.oss.core.scan;
//
//import cn.jpanda.screenshot.oss.core.annotations.View;
//import cn.jpanda.screenshot.oss.newcore.scan.ClassScan;
//import cn.jpanda.screenshot.oss.newcore.scan.DefaultClassScan;
//import org.junit.Test;
//
//import java.util.Set;
//
//@View
//public class DefaultClassScanTest {
//
//    @Test
//    public void loadResult() {
//        ClassScan classScan = new DefaultClassScan(new ViewClassScanFilter());
//        classScan.scan(DefaultClassScanTest.class.getPackage().getName());
//        Set<Class> classes = classScan.loadResult();
//        assert classes.contains(DefaultClassScanTest.class);
//    }
//}