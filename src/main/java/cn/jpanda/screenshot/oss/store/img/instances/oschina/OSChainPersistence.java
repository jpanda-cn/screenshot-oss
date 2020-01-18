package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import cn.jpanda.screenshot.oss.core.annotations.Encrypt;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

import java.time.Instant;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 10:30
 */
@Data
public class OSChainPersistence implements Persistence {
    /**
     * 用户ID
     */
    private String uid;

    /**
     * 用户Cookie
     */
    @Encrypt
    private String cookie;


    private Long expire;
}
