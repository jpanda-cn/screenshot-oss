package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 14:47
 */
public final class KeyCodeNameConvertor {
    private static Map<KeyCode, String> SPECIFIC_CODE_NAME = new HashMap<>();

    static {
        SPECIFIC_CODE_NAME.put(KeyCode.SLASH, "/");
    }

    public static String get(KeyCode code) {
        return SPECIFIC_CODE_NAME.get(code);
    }

    public static String get(KeyCode code, String name) {
        return SPECIFIC_CODE_NAME.getOrDefault(code, name);
    }
}
