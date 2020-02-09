package cn.jpanda.screenshot.oss.view.image;

import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class GitHelperTest {

    @Test
    @Ignore
    @SneakyThrows
    public void cloneRep() {
        Git.cloneRepository()
                .setURI("http://192.168.0.102/root/jpanda-snapshot.git")
                .setBranch("test")
                .setDirectory(new File("F:\\test\\jpanda-snashot-3"))
                .setNoCheckout(false)
                .call();
    }


}