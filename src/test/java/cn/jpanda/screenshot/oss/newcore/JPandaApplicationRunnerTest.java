package cn.jpanda.screenshot.oss.newcore;

import cn.jpanda.screenshot.oss.JpandaBootstrap;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Test;

public class JPandaApplicationRunnerTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        JPandaApplicationRunner jPandaApplicationRunner = new JPandaApplicationRunner();
        jPandaApplicationRunner.run(primaryStage, JpandaBootstrap.class);
    }
}