package cn.jpanda.screenshot.oss.common.utils;

import org.junit.After;
import org.junit.Test;

import java.util.Properties;

public class PropertiesUtilsTest {
    @After
    public void after(){
        Properties properties=new Properties();
        properties.setProperty("name","test");
        PropertiesUtils.store(properties,PROPERTIES_FILE_PATH);
    }

   public static final String PROPERTIES_FILE_PATH=PropertiesUtilsTest.class.getClassLoader().getResource(PropertiesUtilsTest.class.getPackage().getName().replaceAll("\\.", "/") + "/testProperties.properties").getPath().substring(1);

    @Test
    public void store() {
        Properties properties = PropertiesUtils.loadProperties(PROPERTIES_FILE_PATH);
        assert "test".equals(properties.getProperty("name"));
    }

    @Test
    public void loadProperties() {
        Properties properties=new Properties();
        properties.setProperty("name","jpanda");
        PropertiesUtils.store(properties,PROPERTIES_FILE_PATH);
        properties = PropertiesUtils.loadProperties(PROPERTIES_FILE_PATH);
        assert "jpanda".equals(properties.getProperty("name"));
    }

}