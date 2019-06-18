package cn.jpanda.screenshot.oss.core;

import java.util.List;

/**
 * 应用程序上下文类持有者
 * 负责维护各式各样的对象
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2019/6/15 10:25
 */
public interface ApplicationContextClassHolder {
    /**
     * 获取所有被管理的类
     */
    List<Class> getAllCLass();

    /**
     * 添加一个被管理类
     *
     * @param c 类型
     */
    void addClass(Class c);

    /**
     * 是否持有的类
     *
     * @param c 指定类
     */
    boolean contain(Class c);
}
