package cn.jpanda.screenshot.oss.view.image;

import lombok.SneakyThrows;
import org.junit.Test;

import java.nio.file.Paths;

public class GitFileImageStoreConfigTest {

    @Test
    @SneakyThrows
    public void assertGitRepository() {
        String fileName = System.getProperty("user.dir");
        System.out.println(fileName);
        assert GitHelper.isGitRepository(fileName);
        assert !GitHelper.isGitRepository(Paths.get(fileName, "nothing").toFile());
    }



}