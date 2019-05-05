package cn.jpanda.screenshot.oss.core.context;

import java.net.URL;

/**
 * 路径查找接口
 */
public interface FXMLSearch {
    /**
     * 根据指定的类查找FXML文件并读取
     *
     * @param source 指定的资源
     * @return 对应的FXML输入流
     */
    URL search(Class source);

}
