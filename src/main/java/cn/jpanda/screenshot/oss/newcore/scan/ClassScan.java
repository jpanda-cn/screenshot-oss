package cn.jpanda.screenshot.oss.newcore.scan;

import java.util.Set;

/**
 * 类扫描器
 */
public interface ClassScan {
    /**
     * 扫描指定类路径下的所有类
     *
     * @param clazz 指定类
     */
    default ClassScan scan(Class clazz) {
        scan(clazz.getPackage().getName());
        return this;
    }

    /**
     * 扫描指定包下的的所有类
     *
     * @param packageName 包名
     */
    ClassScan scan(String packageName);

    /**
     * 获取指定包下的所有类
     */
    Set<Class> loadResult();
}
