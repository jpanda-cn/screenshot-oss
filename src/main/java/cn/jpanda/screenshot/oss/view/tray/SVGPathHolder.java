package cn.jpanda.screenshot.oss.view.tray;

import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import lombok.Getter;

/**
 * SVG Path Holder
 */
public enum SVGPathHolder {

    /**
     * 拖拽
     */
    DRAG("M474.026809 1008.213393l-152.746094-152.746094a54.186463 54.186463 0 1 1 74.666387-78.079708l60.586439 60.58644v-164.266051a53.759798 53.759798 0 0 1 107.519597-2.55999v168.532701l61.013105-60.586439a53.759798 53.759798 0 0 1 75.946382 76.373047l-150.612769 152.746094a58.453114 58.453114 0 0 1-38.399856 15.786607 55.893124 55.893124 0 0 1-37.973191-15.786607z m305.065523-305.492188a53.759798 53.759798 0 0 1 0-75.946382l60.586439-60.58644H674.132725a54.186463 54.186463 0 0 1 0-107.946261h165.546046l-60.586439-61.013105a54.186463 54.186463 0 1 1 76.373047-76.373047l152.746094 152.746094a53.759798 53.759798 0 0 1 0 75.946382l-152.746094 152.746094a53.333133 53.333133 0 0 1-75.519717 0zM170.667947 702.721205L15.788527 549.975111a54.186463 54.186463 0 0 1 0-75.946382L170.667947 320.85597a54.186463 54.186463 0 1 1 74.239721 76.373047l-60.586439 60.58644h165.972711a54.186463 54.186463 0 0 1 0 107.946261H184.321229l60.586439 60.58644A54.186463 54.186463 0 1 1 170.667947 702.721205z m287.572255-352.85201V184.323149L397.653762 244.909588a54.186463 54.186463 0 0 1-76.373047 0 53.759798 53.759798 0 0 1 0-74.239721L474.026809 15.790447A52.053138 52.053138 0 0 1 512 0.00384a53.759798 53.759798 0 0 1 38.399856 15.786607L703.14595 170.669867a54.186463 54.186463 0 1 1-76.373047 76.373047l-60.58644-62.719765V349.869195a54.186463 54.186463 0 0 1-107.946261 2.55999V349.869195z")
    /*
      圆形
     */, ROUND("M512 1005.568c-272.384 0-493.568-221.696-493.568-493.568S239.616 18.432 512 18.432s493.568 221.696 493.568 493.568-221.184 493.568-493.568 493.568zM512 102.4c-225.792 0-409.6 183.808-409.6 409.6s183.808 409.6 409.6 409.6 409.6-183.808 409.6-409.6-183.808-409.6-409.6-409.6z")
    /*
      矩形
     */, RECTANGLE("M808.079 981.684H215.921c-95.719 0-173.619-77.902-173.619-173.619V215.936c0-95.719 77.9-173.619 173.619-173.619h592.158c95.719 0 173.619 77.9 173.619 173.619v592.129c0 95.716-77.9 173.619-173.619 173.619M215.921 137.018c-43.466 0-78.919 35.39-78.919 78.918v592.129c0 43.543 35.453 78.917 78.919 78.917h592.158c43.498 0 78.919-35.374 78.919-78.917V215.936c0-43.528-35.421-78.918-78.919-78.918H215.921z")
    /*
    箭头
     */, ARROW("M822.66112 218.55232l-61.55264 470.9376c-0.45056 3.45088-2.03776 6.53312-4.37248 8.86784-2.304 2.304-5.3248 3.87072-8.72448 4.352-6.83008 0.96256-13.47584-2.7648-16.22016-9.10336l-81.99168-189.07136L336.03584 818.29888c-6.00064 6.00064-15.72864 6.00064-21.72928 0L205.70112 709.6832c-6.00064-6.00064-6.00064-15.7184 0-21.71904l313.76384-313.76384-189.07136-81.99168c-6.33856-2.75456-10.06592-9.39008-9.10336-16.2304 0.95232-6.84032 6.3488-12.20608 13.21984-13.09696l470.92736-61.55264c4.73088-0.62464 9.48224 0.99328 12.8512 4.36224C821.66784 209.07008 823.28576 213.82144 822.66112 218.55232z")
     /*
        画笔
      */, PEN("M358.681 586.386s-90.968 49.4-94.488 126.827c-3.519 77.428-77.427 133.74-102.063 140.778s360.157 22.971 332.002-142.444l-135.45-125.16zM527.78 638.946c14.016 13.601 17.565 32.675 7.929 42.606-9.635 9.93-28.81 6.954-42.823-6.647l-92.767-88.518c-14.015-13.6-17.565-32.675-7.929-42.605 9.636-9.93 28.81-6.955 42.824 6.646l92.766 88.518zM849.514 173.863c-25.144-17.055-47.741-1.763-57.477 3.805-29.097 19.485-237.243 221.77-327.69 315.194-11.105 14.8-18.59 26.294 34.663 79.546 44.95 44.95 65.896 42.012 88.66 22.603 37.906-37.906 199.299-262.926 258.92-348.713 9.792-14.092 29.851-54.17 2.924-72.435z")
    /*
    文字
     */, TEXT("M56.888889 56.888889h682.666667v113.777778H56.888889zM512 512h455.111111v113.777778H512zM341.333333 170.666667h113.777778v796.444444H341.333333z     M682.666667 568.888889h113.777777v398.222222h-113.777777z")
   /*
   马赛克
    */, MOSAIC("M40.96 40.0384v942.08h942.08v-942.08H40.96z m672.50176 866.7136h-198.20544v-189.58336h198.20544v189.58336z m194.21184-191.1808h-192.6144v-199.00416H514.4576v199.80288h-196.8128v-197.24288H117.0432v200.58112h197.632v187.02336H116.3264V316.0064h197.55008V115.4048H907.6736V715.5712zM313.87648 316.0064v200.58112h200.58112V316.0064h-200.58112z")
    /*
    取色器
    */, COLOR_PICKER("M974.78094454 15.48290198c-71.10159612-71.10159612-184.10234947-71.10159612-253.9342745-1.26967098l-48.24751236 48.24751231-41.89915448-41.89915447-123.15812421 123.15812146 43.16882546 43.16882826-276.7883583 276.78835831c-54.5958702 54.5958702-66.02291205 137.12450817-34.28112563 203.14742019l-69.83192787 69.83192511 85.0679828 85.06798276 69.831925-69.83192508c24.12375473 11.4270419 49.51718332 16.50572871 74.91061198 16.50572589h8.88769985c41.8991545-2.53934201 83.79831178-19.04507074 116.80976634-52.05652533l276.78835836-276.7883583 43.16882827 43.1688255 123.15812149-123.15812428-41.89915453-41.89915445 48.24751233-48.24751236c69.8319251-71.10159612 69.8319251-184.10234947 0-253.93427454z m-272.97934525 441.84563988l-168.86629174-168.86629459 60.9442252-60.94422522 168.86629174 168.86629178-60.9442252 60.94422803zM190.12403436 828.07258279s-73.64094092 62.21389622-73.64093809 132.04582126c0 40.62948347 33.01145461 73.64094092 73.64093809 73.64094092 40.62948347 0 73.64094092-33.01145461 73.64094089-73.64094092 1.26967099-71.10159612-73.64094092-132.04582415-73.64094089-132.04582126z")
    /*
    图钉
     */, DRAWING_PIN("M43.072 974.72l380.864-301.952 151.936 161.6s63.424 17.28 67.328-30.72l-3.904-163.584 225.088-259.648 98.048-5.696s76.928-15.488 21.184-82.752L708.544 15.04s-74.944-9.6-69.248 59.584v75.008L383.552 367.104l-157.696 9.536s-57.728 19.2-36.608 69.248l148.16 146.176L43.072 974.72z")
    /*
    设置
     */, SETTING("M881 512c0-52.4 32.9-96.8 79-114.5-11-43.2-28-83.9-50.2-121.3C864.6 296.3 810 288.1 773 251c-37-37-45.2-91.7-25.1-136.8C710.4 92 669.7 75 626.5 64c-17.8 46.1-62.2 79-114.5 79-52.4 0-96.8-32.9-114.5-79-43.2 11-83.9 28-121.3 50.2 20.1 45.2 11.9 99.8-25.1 136.8-37 37-91.7 45.2-136.8 25.2C92 313.6 75 354.3 64 397.5c46.1 17.8 79 62.2 79 114.5 0 52.4-32.9 96.8-79 114.5 11 43.2 28 83.9 50.2 121.3C159.4 727.7 214 735.9 251 773c37 37 45.2 91.7 25.1 136.8C313.6 932 354.3 949 397.5 960c17.8-46.1 62.2-79 114.5-79 52.4 0 96.8 32.9 114.5 79 43.2-11 83.9-28 121.3-50.2-20.1-45.2-11.9-99.8 25.1-136.8 37-37 91.7-45.2 136.8-25.2C932 710.4 949 669.7 960 626.5c-46.1-17.7-79-62.1-79-114.5zM512 635c-67.9 0-123-55.1-123-123s55.1-123 123-123 123 55.1 123 123-55.1 123-123 123z")
    /*
    上传
     */, UPLOAD("M992.171444 312.62966C975.189616 137.155482 827.415189 0 647.529412 0 469.849434 0 323.616239 133.860922 303.679205 306.210218 131.598564 333.839271 0 482.688318 0 662.588235c0 199.596576 161.815189 361.411765 361.411765 361.411765h184.014581V692.705882H294.530793l337.939795-361.411764 337.939796 361.411764H726.132229v331.294118H933.647059v-1.555371c185.470975-15.299199 331.294118-170.426291 331.294117-359.856394 0-168.969898-116.101408-310.367302-272.769732-349.958575z")
    /*
     * 关闭
     */, CLOSE("M512 0C230.4 0 0 230.4 0 512s230.4 512 512 512 512-230.4 512-512-230.4-512-512-512z m236.8 659.2c25.6 25.6 25.6 64 0 89.6-12.8 12.8-25.6 19.2-44.8 19.2-19.2 0-32-6.4-44.8-19.2L512 601.6l-147.2 147.2c-12.8 12.8-25.6 19.2-44.8 19.2-19.2 0-32-6.4-44.8-19.2-25.6-25.6-25.6-64 0-89.6L422.4 512 275.2 364.8c-25.6-25.6-25.6-64 0-89.6 25.6-25.6 64-25.6 89.6 0L512 422.4l147.2-147.2c25.6-25.6 64-25.6 89.6 0 25.6 25.6 25.6 64 0 89.6L601.6 512l147.2 147.2z")
    /*
     * 保存
     */, SAVE("M998.8 152.2c-33.8-33.8-88.2-33.8-120.8 0L365.2 665.4 147.8 447.6c-33.8-33.8-88.2-33.8-122 0s-33.8 88.2 0 122l276.8 276.8c17.2 17.2 39.4 25.6 62.6 25.6 22.2 0 45.4-8.2 62.6-25.6l572-572c32.8-34 32.8-88.4-1-122.2z")
    /*
        圆角正方体-实心
     */, RADIO_RECTANGLE("M928 64h-832c-17.92 0-32 14.08-32 32v832c0 17.92 14.08 32 32 32h832c17.92 0 32-14.08 32-32v-832c0-17.92-14.08-32-32-32z")
    /*
     * 圆角正方体-实心-选中
     */
    , RADIO_RECTANGLE_CHECKED("M921.6 0H102.4C45.8432 0 0 45.8432 0 102.4v819.2c0 56.5568 45.8432 102.4 102.4 102.4h819.2c56.5568 0 102.4-45.8432 102.4-102.4V102.4c0-56.5568-45.8432-102.4-102.4-102.4zM502.114133 701.262933l-0.110933-0.110933-66.807467 66.779733L179.1936 511.9488l76.8-76.785067 179.364267 179.364267 363.5392-358.978133 71.490133 72.3008-368.273067 373.412266z")
    /**
     * 圆点
     */
    , DOT("M422.339 483.676a11.169 11.169 0 1 0 228.74 0 11.169 11.169 0 1 0-228.74 0z");
    ;
    @Getter
    private String path;


    SVGPathHolder(String path) {
        this.path = path;
    }

    public SVGPath
    to(Paint paint) {
        return createPath(getPath(), paint);
    }

    public static SVGPath createPath(String content, Paint paint) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(content);
        svgPath.setFill(paint);
        return svgPath;
    }

}