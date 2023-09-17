package io.github.clooooud.barbcd.gui.element.components;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class DeleteSearchFieldComponent<E> extends FormComponent {

    public DeleteSearchFieldComponent(String componentLabel, ObservableList<E> list) {
        this(componentLabel, list, null);
    }

    public DeleteSearchFieldComponent(String componentLabel, ObservableList<E> list, String description) {
        super(componentLabel, new DeleteSearchField<>(list), description);
    }

    public E getSelected() {
        return getSearchField().getSelectionModel().getSelectedItem();
    }

    public SearchFieldComponent.SearchField<E> getSearchField() {
        return ((DeleteSearchField<E>) this.getCTA()).searchField;
    }

    public static class DeleteSearchField<E> extends HBox {

        private final SearchFieldComponent.SearchField<E> searchField;

        public DeleteSearchField(ObservableList<E> list) {
            this.searchField = new SearchFieldComponent.SearchField<>(list);
            Button deleteButton = new Button("X");
            deleteButton.setOnAction(event -> {
                this.searchField.getSelectionModel().clearSelection();
            });

            this.setSpacing(5);
            this.getChildren().addAll(searchField, deleteButton);
            HBox.setHgrow(searchField, Priority.ALWAYS);
        }
    }
}
