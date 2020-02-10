package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import javafx.event.EventTarget;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 画布上的快捷键管理器
 */
public class CanvasShortcutManager {
    /**
     * 全局快捷键标记
     */
    public static final String GLOBAL_FLAG = "GLOBAL_FLAG";

    private Map<Object, List<ShortCutExecutorHolder>> holders;

    private KeyboardShortcutsManager keyboardShortcutsManager;

    @Getter
    private ShortcutMatch shortcutMatch;


    public CanvasShortcutManager(KeyboardShortcutsManager keyboardShortcutsManager, ShortcutMatch shortcutMatch) {
        holders = new HashMap<>();
        this.keyboardShortcutsManager = keyboardShortcutsManager;
        this.shortcutMatch = shortcutMatch;
    }

    public void add(EventTarget target, Object type, ShortCutExecutorHolder holder) {
        if (type == null) {
            type = GLOBAL_FLAG;
        }
        keyboardShortcutsManager.registryShortCut(target, holder);
        List<ShortCutExecutorHolder> holderLis = holders.computeIfAbsent(type, (b) -> new ArrayList<>());
        holderLis.add(holder);
    }

    public void addGlobal(EventTarget target, ShortCutExecutorHolder holder) {
        keyboardShortcutsManager.registryShortCut(target, holder);
        List<ShortCutExecutorHolder> holderList = holders.computeIfAbsent(GLOBAL_FLAG, (b) -> new ArrayList<>());
        holderList.add(holder);
    }

    public List<ShortCutExecutorHolder> load(Object key) {
        if (key == null) {
            return new ArrayList<>();
        }
        return holders.getOrDefault(key, new ArrayList<>());
    }

    public List<ShortCutExecutorHolder> loadWithGlobal(Object key) {
        List<ShortCutExecutorHolder> result = new ArrayList<>();
        List<ShortCutExecutorHolder> holders = load(key);
        result.addAll(holders);
        result.addAll(load(GLOBAL_FLAG));
        return result;
    }

    public List<ShortCutExecutorHolder> loadGlobal() {
        return load(GLOBAL_FLAG);
    }

    public void clear() {
        holders.clear();
    }

    public void clear(Object type) {
        holders.remove(type);
    }
}
