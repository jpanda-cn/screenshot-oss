package cn.jpanda.screenshot.oss.persistences;

import cn.jpanda.screenshot.oss.core.annotations.Encrypt;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

@Data
public class LocalImageStorePersistence implements Persistence {
    /**
     * 图片存储路径
     */
    @Encrypt
    private String path = "images/";
}
