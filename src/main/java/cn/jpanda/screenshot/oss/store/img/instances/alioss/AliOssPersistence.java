package cn.jpanda.screenshot.oss.store.img.instances.alioss;

import cn.jpanda.screenshot.oss.core.annotations.Encrypt;
import cn.jpanda.screenshot.oss.core.annotations.Profile;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

@Data
@Profile
public class AliOssPersistence implements Persistence {
    /**
     * 地域域名
     */
    private String endpoint;
    private String bucket;
    private String accessKeyId;
    @Encrypt
    private String accessKeySecret;
    private String accessUrl;
    private boolean async = false;
}
