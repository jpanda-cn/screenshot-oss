package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 快捷键
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 9:58
 */
@Data
@ToString
public class Shortcut {
    /**
     * 快键键触发类型
     */
    private EventType<KeyEvent> keyEvent;

    /**
     * 是否限制CTRL按钮
     */
    private Boolean ctrl;
    /**
     * 是否限制ALT按钮
     */
    private Boolean alt;
    /**
     * 是否限制SHIFT按钮
     */
    private Boolean shift;

    /**
     * 快键键描述
     */
    private String description;
    /**
     * 需要匹配的其他按钮
     */
    private List<KeyCode> codes;


    public static class Builder {
        /**
         * 快键键触发类型
         */
        private EventType<KeyEvent> keyEvent;
        /**
         * 是否限制CTRL按钮
         */
        private Boolean isCtrl;
        /**
         * 是否限制ALT按钮
         */
        private Boolean isAlt;
        /**
         * 是否限制SHIFT按钮
         */
        private Boolean isShift;
        /**
         * 需要匹配的其他按钮
         */
        private List<KeyCode> codes;
        /**
         * 快键键描述
         */
        private String description;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder keyEvent(EventType<KeyEvent> keyEvent) {
            this.keyEvent = keyEvent;
            return this;
        }

        public Builder ctrl(boolean isDown) {
            isCtrl = isDown;
            return this;
        }

        public Builder alt(boolean isDown) {
            isAlt = isDown;
            return this;
        }

        public Builder shift(boolean isDown) {
            isShift = isDown;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder addCode(KeyCode code) {
            if (codes == null) {
                codes = new ArrayList<>();
            }
            codes.add(code);
            return this;
        }

        public Shortcut build() {
            if (keyEvent == null) {
                keyEvent = KeyEvent.KEY_PRESSED;
            }
            if (isCtrl == null) {
                isCtrl = false;
            }
            if (isAlt == null) {
                isAlt = false;
            }
            if (isShift == null) {
                isShift = false;
            }
            if (codes == null || codes.size() == 0) {
                throw new RuntimeException("注册快捷键时，必须包含至少一个常规按键");
            }
            if (StringUtils.isEmpty(description)) {
                throw new RuntimeException("注册快键键时，必须描述快捷键的作用");
            }
            Shortcut sc = new Shortcut();
            sc.setKeyEvent(keyEvent);
            sc.setAlt(isAlt);
            sc.setCtrl(isCtrl);
            sc.setShift(isShift);
            sc.setCodes(codes);
            sc.setDescription(description);
            return sc;
        }
    }
}
