package cn.jpanda.screenshot.oss.persistences;

import cn.jpanda.screenshot.oss.core.annotations.Profile;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

@Data
@Profile
public class LocalImageStorePersistence implements Persistence {
    /**
     * 图片存储路径
     */
    private String path = "";
}
