package cn.jpanda.screenshot.oss.store.clipboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 剪切板处理方式注册
 */
@Data
@Builder
@AllArgsConstructor
public class ClipboardCallbackRegister {
    private String name;
    private ClipboardCallback clipboardCallback;
}
