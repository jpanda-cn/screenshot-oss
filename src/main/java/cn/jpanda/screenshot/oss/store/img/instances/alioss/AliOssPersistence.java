package cn.jpanda.screenshot.oss.store.img.instances.alioss;

import cn.jpanda.screenshot.oss.core.annotations.Encrypt;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

@Data
public class AliOssPersistence implements Persistence {
    private String endpoint;
    private String bucket;
    private String accessKeyId;
    @Encrypt
    private String accessKeySecret;
    private String schema = "HTTP";
    private boolean async = false;
}
