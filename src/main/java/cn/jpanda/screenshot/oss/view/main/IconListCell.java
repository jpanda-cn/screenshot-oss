package cn.jpanda.screenshot.oss.view.main;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

public class IconListCell extends ListCell<IconLabel> {
    @Override
    protected void updateItem(IconLabel item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        setText(null);
        if (item != null) {
            setGraphic(loadImageView(item.getIcon()));
            setText(loadText(item.getText()));
        }
    }

    protected ImageView loadImageView(String url){
        ImageView imageView = new ImageView(url);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        return imageView;
    }

    protected String loadText(String text){
        return text;
    }
}
