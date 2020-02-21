package cn.jpanda.screenshot.oss.store.img.instances.uomg;

import lombok.Getter;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/2/21 11:17
 */
@Getter
public enum EFigureBed {

    JUE_JIN("https://api.uomg.com/api/image.juejin", "掘金"), SOU_GOU("https://api.uomg.com/api/image.sogou", "搜狗"), JING_DONG("https://api.uomg.com/api/image.jd", "京东"), QI_HU_360("https://api.uomg.com/api/image.360", "奇虎360"), SINA("https://api.uomg.com/api/image.sina", "新浪微博"), BAI_DU("https://api.uomg.com/api/image.baidu", "百度识图"), A_LI_BA_BA("https://api.uomg.com/api/image.ali", "阿里巴巴");

    private String url;
    private String name;

    EFigureBed(String url, String name) {
        this.url = url;
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
