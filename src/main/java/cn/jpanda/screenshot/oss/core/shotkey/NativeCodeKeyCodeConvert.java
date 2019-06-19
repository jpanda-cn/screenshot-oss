package cn.jpanda.screenshot.oss.core.shotkey;

import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

import static javafx.scene.input.KeyCode.*;
import static org.jnativehook.keyboard.NativeKeyEvent.*;

public class NativeCodeKeyCodeConvert {
    private static Map<Integer, KeyCode> native2code = new HashMap<>();
    private static Map<KeyCode, Integer> code2native = new HashMap<>();

    static {
        registry(VC_ESCAPE, ESCAPE);
        registry(VC_F1, F1);
        registry(VC_F2, F2);
        registry(VC_F3, F3);
        registry(VC_F4, F4);
        registry(VC_F5, F5);
        registry(VC_F6, F6);
        registry(VC_F7, F7);
        registry(VC_F8, F8);
        registry(VC_F9, F9);
        registry(VC_F10, F10);
        registry(VC_F11, F11);
        registry(VC_F12, F12);
        registry(VC_F13, F13);
        registry(VC_F14, F14);
        registry(VC_F15, F15);
        registry(VC_F16, F16);
        registry(VC_F17, F17);
        registry(VC_F18, F18);
        registry(VC_F19, F19);
        registry(VC_F20, F20);
        registry(VC_F21, F21);
        registry(VC_F22, F22);
        registry(VC_F23, F23);
        registry(VC_F24, F24);
        registry(VC_BACKQUOTE, BACK_QUOTE);
        registry(VC_1, DIGIT1);
        registry(VC_2, DIGIT2);
        registry(VC_3, DIGIT3);
        registry(VC_4, DIGIT4);
        registry(VC_5, DIGIT5);
        registry(VC_6, DIGIT6);
        registry(VC_7, DIGIT7);
        registry(VC_8, DIGIT8);
        registry(VC_9, DIGIT9);
        registry(VC_0, DIGIT0);
        registry(VC_A, A);
        registry(VC_B, B);
        registry(VC_C, C);
        registry(VC_D, D);
        registry(VC_E, E);
        registry(VC_F, F);
        registry(VC_G, G);
        registry(VC_H, H);
        registry(VC_I, I);
        registry(VC_J, J);
        registry(VC_K, K);
        registry(VC_L, L);
        registry(VC_M, M);
        registry(VC_N, N);
        registry(VC_O, O);
        registry(VC_P, P);
        registry(VC_Q, Q);
        registry(VC_R, R);
        registry(VC_S, S);
        registry(VC_T, T);
        registry(VC_U, U);
        registry(VC_V, V);
        registry(VC_W, W);
        registry(VC_X, X);
        registry(VC_Y, Y);
        registry(VC_Z, Z);
        registry(VC_MINUS, MINUS);
        registry(VC_EQUALS, EQUALS);
        registry(VC_BACKSPACE, BACK_SPACE);
        registry(VC_TAB, TAB);
        registry(VC_CAPS_LOCK, CAPS);
        registry(VC_OPEN_BRACKET, OPEN_BRACKET);
        registry(VC_CLOSE_BRACKET, CLOSE_BRACKET);
        registry(VC_BACK_SLASH, BACK_SLASH);
        registry(VC_SEMICOLON, SEMICOLON);
        registry(VC_QUOTE, QUOTE);
        registry(VC_ENTER, ENTER);
        registry(VC_COMMA, COMMA);
        registry(VC_PERIOD, PERIOD);
        registry(VC_SLASH, SLASH);
        registry(VC_SPACE, SPACE);
        registry(VC_PRINTSCREEN, PRINTSCREEN);
        registry(VC_SCROLL_LOCK, SCROLL_LOCK);
        registry(VC_PAUSE, PAUSE);
        registry(VC_INSERT, INSERT);
        registry(VC_DELETE, DELETE);
        registry(VC_HOME, HOME);
        registry(VC_END, END);
        registry(VC_PAGE_UP, PAGE_UP);
        registry(VC_PAGE_DOWN, PAGE_DOWN);
        registry(VC_UP, UP);
        registry(VC_LEFT, LEFT);
        registry(VC_CLEAR, CLEAR);
        registry(VC_RIGHT, RIGHT);
        registry(VC_DOWN, DOWN);
        registry(VC_NUM_LOCK, NUM_LOCK);
        registry(VC_SEPARATOR, SEPARATOR);
        registry(VC_SHIFT, SHIFT);
        registry(VC_CONTROL, CONTROL);
        registry(VC_ALT, ALT);
        registry(VC_META, META);
        registry(VC_CONTEXT_MENU, CONTEXT_MENU);
        registry(VC_POWER, POWER);
        registry(VC_VOLUME_UP, VOLUME_UP);
        registry(VC_VOLUME_DOWN, VOLUME_DOWN);
        registry(VC_KATAKANA, KATAKANA);
        registry(VC_UNDERSCORE, UNDERSCORE);
        registry(VC_KANJI, KANJI);
        registry(VC_HIRAGANA, HIRAGANA);
        registry(VC_UNDEFINED, UNDEFINED);
    }

    private static void registry(Integer n, KeyCode c) {
        native2code.put(n, c);
        code2native.put(c, n);
    }

    public static KeyCode getKeyCode(Integer n) {
        return native2code.getOrDefault(n, UNDEFINED);
    }

    public int getNative(KeyCode code) {
        return code2native.getOrDefault(code, VC_UNDEFINED);
    }
}
