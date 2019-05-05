package cn.jpanda.screenshot.oss.common.utils;

import cn.jpanda.screenshot.oss.core.exceptions.JpandaRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;

public class ReflectionUtilsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void newInstance() {
        ReflectionUtils.newInstance(Object.class);
    }

    @Test
    public void newInstanceWithSomeArgs() {
        thrown.expect(JpandaRuntimeException.class);
        ReflectionUtils.newInstance(User.class);
    }

    @Test
    @SneakyThrows
    public void setValue() {
        User user = new User("jpanda", "good Panda");
        ReflectionUtils.setValue(user.getClass().getDeclaredField("name"), user, "panda");
        assert "panda".equals(user.getName());
    }

    @Test
    @SneakyThrows
    public void readValue() {
        User user = new User("jpanda", "good Panda");
        assert "jpanda".equals(ReflectionUtils.readValue(user.getClass().getDeclaredField("name"), user));
    }

    @Test
    @SneakyThrows
    public void makeAccessible() {
        User user = new User("jpanda", "good Panda");
        Field nameField = user.getClass().getDeclaredField("name");
        ReflectionUtils.makeAccessible(nameField);
        assert "jpanda".equals(nameField.get(user));


    }

}

@Data
@AllArgsConstructor
class User {
    private String name;
    private String description;
}