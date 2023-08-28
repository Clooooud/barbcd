package io.github.clooooud.barbcd.gui.scenes.admin;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.gui.StageWrapper;
import io.github.clooooud.barbcd.gui.element.ScrollBox;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
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
    private HBox addButtonBox;
    private Button deleteButton;

    public ListAdminScene(BarBCD app) {
        super(app);
    }

    protected abstract List<T> getObjects();

    protected abstract void delete(T object);

    protected abstract String getTitle();

    protected String getFilterPrompt() {
        return "Rechercher";
    }

    protected abstract String getFilterString(T object);

    protected abstract RootAdminScene getRefreshedScene();

    protected abstract RootAdminScene getNewObjectScene();

    protected abstract RootAdminScene getObjectScene(T object);

    protected abstract String getListObjectName(T object);

    protected abstract String getListObjectDesc(T object);

    protected boolean canDeleteObject(T object) {
        return isAdmin();
    }

    protected abstract String getDeleteObjectMessage();

    protected abstract String getDeleteObjectsMessage();

    protected List<T> getSelectedObjects() {
        return checkedObjects.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .map(Map.Entry::getValue)
                .toList();
    }

    protected TextField getFilter() {
        return filter;
    }

    private boolean isAdmin() {
        return this.getLibrary().getUser().isAdmin();
    }

    private void deleteObject(T object) {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                getDeleteObjectMessage()
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                delete(object);
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(getRefreshedScene());
            }
        });
    }

    private void massDeleteObjects() {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                getDeleteObjectsMessage()
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                getSelectedObjects().forEach(this::delete);
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(getRefreshedScene());
            }
        });
    }

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

        utilBar.getChildren().addAll(filterLabel, filter);

        if (isAdmin()) {
            deleteButton = new Button();
            ImageView imageView = new ImageView(new Image(StageWrapper.getResource("assets/trash-2.png")));
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            deleteButton.setGraphic(imageView);
            deleteButton.setCursor(Cursor.HAND);
            deleteButton.setOnAction(event -> massDeleteObjects());
            deleteButton.setDisable(true);
            utilBar.getChildren().add(deleteButton);
        }

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

        for (T object : list) {
            HBox objectLine = new HBox();
            objectLine.setAlignment(Pos.CENTER_LEFT);
            objectLine.setSpacing(10);
            objectLine.setPadding(new Insets(0, 0, 0, 10));

            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> deleteButton.setDisable(getSelectedObjects().isEmpty()));
            checkedObjects.put(checkBox, object);

            if (isAdmin()) {
                objectLine.getChildren().add(checkBox);
            }

            if (!canDeleteObject(object)) {
                checkBox.setVisible(false);
                checkBox.setDisable(true);
            }

            HBox objectBox = createObjectBox(object);

            objectLine.getChildren().add(objectBox);
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

    protected HBox createObjectBox(T object) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("list-elem");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setPrefHeight(50);
        vBox.setMinHeight(50);
        vBox.setMaxHeight(50);

        Label nameLabel = new Label(getListObjectName(object));
        Label classLabel = new Label(getListObjectDesc(object));

        nameLabel.getStyleClass().add("list-elem-title");
        classLabel.getStyleClass().add("list-elem-content");

        vBox.getChildren().addAll(nameLabel, classLabel);
        hBox.getChildren().add(vBox);
        HBox.setHgrow(vBox, Priority.ALWAYS);

        if (!canDeleteObject(object)) {
            hBox.setOnMouseClicked(event -> this.getApp().getStageWrapper().setContent(getObjectScene(object)));
            return hBox;
        }

        HBox deleteButtonBox = new HBox();
        deleteButtonBox.setAlignment(Pos.CENTER);
        deleteButtonBox.setCursor(Cursor.HAND);
        deleteButtonBox.setOnMouseClicked(event -> deleteObject(object));

        ImageView deleteButton = new ImageView(new Image(StageWrapper.getResource("assets/x.png")));
        deleteButton.setFitWidth(25);
        deleteButton.setFitHeight(25);

        deleteButtonBox.getChildren().add(deleteButton);

        hBox.getChildren().add(deleteButtonBox);

        hBox.setOnMouseClicked(event -> {
            if (GuiUtil.isNodeClicked(event.getX(), event.getY(), deleteButtonBox)) {
                return;
            }

            this.getApp().getStageWrapper().setContent(getObjectScene(object));
        });

        return hBox;
    }
}
