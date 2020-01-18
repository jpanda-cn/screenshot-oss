package cn.jpanda.screenshot.oss.common.toolkit;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * 回调方法
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/10 14:37
 */
@FunctionalInterface
public interface Callable<R, P> {
    R apply(P p) throws URISyntaxException, IOException;
}
