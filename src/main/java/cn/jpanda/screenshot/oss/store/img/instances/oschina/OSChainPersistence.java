package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 10:30
 */
@Data
public class OSChainPersistence implements Persistence {
    /**
     * 博客ID
     */
    private String blogId;
}
