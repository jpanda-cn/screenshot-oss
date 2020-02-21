package cn.jpanda.screenshot.oss.store.img.instances.uomg;

import cn.jpanda.screenshot.oss.core.annotations.Profile;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

@Data
@Profile
public class UmogPersistence implements Persistence {
    /**
     * 子类型
     */
    private String type = EFigureBed.A_LI_BA_BA.name();
}
