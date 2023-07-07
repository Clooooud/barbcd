package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.model.Library;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AuthScene extends RootScene {

    private Library library;

    private TextField passwordField;

    public AuthScene(BarBCD app) {
        super(app);
        this.library = app.getLibrary();
    }

    @Override
    public void initContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        VBox formBox = new VBox();
        formBox.setMinWidth(300);
        formBox.setPrefWidth(300);
        formBox.setMaxWidth(300);
        formBox.setPadding(new Insets(20));
        formBox.setSpacing(10);
        formBox.setAlignment(Pos.CENTER_RIGHT);
        formBox.setStyle("-fx-background-color: derive(white, -5%); -fx-background-radius: 10; -fx-border-color: derive(white, -50%); -fx-border-width: 1; -fx-border-radius: 10;");

        Label titleLabel = new Label("Authentification");
        titleLabel.setFont(Font.font(null, FontWeight.BOLD, 28));
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        Separator separator = new Separator(Orientation.HORIZONTAL);

        VBox passwordBox = new VBox();

        Label passwordLabel = new Label("Mot de passe");
        passwordLabel.setFont(Font.font(null, FontWeight.BOLD, 16));

        this.passwordField = new TextField();
        this.passwordField.setMaxWidth(Double.MAX_VALUE);

        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        Button button = new Button("Se connecter");
        button.setStyle("-fx-border-color: derive(white, -20%)");

        formBox.getChildren().addAll(titleLabel, separator, passwordBox, button);

        vBox.getChildren().add(formBox);
    }
}
