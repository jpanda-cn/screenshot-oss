package cn.jpanda.screenshot.oss.store.save;

import javafx.fxml.Initializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageStoreRegisterManager {

    /**
     * 图片存储渠道名称和实现的关系
     */
    private Map<String, ImageStore> nameMap = new HashMap<>();

    private Map<String, Class<? extends Initializable>> configs = new HashMap<>();

    /**
     * 获取所有渠道名称
     */
    public List<String> getNames() {
        return new ArrayList<>(nameMap.keySet());
    }

    public void registry(ImageStoreRegister r) {
        String channelName = r.getName();
        ImageStore store = r.getImageStore();
        Class<? extends Initializable> config = r.getImageConfig();
        nameMap.put(channelName, store);
        configs.put(channelName, config);
    }

    public ImageStore getImageStore(String name) {
        return nameMap.get(name);
    }

    public Class<? extends Initializable> getConfig(String name) {
        return configs.get(name);
    }
}
