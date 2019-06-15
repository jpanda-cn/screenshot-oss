package cn.jpanda.screenshot.oss.view.tray.toolkits;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class ArrowInnerSnapshotCanvasEventHandlerTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(new AnchorPane(getPolygon(100))));
        primaryStage.showAndWait();
    }

    private Polygon getPolygon(double length) {
        // 通过已知的两点绘制出一个三角形
        // 获取两点之间的距离 已知


        // 已知等边三角形的两点，求第三点有两个点
        // 转成矩形，
        // 第三边长度
        double height = new BigDecimal(0.75).multiply(new BigDecimal(Math.pow(length, 2))).doubleValue();
        // 生成一个正三角形
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(
                length / 2, height
                , 0D, height
                , length, height
        );
        // 生成了一个正三角形
        return polygon;
    }
}