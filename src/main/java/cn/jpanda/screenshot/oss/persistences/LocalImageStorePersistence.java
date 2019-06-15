package cn.jpanda.screenshot.oss.persistences;

import cn.jpanda.screenshot.oss.newcore.persistence.Persistence;
import cn.jpanda.screenshot.oss.newcore.annotations.Encrypt;
import lombok.Data;

@Data
public class LocalImageStorePersistence implements Persistence {
    /**
     * 图片存储路径
     */
    @Encrypt
    private String path;
}
