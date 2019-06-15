package cn.jpanda.screenshot.oss.core.scan;

import cn.jpanda.screenshot.oss.newcore.scan.ClassScan;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 类扫描注册器
 */
public class BootStrapClassScanRegistry {
    /**
     * 默认的类扫描器
     */
    protected ClassScan classScan;
    List<BeanRegistry> registries = new ArrayList<>();

    public BootStrapClassScanRegistry() {
        this(new DefaultClassScan(new ViewClassScanFilter()));
    }

    public void add(BeanRegistry registry) {
        registries.add(registry);
    }

    public void remove(BeanRegistry registry) {
        registries.remove(registry);
    }

    public BootStrapClassScanRegistry(ClassScan classScan) {
        this.classScan = classScan;

    }

    public void doRegistry(Class clazz) {
        doRegistry(clazz.getPackage().getName());
    }

    public void doRegistry(String packageName) {
        classScan.scan(packageName);
        Set<Class> classes = classScan.loadResult();
        for (Class aClass : classes) {
            registries.forEach(f -> f.doRegistry(aClass));
        }
    }
}
