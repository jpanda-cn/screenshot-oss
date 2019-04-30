package cn.jpanda.screenshot.oss.common.utils;

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
        return currentPath;
    }
}
