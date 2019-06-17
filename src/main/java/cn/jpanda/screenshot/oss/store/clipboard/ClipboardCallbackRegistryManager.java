package cn.jpanda.screenshot.oss.store.clipboard;

import cn.jpanda.screenshot.oss.common.enums.ClipboardType;
import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.newcore.Configuration;
import cn.jpanda.screenshot.oss.newcore.annotations.ClipType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClipboardCallbackRegistryManager {
    private Configuration configuration;
    private Log log;

    public ClipboardCallbackRegistryManager(Configuration configuration) {
        this.configuration = configuration;
        log = configuration.getLogFactory().getLog(getClass());
    }

    /**
     * 图片存储渠道名称和实现的关系
     */
    private Map<String, ClipboardCallback> nameMap = new HashMap<>();

    private Map<ClipboardType, List<String>> typeMap = new HashMap<>();
    private Map<String, ClipboardType> name2type = new HashMap<>();

    /**
     * 获取所有渠道名称
     */
    public List<String> getNames() {
        return new ArrayList<>(nameMap.keySet());
    }

    public void registry(ClipType clipType, ClipboardCallback clipboardCallback) {

        String name = clipType.name();
        ClipboardType type = clipType.type();
        log.debug("registry new clipboardCallback named:%s, handle type is %s.", name, type);
        if (nameMap.keySet().contains(name)) {
            log.err("find two beans with the same name :%s", name);
            return;
        }
        nameMap.put(name, clipboardCallback);
        List<String> names = typeMap.computeIfAbsent(type, k -> new ArrayList<>());
        names.add(name);
        name2type.put(name, type);
    }

    public ClipboardCallback get(String name) {
        return nameMap.get(name);
    }

    public List<String> getNamesByType(ClipboardType clipboardType) {
        return typeMap.getOrDefault(clipboardType, new ArrayList<>());
    }
    public ClipboardType getType(String name) {
        return name2type.get(name);
    }
}
