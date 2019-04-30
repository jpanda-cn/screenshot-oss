package cn.jpanda.screenshot.oss.common.utils;

import cn.jpanda.screenshot.oss.core.exceptions.JpandaRuntimeException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Properties工具类
 */
public final class PropertiesUtils {
    public static void store(Properties properties, String propertiesURL) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(Paths.get(propertiesURL).toFile())) {
            properties.store(fileOutputStream, "");
        } catch (IOException e) {
            throw new JpandaRuntimeException(e);
        }
    }

    /**
     * 加载指定路径的文件为资源文件
     *
     * @param path 指定路径
     * @return 资源文件类
     */
    public static Properties loadProperties(String path) {
        Path propertiesPath = Paths.get(path);
        if (Files.notExists(propertiesPath)) {
            try {
                Files.createFile(propertiesPath);
            } catch (IOException e) {
                throw new JpandaRuntimeException(String.format("can not create properties file named:%s", path),e);
            }
        }
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(propertiesPath));
        } catch (IOException e) {
            throw new JpandaRuntimeException(String.format("can not find properties file named:%s", path),e);
        }
        return properties;
    }
}
