package cn.jpanda.screenshot.oss.common.utils;

import java.io.File;
import java.nio.file.Paths;

/**
 * jar包操作工具类
 */
public final class JarUtils {
    /**
     * 获取当前jar包的工作目录
     */
    public static String getCurrentJarDirectory() {
        String currentPath = JarUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (currentPath.startsWith("/")) {
            currentPath = currentPath.substring(1);
        }
        File file=Paths.get(currentPath).toFile();
        while (!file.isDirectory()){
            currentPath=currentPath.substring(0,currentPath.lastIndexOf("/"));
            file=Paths.get(currentPath).toFile();
        }
        return currentPath;
    }
}
