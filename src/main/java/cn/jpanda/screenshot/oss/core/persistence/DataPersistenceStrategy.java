package cn.jpanda.screenshot.oss.core.persistence;

/**
 * 数据持久化策略接口
 */
public interface DataPersistenceStrategy {
    /**
     * 加载指定类型的用户数据
     *
     * @param type 加载数据的依据
     * @return 持久化的数据
     */
    Persistence load(Class<? extends Persistence> type);

    /**
     * 存储指定类型的用户数据
     *
     * @param p 持久化的数据
     * @return 操作是否成功
     */
    boolean store(Persistence p);
}
