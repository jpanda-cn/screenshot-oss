package cn.jpanda.screenshot.oss.newcore;

import cn.jpanda.screenshot.oss.common.utils.OrderComparator;
import cn.jpanda.screenshot.oss.core.log.LogFactory;
import cn.jpanda.screenshot.oss.newcore.persistence.Persistence;
import cn.jpanda.screenshot.oss.newcore.persistence.strategy.DataPersistenceStrategy;
import cn.jpanda.screenshot.oss.newcore.interceptor.ValueInterceptor;
import cn.jpanda.screenshot.oss.newcore.scan.AfterBootstrapLoaderProcess;
import cn.jpanda.screenshot.oss.newcore.toolkit.BeanInstance;
import cn.jpanda.screenshot.oss.newcore.toolkit.DefaultBeanInstance;
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
    /**
     * 默认使用的引导配置文件的名称
     */
    public static final String DEFAULT_BOOTSTRAP_LOAD_CONFIG_FILE = "JPandaBootstrap.properties";
    /**
     * 默认使用的配置文件
     */
    public static final String DEFAULT_COMMON_CONFIG_FILE = "JPandaScreenshot4Oss.properties";
    /**
     * 用户当前输入的密码，该数据将会用来加解密用户数据，且不会被持久化。
     * 该参数会配合主配置文件中的{userPassword}字段一起使用
     */
    @Getter
    @Setter
    private String password;

    @Setter
    @Getter
    private boolean usePassword;
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
