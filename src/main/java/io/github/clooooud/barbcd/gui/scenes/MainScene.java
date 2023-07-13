package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.model.document.ViewableDocument;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.clooooud.barbcd.gui.StageWrapper.getResource;

public class MainScene extends RootScene {

    private VBox contentBox;
    private VBox searchResults;
    private HBox searchBar;
    private VBox researchBox;
    private TextField searchField;
    private boolean searchBarMoving = false;

    public MainScene(BarBCD app) {
        super(app);
    }

    public static boolean isNodeClicked(double clickX, double clickY, Region node) {
        return (clickX >= (node.getLayoutX() + node.getTranslateX()) && clickX <= (node.getLayoutX() + node.getTranslateX()) + node.getWidth())
                && (clickY >= (node.getLayoutY() + node.getTranslateY()) && clickY <= (node.getLayoutY() + node.getTranslateY()) + node.getHeight());
    }

    @Override
    public HBox getHeader() {
        HBox header = super.getHeader();

        getClickableTitle().setOnMouseClicked(event -> {
            getHeaderBox().requestFocus();
            searchField.clear();
        });

        return header;
    }

    private List<ViewableDocument> getDocuments(String research) {
        return Stream.concat(
                getLibrary().getDocuments(SaveableType.MAGAZINE).stream(),
                getLibrary().getDocuments(SaveableType.OEUVRE).stream()
        ).map(saveable -> (ViewableDocument) saveable)
                .filter(document -> document.getSearchString(getLibrary()).toLowerCase().contains(research.toLowerCase()))
                .sorted((o1, o2) -> o1.getTitle().compareToIgnoreCase(o2.getTitle()))
                .collect(Collectors.toList());
    }

    public void updateSearchBar(boolean refresh, boolean focused) {
        if (!focused && !refresh) {
            contentBox.getChildren().subList(1, contentBox.getChildren().size()).clear();
            researchBox = null;
        }

        TranslateTransition translate = new TranslateTransition();
        translate.setNode(searchBar);
        translate.setDuration(Duration.seconds(refresh ? 0.0001 : 0.25));
        translate.setInterpolator(Interpolator.EASE_BOTH);
        double fromY = searchBar.getLayoutY() + searchBar.getTranslateY();
        double toY;
        if (focused) {
            toY = 20;
        } else {
            toY = contentBox.getHeight() / 2 - searchBar.getHeight() / 2;
        }
        translate.setByY(toY - fromY);
        translate.setOnFinished(event -> {
            searchBarMoving = false;
            if (focused && !refresh) {
                contentBox.setAlignment(Pos.TOP_CENTER);
                Region searchResults = getSearchResults();
                contentBox.getChildren().setAll(searchBar, searchResults);
            }
        });
        translate.play();
        searchBarMoving = true;
    }

    private VBox getSearchResults() {
        VBox vBox = new VBox();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.minViewportHeightProperty().bind(this.contentBox.heightProperty()
                .subtract(40)
                .subtract(120)
                .subtract(100));

        vBox.setMinWidth(500);
        vBox.setPrefWidth(500);
        vBox.setMaxWidth(800);
        vBox.setPadding(new Insets(0, 60, 0, 60));

        this.researchBox = new VBox();
        updateResearchBox();

        scrollPane.setContent(researchBox);
        vBox.getChildren().add(scrollPane);

        return vBox;
    }

    private void updateResearchBox() {
        if (this.researchBox == null) {
            return;
        }

        this.researchBox.getChildren().clear();


        List<ViewableDocument> documents = getDocuments(searchField.getText());

        if (documents.isEmpty()) {
            HBox hBox = new HBox();
            hBox.setPrefHeight(60);
            hBox.getStyleClass().add("document-line");
            hBox.setAlignment(Pos.CENTER);

            Label label = new Label("La BCD n'a aucune oeuvre enregistrée");
            label.setFont(Font.font(null, FontWeight.BOLD, 20));

            hBox.getChildren().add(label);

            this.researchBox.getChildren().add(hBox);
            return;
        }

        for (ViewableDocument document : documents) {
            this.researchBox.getChildren().add(getDocumentLine(document));
        }
    }

    private HBox getDocumentLine(ViewableDocument document) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getStyleClass().add("document-line");
        hBox.setPrefHeight(60);
        hBox.setPadding(new Insets(5, 10, 5, 10));

        ImageView imageView = new ImageView(new Image(getResource("assets/book.png")));
        hBox.getChildren().add(imageView);
        HBox.setMargin(imageView, new Insets(0, 5, 0, 0));

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(document.getTitle());
        title.getStyleClass().add("document-title");
        Label author = new Label(document.getAuthor());
        author.getStyleClass().add("document-author");

        vBox.getChildren().addAll(title, author);

        hBox.getChildren().add(vBox);
        HBox.setHgrow(vBox, Priority.ALWAYS);

        boolean isAvailable = document.isAvailable(this.getLibrary());
        Label availableLabel = new Label(isAvailable ? "Disponible" : "Indisponible");
        availableLabel.setFont(Font.font(null, FontWeight.BOLD,14));
        hBox.getChildren().add(availableLabel);
        ImageView availability = new ImageView(new Image(getResource(isAvailable ? "assets/check.png" : "assets/x.png")));
        hBox.getChildren().add(availability);

        return hBox;
    }

    @Override
    public void initContent(VBox vBox) {
        this.contentBox = vBox;
        vBox.getStyleClass().add("main-page-content");

        this.searchBar = new HBox();
        searchBar.setId("search-bar");
        searchBar.setMinSize(500, 120);
        searchBar.setPrefSize(500, 120);
        searchBar.setMaxSize(800, 120);

        ImageView searchIcon = new ImageView(new Image(getResource("assets/search.png")));
        this.searchField = new TextField();
        searchField.setPromptText("Rechercher un livre".toUpperCase());
        searchField.setMaxWidth(Double.MAX_VALUE);
        searchField.setId("search-field");
        searchField.setFocusTraversable(false);
        searchField.textProperty().addListener((observableValue, oldVal, newVal) -> Platform.runLater(this::updateResearchBox));

        searchBar.setOnMouseClicked(event -> searchField.requestFocus());

        contentBox.setOnMouseClicked(event -> {
            if (isNodeClicked(event.getX(), event.getY(), searchBar)) {
                return;
            }

            if (searchBarMoving) { // It can't be returned during transition
                return;
            }

            contentBox.requestFocus();
        });

        Platform.runLater(() -> {
            getApp().getStageWrapper().getStage()
                    .heightProperty()
                    .addListener(
                            (observableValue, oldVal, newVal) -> Platform.runLater(
                                    () -> updateSearchBar(true, searchField.isFocused())
                            )
                    );
        });

        searchField.focusedProperty().addListener((observableValue, oldVal, newVal) -> {
            if (contentBox.getScene() == null) {
                return;
            }

            if (!contentBox.getScene().getWindow().isFocused()) { // Prevent weird thing when using alt-tab / reducing the window
                return;
            }

            updateSearchBar(false, newVal);
            if (!newVal) {
                searchBar.setTranslateY(0);
            }
        });

        searchBar.getChildren().addAll(searchIcon, searchField);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        contentBox.getChildren().add(searchBar);

        searchBar.setOpacity(0);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateSearchBar(true, false);
                if (searchBar.getTranslateY() == 0) {
                    Platform.runLater(this);
                } else {
                    searchBar.setOpacity(1);
                }
            }
        });
    }
}
