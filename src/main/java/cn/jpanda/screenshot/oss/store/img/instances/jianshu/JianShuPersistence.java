package cn.jpanda.screenshot.oss.store.img.instances.jianshu;

import cn.jpanda.screenshot.oss.core.annotations.Encrypt;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 10:30
 */
@Data
public class JianShuPersistence implements Persistence {

    /**
     * 用户Cookie
     */
    @Encrypt
    private String cookie;


    private Long expire;
}
