package cn.jpanda.screenshot.oss.core.scan;

@FunctionalInterface
public interface ClassScanFilter {
    /**
     * 执行过滤操作
     */
    boolean doFilter(Class clazz);
}
