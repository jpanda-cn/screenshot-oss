package cn.jpanda.screenshot.oss.store.clipboard;

import cn.jpanda.screenshot.oss.common.enums.ClipboardType;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ClipType;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.view.main.IconLabel;

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

    private Map<ClipboardType, List<IconLabel>> typeMap = new HashMap<>();
    private Map<String, ClipboardType> name2type = new HashMap<>();
    private Map<String, IconLabel> name2IconLabel = new HashMap<>();
    private Map<IconLabel, String> iconLabel2name = new HashMap<>();

    /**
     * 获取所有渠道名称
     */
    public List<String> getNames() {
        return new ArrayList<>(nameMap.keySet());
    }
    /**
     * 获取所有渠道名称
     */
    public List<IconLabel> getIconLabels() {
        return new ArrayList<>(iconLabel2name.keySet());
    }

    public void registry(ClipType clipType, ClipboardCallback clipboardCallback) {

        String name = clipType.name();
        ClipboardType type = clipType.type();
        log.debug("registry new clipboardCallback named:{0}, handle type is {1}.", name, type);
        if (nameMap.keySet().contains(name)) {
            log.err("find two beans with the same name :{0}", name);
            return;
        }
        IconLabel label = IconLabel.builder().text(name).icon(clipType.icon()).build();
        name2IconLabel.put(name,label);
        iconLabel2name.put(label,name);
        nameMap.put(name, clipboardCallback);
        List<IconLabel> names = typeMap.computeIfAbsent(type, k -> new ArrayList<>());
        names.add(label);
        name2type.put(name, type);
    }

    public ClipboardCallback get(String name) {
        return nameMap.get(name);
    }

    public List<IconLabel> getNamesByType(ClipboardType clipboardType) {
        return typeMap.getOrDefault(clipboardType, new ArrayList<>());
    }

    public ClipboardType getType(String name) {
        return name2type.get(name);
    }

    public  IconLabel getIconLabel(String name){
        return name2IconLabel.get(name);
    }
    public  String getName(IconLabel label){
        return iconLabel2name.get(label);
    }
}
