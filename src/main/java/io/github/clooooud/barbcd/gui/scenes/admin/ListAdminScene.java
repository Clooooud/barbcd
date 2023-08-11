package io.github.clooooud.barbcd.gui.scenes.admin;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.gui.StageWrapper;
import io.github.clooooud.barbcd.gui.element.ScrollBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ListAdminScene<T extends Saveable> extends RootAdminScene {

    private final Map<CheckBox, T> checkedObjects = new HashMap<>();
    private final Map<T, HBox> objectBoxes = new HashMap<>();

    private VBox contentBox;
    private TextField filter;
    protected HBox addButtonBox;
    private Button deleteButton;

    public ListAdminScene(BarBCD app) {
        super(app);
    }

    protected List<T> getSelectedObjects() {
        return checkedObjects.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .map(Map.Entry::getValue)
                .toList();
    }

    private boolean isAdmin() {
        return this.getLibrary().getUser().isAdmin();
    }

    protected abstract List<T> getObjects();

    protected abstract void massDelete();

    protected abstract void deleteObject(T object);

    protected abstract HBox createObjectBox(T object);

    protected abstract String getTitle();

    protected abstract String getFilterPrompt();

    protected abstract String getFilterString(T object);

    protected abstract RootAdminScene getNewObjectScene();

    protected abstract RootAdminScene getObjectScene(T object);

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.getStyleClass().add("admin-content");

        Label label = new Label(getTitle());
        label.getStyleClass().add("admin-scene-title");

        vBox.getChildren().add(label);

        HBox utilBar = new HBox();
        utilBar.setSpacing(5);
        utilBar.setAlignment(Pos.CENTER);

        Label filterLabel = new Label("Filtre");
        filterLabel.setFont(Font.font(null, FontWeight.BOLD, 14));

        filter = new TextField();
        filter.setFocusTraversable(false);
        filter.setPromptText(getFilterPrompt());
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.strip().equals(newValue.strip())) {
                return;
            }

            updateContent();
        });

        deleteButton = new Button();
        ImageView imageView = new ImageView(new Image(StageWrapper.getResource("assets/trash-2.png")));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        deleteButton.setGraphic(imageView);
        deleteButton.setCursor(Cursor.HAND);
        deleteButton.setOnAction(event -> massDelete());
        deleteButton.setDisable(true);

        utilBar.getChildren().addAll(filterLabel, filter, deleteButton);
        vBox.getChildren().add(utilBar);

        contentBox = createContent();
        ScrollBox scrollBox = new ScrollBox(vBox, contentBox, true);
        scrollBox.setMaxWidth(800);
        vBox.getChildren().add(scrollBox);
    }

    private void updateContent() {
        if (filter.getText().isBlank()) {
            contentBox.getChildren().setAll(objectBoxes.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList());
        } else {
            contentBox.getChildren().setAll(objectBoxes.entrySet().stream().filter(entry -> {
                T object = entry.getKey();
                return getFilterString(object).contains(filter.getText());
            }).sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList());
        }
        contentBox.getChildren().add(addButtonBox);
    }

    private VBox createContent() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        List<T> list = this.getObjects();

        for (int i = 0; i < list.size(); i++) {
            T object = list.get(i);

            HBox objectLine = new HBox();
            objectLine.setAlignment(Pos.CENTER_LEFT);
            objectLine.setSpacing(10);
            objectLine.setPadding(new Insets(0, 0, 0, 10));

            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                deleteButton.setDisable(getSelectedObjects().isEmpty());
            });
            checkedObjects.put(checkBox, object);

            HBox objectBox = createObjectBox(object);

            if (object instanceof User) {
                if (((User) object).isAdmin()) {
                    checkBox.setDisable(true);
                    checkBox.setVisible(false);
                }
            }

            objectLine.getChildren().addAll(checkBox, objectBox);
            HBox.setHgrow(objectBox, Priority.ALWAYS);

            vBox.getChildren().add(objectLine);
            objectBoxes.put(object, objectLine);
        }

        addButtonBox = new HBox();
        addButtonBox.setAlignment(Pos.CENTER);

        ImageView addButton = new ImageView(new Image(StageWrapper.getResource("assets/add.png")));
        addButton.setFitWidth(50);
        addButton.setFitHeight(50);
        addButton.setCursor(Cursor.HAND);
        addButton.setOnMouseClicked(event -> this.getApp().getStageWrapper().setContent(getNewObjectScene()));

        addButtonBox.getChildren().add(addButton);
        vBox.getChildren().add(addButtonBox);

        if (!isAdmin()) {
            addButtonBox.setVisible(false);
            addButtonBox.setDisable(true);
        }

        return vBox;
    }
}