package cn.jpanda.screenshot.oss.store;

import cn.jpanda.screenshot.oss.core.annotations.Profile;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

@Profile("image-store-result.properties")
@Data
public class ImageStoreResultPersistence implements Persistence {
    private String json = "";
}
