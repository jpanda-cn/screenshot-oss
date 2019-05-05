package cn.jpanda.screenshot.oss.common.utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        JarUtilsTest.class
        , StringUtilsTest.class
        , DESUtilsTest.class
        , PropertiesUtilsTest.class
        , ReflectionUtilsTest.class
})
public class AllUtilTest {
}
