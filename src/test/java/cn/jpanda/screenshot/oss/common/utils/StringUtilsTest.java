package cn.jpanda.screenshot.oss.common.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StringUtilsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void toByte() {
        assert 1 == StringUtils.toByte("1");
        assert new Byte("1").equals(StringUtils.toByte("1"));
    }

    @Test
    public void toShort() {
        assert 1 == StringUtils.toShort("1");
        assert new Short("1").equals(StringUtils.toShort("1"));
    }

    @Test
    public void toInteger() {
        assert 1 == StringUtils.toInteger("1");
        assert new Integer("1").equals(StringUtils.toInteger("1"));
    }

    @Test
    public void toLang() {
        assert 1 == StringUtils.toLang("1");
        assert new Long("1").equals(StringUtils.toLang("1"));
    }

    @Test
    public void toFloat() {
        assert 1 == StringUtils.toFloat("1");
        assert new Float("1").equals(StringUtils.toFloat("1"));
    }

    @Test
    public void toDouble() {
        assert 1D == StringUtils.toDouble("1");
        assert new Double("1").equals(StringUtils.toDouble("1"));
    }

    @Test
    public void toCharacter() {
        assert new Character('1').charValue() == StringUtils.toCharacter("1");
        assert new Character('1').equals(StringUtils.toCharacter("1"));
        thrown.expect(IllegalArgumentException.class);
        StringUtils.toCharacter(null);
        StringUtils.toCharacter("abc");
    }

    @Test
    public void toBoolean() {
        assert StringUtils.toBoolean("1");
        assert StringUtils.toBoolean("true");
        assert !StringUtils.toBoolean(null);
        assert !StringUtils.toBoolean(null);
        assert !StringUtils.toBoolean(new String());
        assert !StringUtils.toBoolean("");
        assert !StringUtils.toBoolean("  ");
    }

    @Test
    public void toStringTest() {
        assert "123".equals(StringUtils.toString(123));
        assert "123".equals(StringUtils.toString("123"));
        assert null == StringUtils.toString(null);
        assert null != StringUtils.toString(new Object());
    }

    @Test
    public void isEmpty() {
        assert StringUtils.isEmpty(null);
        assert StringUtils.isEmpty("");
        assert !StringUtils.isEmpty(" ");
        assert !StringUtils.isEmpty(" 1 ");
    }

    @Test
    public void isNotEmpty() {
        assert !StringUtils.isNotEmpty(null);
        assert !StringUtils.isNotEmpty("");
        assert StringUtils.isNotEmpty(" ");
        assert StringUtils.isNotEmpty(" 1 ");
    }

    @Test
    public void cast2BasicType() {
        // 转换为基本类型
        assert 1 == StringUtils.cast2BasicType("1", byte.class);
        assert new Byte("1").equals(StringUtils.cast2BasicType("1", Byte.class));

        assert 1 == StringUtils.cast2BasicType("1", short.class);
        assert new Short("1").equals(StringUtils.cast2BasicType("1", Short.class));

        assert 1 == StringUtils.cast2BasicType("1", int.class);
        assert new Integer("1").equals(StringUtils.cast2BasicType("1", Integer.class));

        assert 1 == StringUtils.cast2BasicType("1", long.class);
        assert new Long("1").equals(StringUtils.cast2BasicType("1", Long.class));

        assert 1 == StringUtils.cast2BasicType("1", double.class);
        assert new Double("1").equals(StringUtils.cast2BasicType("1", Double.class));

        assert 1 == StringUtils.cast2BasicType("1", float.class);
        assert new Float("1").equals(StringUtils.cast2BasicType("1", Float.class));

        assert StringUtils.cast2BasicType("1", boolean.class);
        assert StringUtils.cast2BasicType("1", Boolean.class);

        assert new Character('1').charValue() == StringUtils.cast2BasicType("1", char.class);
        assert new Character('1').equals(StringUtils.cast2BasicType("1", Character.class));

        thrown.expect(IllegalArgumentException.class);
        StringUtils.cast2BasicType("123", Object.class);
        StringUtils.cast2BasicType(null, int.class);
    }

    @Test
    public void getOrDefault() {
        assert "name".equals(StringUtils.getOrDefault(null, "name"));
        assert "name".equals(StringUtils.getOrDefault("", "name"));
        assert !"name".equals(StringUtils.getOrDefault(" ", "name"));
    }
}