package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.scene.Scene;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/10 14:39
 */
public class PropertiesReader {

    public static <T> T read(Scene scene, Object key) {
        return (T) scene.getProperties().get(key);
    }

    public static <T> T readUserData(Scene scene) {

        return (T) scene.getUserData();
    }
}
