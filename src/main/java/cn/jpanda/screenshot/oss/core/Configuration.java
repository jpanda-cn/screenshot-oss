package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.common.utils.OrderComparator;
import cn.jpanda.screenshot.oss.core.controller.ViewContext;
import cn.jpanda.screenshot.oss.core.interceptor.value.ValueInterceptor;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogFactory;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import cn.jpanda.screenshot.oss.core.persistence.strategy.DataPersistenceStrategy;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;
import cn.jpanda.screenshot.oss.core.toolkit.BeanInstance;
import cn.jpanda.screenshot.oss.core.toolkit.DefaultBeanInstance;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Configuration对象，是一个比较重的对象，他在当前工程中处于核心的地位
 */
public class Configuration {
    @Setter
    private Log log;
    /**
     * 默认使用的引导配置文件的名称
     */
    public static final String DEFAULT_BOOTSTRAP_LOAD_CONFIG_FILE = "bootstrap.properties";
    /**
     * 默认使用的配置文件
     */
    public static final String DEFAULT_COMMON_CONFIG_FILE = "screenshot4oss.properties";
    /**
     * 用户当前输入的密码，该数据将会用来加解密用户数据，且不会被持久化。
     * 该参数会配合主配置文件中的{userPassword}字段一起使用
     */
    @Getter
    @Setter
    private String password;

    /**
     * 是否已经启动完成
     */
    @Getter
    @Setter
    private boolean started;
    /**
     * 正在截图中
     */
    @Getter
    private volatile SimpleObjectProperty<Stage> cutting = new SimpleObjectProperty<>();
    /**
     * 程序执行器所处的工作目录
     */
    @Getter
    @Setter
    private String workPath;

    /**
     * 当前使用的日志工具
     */
    @Getter
    @Setter
    private LogFactory logFactory;

    @Getter
    @Setter
    private BootstrapLoader bootstrapLoader;

    /**
     * 数据持久化值拦截器
     */
    private List<ValueInterceptor> valueInterceptors = new ArrayList<>();

    /**
     * 程序完成引导后执行的类
     */
    private List<AfterBootstrapLoaderProcess> afterBootstrapLoaderProcesses = new ArrayList<>();
    /**
     * 数据持久化策略
     */
    @Getter
    @Setter
    private DataPersistenceStrategy dataPersistenceStrategy;
    /**
     * 视图上下文
     */
    @Getter
    @Setter
    private ViewContext viewContext;

    /**
     * 特殊且唯一的实体Bean注册表
     */
    private Map<Class, Object> specialAndUniqueBeanRegistryTable = new ConcurrentHashMap<>();

    private Map<Object, Object> uniquePropertiesHolder = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getUniquePropertiesHolder(Object key, T defaultValue) {
        return (T) uniquePropertiesHolder.getOrDefault(key, defaultValue);
    }

    public <T> T getUniquePropertiesHolder(Object key) {
        return (T) uniquePropertiesHolder.get(key);
    }

    /**
     * 注册一个特殊且唯一的实体类
     */
    public <T> void registryUniquePropertiesHolder(Object key, Object value) {
        uniquePropertiesHolder.put(key, value);
    }


    /**
     * 获取一个特殊且唯一的实体类
     */
    @SuppressWarnings("unchecked")
    public <T> T getUniqueBean(Class<T> tClass) {
        T result = (T) specialAndUniqueBeanRegistryTable.get(tClass);
        if (result == null) {
            Class cls = specialAndUniqueBeanRegistryTable.keySet().stream().filter(tClass::isAssignableFrom).findFirst().orElse(null);
            if (cls == null) {
                return result;
            }
            result = (T) specialAndUniqueBeanRegistryTable.get(cls);
        }
        return result;
    }

    /**
     * 注册一个特殊且唯一的实体类
     */
    public <T> void registryUniqueBean(Class<? extends T> tClass, T obj) {
        specialAndUniqueBeanRegistryTable.put(tClass, obj);
    }

    /**
     * 获取实体对象
     */
    public <T extends Persistence> T getPersistence(Class<T> p) {
        return dataPersistenceStrategy.load(p);
    }

    /**
     * 保存实体对象
     */
    public void storePersistence(Persistence p) {
        dataPersistenceStrategy.store(p);
    }

    /**
     * 创建一个用于实例化指定类型的Bean实例化工具
     *
     * @param clazz 目标类型
     */
    public <T> BeanInstance<T> createBeanInstance(Class<T> clazz) {
        return new DefaultBeanInstance<>(this, bootstrapLoader);
    }

    /**
     * 注册持久化对象使用的拦截器
     *
     * @param value 拦截器对象
     */
    public void registryInterceptor(ValueInterceptor value) {
        valueInterceptors.add(value);
        valueInterceptors = valueInterceptors.stream().sorted((pre, nex) -> new OrderComparator().compare(pre.getClass(), nex.getClass())).collect(Collectors.toList());
    }

    /**
     * 获取所有拦截器
     */
    public List<ValueInterceptor> getValueInterceptors() {
        return valueInterceptors;
    }

    /**
     * 注册持久化对象使用的拦截器
     *
     * @param value 拦截器对象
     */
    public void registryAfterBootstrapLoaderProcesses(AfterBootstrapLoaderProcess value) {
        log.info("registry new after bootstrap loader processes :{0}", value.getClass().getCanonicalName());
        afterBootstrapLoaderProcesses.add(value);
        afterBootstrapLoaderProcesses = afterBootstrapLoaderProcesses.stream().sorted((pre, nex) -> new OrderComparator().compare(pre.getClass(), nex.getClass())).collect(Collectors.toList());
    }

    /**
     * 获取所有拦截器
     */
    public List<AfterBootstrapLoaderProcess> getAfterBootstrapLoaderProcesses() {
        return afterBootstrapLoaderProcesses;
    }
}
