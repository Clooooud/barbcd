package io.github.clooooud.barbcd.gui.content;

import io.github.clooooud.barbcd.gui.RootScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainPage extends ContentBox {

    @Override
    public Parent getContent() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(75));

        HBox hBox = new HBox();
        hBox.setId("search-bar");
        hBox.setPrefSize(500, 120);
        hBox.setMaxSize(800, 120);
        hBox.setPadding(new Insets(40));
        hBox.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView(new Image(RootScene.getResource("assets/search.png")));
        Label label = new Label("Rechercher un livre".toUpperCase());
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font(null, FontWeight.BOLD, 24));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        hBox.getChildren().addAll(imageView, label);
        HBox.setHgrow(label, Priority.ALWAYS);

        vBox.getChildren().add(hBox);

        return vBox;
    }
}
