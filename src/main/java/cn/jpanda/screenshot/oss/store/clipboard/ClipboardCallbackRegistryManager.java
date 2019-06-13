package cn.jpanda.screenshot.oss.store.clipboard;

import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClipboardCallbackRegistryManager {
    /**
     * 图片存储渠道名称和实现的关系
     */
    private Map<String, ClipboardCallback> nameMap = new HashMap<>();

    /**
     * 获取所有渠道名称
     */
    public List<String> getNames() {
        return new ArrayList<>(nameMap.keySet());
    }

    public void registry(ClipboardCallbackRegister r) {
        String channelName = r.getName();
        ClipboardCallback clipboardCallback = r.getClipboardCallback();
        nameMap.put(channelName, clipboardCallback);
    }

    public ClipboardCallback get(String name) {
        return nameMap.get(name);
    }
}
