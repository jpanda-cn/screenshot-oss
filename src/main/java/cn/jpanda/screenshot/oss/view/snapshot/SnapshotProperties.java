package cn.jpanda.screenshot.oss.view.snapshot;

import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

@Data
public class SnapshotProperties implements Persistence {
    // 当前截图的屏幕索引
    private int screenIndex = 1;
}
