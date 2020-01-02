import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import lombok.Getter;

/**
 * SVG Path Holder
 *
 */
public enum SVGPathHolder {

    /**
     * 删除图标
     */
    DELETE("M762.218 134.865H263.15c-45.947 0-83.178 37.228-83.178 83.175v27.725h665.422V218.04c0.001-45.947-37.229-83.175-83.177-83.175m-145.29-55.45l12.238 87.507H396.203l12.237-87.507h208.488m6.66-55.45H401.783c-22.88 0-44.162 18.52-47.357 41.209L338.207 181.19c-3.168 22.66 12.97 41.181 35.85 41.181h277.258c22.879 0 39.017-18.519 35.823-41.208l-16.22-116.017c-3.17-22.662-24.451-41.181-47.33-41.181m152.493 277.248H249.288c-30.488 0-53.205 24.855-50.443 55.233l45.435 499.479c2.734 30.379 29.973 55.233 60.46 55.233h415.89c30.487 0 57.726-24.856 60.46-55.233l45.434-499.48c2.761-30.377-19.956-55.232-50.443-55.232M401.78 800.26h-83.178l-27.726-388.147H401.78V800.26z m166.356 0H457.233V412.113h110.904V800.26z m138.629 0h-83.178V412.113H734.49L706.766 800.26z")
    /**
     * 图钉
     */


    ;

    @Getter
    private String path;


    SVGPathHolder(String path) {
        this.path = path;
    }

    public SVGPath to(Paint paint){
        return createPath(getPath(),paint);
    }

    public static SVGPath createPath(String content,Paint paint){
        SVGPath svgPath=new SVGPath();
        svgPath.setContent(content);
        svgPath.setFill(paint);
        return svgPath;
    }

}
