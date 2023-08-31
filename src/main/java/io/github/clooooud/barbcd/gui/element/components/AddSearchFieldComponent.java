package io.github.clooooud.barbcd.gui.element.components;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class AddSearchFieldComponent<E> extends FormComponent {

    public AddSearchFieldComponent(String componentLabel, ObservableList<E> list, EventHandler<ActionEvent> addAction) {
        this(componentLabel, list, addAction, null);
    }

    public AddSearchFieldComponent(String componentLabel, ObservableList<E> list, EventHandler<ActionEvent> addAction, String description) {
        super(componentLabel, new AddSearchField<>(list, addAction), description);
    }

    public E getSelected() {
        return getSearchField().getSelectionModel().getSelectedItem();
    }

    public SearchFieldComponent.SearchField<E> getSearchField() {
        return ((AddSearchField<E>) this.getCTA()).searchField;
    }

    public static class AddSearchField<E> extends HBox {

        private final SearchFieldComponent.SearchField<E> searchField;

        public AddSearchField(ObservableList<E> list, EventHandler<ActionEvent> addAction) {
            this.searchField = new SearchFieldComponent.SearchField<>(list);
            Button addButton = new Button("+");
            addButton.setOnAction(addAction);

            this.setSpacing(5);
            this.getChildren().addAll(searchField, addButton);
            HBox.setHgrow(searchField, Priority.ALWAYS);
        }
    }
}
