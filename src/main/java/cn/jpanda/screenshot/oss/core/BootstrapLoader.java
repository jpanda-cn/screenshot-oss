package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.common.utils.JarUtils;
import cn.jpanda.screenshot.oss.common.utils.ReflectionUtils;
import cn.jpanda.screenshot.oss.core.annotations.Order;
import cn.jpanda.screenshot.oss.core.log.*;
import cn.jpanda.screenshot.oss.core.scan.DefaultClassScan;
import cn.jpanda.screenshot.oss.core.persistence.strategy.DataPersistenceStrategy;
import cn.jpanda.screenshot.oss.core.persistence.strategy.StandardPropertiesDataPersistenceStrategy;
import cn.jpanda.screenshot.oss.core.persistence.visitor.CachedPropertiesVisitor;
import cn.jpanda.screenshot.oss.core.persistence.visitor.DefaultPropertiesVisitor;
import cn.jpanda.screenshot.oss.core.scan.ClassScan;
import cn.jpanda.screenshot.oss.core.scan.filters.ComponentAnnotationClassScanFilter;
import cn.jpanda.screenshot.oss.core.toolkit.BeanInstance;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 程序引导加载器
 * 加载日志实例
 * 配置当前工作目录等元素
 */
public abstract class BootstrapLoader {
    private Log log;
    /**
     * 程序全局配置类
     */
    private Configuration configuration;
    /**
     * 实体注册器
     */
    private List<BeanRegistry> beanRegistries;

    @Getter
    @Setter
    private ApplicationContextClassHolder classHolder = new DefaultApplicationContextClassHolder();

    @Getter
    @Setter
    private ClassScan classScan;


    /**
     * 执行引导加载操作
     * 该方法禁止重写，避免破坏程序的运行
     */
    public final Configuration load(Class startClass) {
        // 创建Configuration对象
        createConfiguration();
        // 加载日志实例
        initLogInstance();
        // 配置当前工作目录等元素
        configWorkParams();
        // 加载数据持久化策略
        initDataPersistenceStrategy();
        // 扫描所有的类文件，保存所有需要处理的类文件
        initClassScan();
        scanClass(startClass);
        // 加载bean注册器
        loadBeanRegistry();
        // 执行bean注册操作
        registryClass();
        return configuration;
    }

    protected void createConfiguration() {
        configuration = new Configuration();
        configuration.setBootstrapLoader(this);
        // 配置Configuration对象
        ConfigurationHolder.getInstance().setConfiguration(configuration);
    }

    /**
     * 加载日志实例
     */
    protected void initLogInstance() {
        DefaultOutLogConfig defaultOutLogConfig = new DefaultOutLogConfig(Loglevel.TRACE);
        LogFactory logFactory = new DefaultOutLogFactory(defaultOutLogConfig);
        log = logFactory.getLog(getClass());

        log.debug("Use log instance named:%s", logFactory.getClass().getSimpleName());
        configuration.setLogFactory(logFactory);
        LogHolder.getInstance().initLogFactory(logFactory);
    }

    /**
     * 配置当前工作目录等元素
     */
    protected void configWorkParams() {

        String currentPath = Paths.get(JarUtils.getCurrentJarDirectory()).toFile().getAbsolutePath();
        log.debug("Current working director is %s.", currentPath);
        configuration.setWorkPath(currentPath);
    }

    protected void scanClass(Class startClass) {
        // 加载获取所有需要维护处理的类
        classScan.scan(startClass).loadResult().forEach(classHolder::addClass);
    }

    protected void loadBeanRegistry() {
        // 使用类扫描器扫描加载所有需要处理的实体
        // 第一次处理所有类,获取其中所有的配置类,这些配置类主要是指实现了BeanRegistry接口的类
        BeanInstance<BeanRegistry> beanInstance = configuration.createBeanInstance(BeanRegistry.class);
        Object o = classHolder.getAllCLass().stream().filter(BeanRegistry.class::isAssignableFrom).sorted((pre, nex) -> {
            Integer preValue = Integer.MIN_VALUE;
            if (ReflectionUtils.hasAnnotation(pre, Order.class)) {
                Order order = (Order) pre.getDeclaredAnnotation(Order.class);
                preValue = order.value();
            }
            Integer nexValue = Integer.MIN_VALUE;
            if (ReflectionUtils.hasAnnotation(nex, Order.class)) {
                Order order = (Order) nex.getDeclaredAnnotation(Order.class);
                nexValue = order.value();
            }
            return preValue - nexValue;
        }).map(beanInstance::instance).filter(Objects::nonNull).collect(Collectors.toList());
        beanRegistries = (List<BeanRegistry>) o;
    }

    protected void initClassScan() {
        classScan = new DefaultClassScan(new ComponentAnnotationClassScanFilter());
    }

    protected void registryClass() {
        //  执行类注册操作
        classHolder.getAllCLass().forEach((c) -> {
            beanRegistries.forEach((b) -> {
                b.doRegistry(c);
            });
        });
    }

    protected void initDataPersistenceStrategy() {
        DataPersistenceStrategy dataPersistenceStrategy = new StandardPropertiesDataPersistenceStrategy(configuration, new CachedPropertiesVisitor(new DefaultPropertiesVisitor()));
        configuration.setDataPersistenceStrategy(dataPersistenceStrategy);
    }
}
