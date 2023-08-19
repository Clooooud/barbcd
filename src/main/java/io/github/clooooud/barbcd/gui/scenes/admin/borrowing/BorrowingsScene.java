package io.github.clooooud.barbcd.gui.scenes.admin.borrowing;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.model.document.Borrowing;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import javafx.application.Platform;
import javafx.scene.control.TextField;

import java.util.List;

public class BorrowingsScene extends ListAdminScene<Borrowing> {

    public BorrowingsScene(BarBCD app) {
        super(app);
    }

    public BorrowingsScene(BarBCD app, String filter) {
        super(app);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TextField field = getFilter();

                if (field == null) {
                    Platform.runLater(this);
                    return;
                }

                field.setText(filter);
            }
        });
    }

    @Override
    protected List<Borrowing> getObjects() {
        return this.getLibrary().getDocuments(SaveableType.BORROWING).stream()
                .map(document -> (Borrowing) document)
                .sorted().toList();
    }

    @Override
    protected void delete(Borrowing object) {
        this.getLibrary().removeDocument(object);
    }

    @Override
    protected String getTitle() {
        return "Emprunts";
    }

    @Override
    protected String getFilterString(Borrowing object) {
        return object.getBorrowedDocument().getSearchString(this.getLibrary())
                + String.join(" ",
                object.getStudent().getFirstName(),
                object.getStudent().getLastName(),
                object.getStudent().getCurrentClass().getClassName()
        );
    }

    @Override
    protected RootAdminScene getRefreshedScene() {
        return new BorrowingsScene(this.getApp());
    }

    @Override
    protected RootAdminScene getNewObjectScene() {
        return new NewBorrowingScene(this.getApp());
    }

    @Override
    protected RootAdminScene getObjectScene(Borrowing object) {
        return new BorrowingScene(this.getApp(), object);
    }

    @Override
    protected String getListObjectName(Borrowing object) {
        return object.getBorrowedDocument().getTitle() + " - " + object.getBorrowedDocument().getAuthor();
    }

    @Override
    protected String getListObjectDesc(Borrowing object) {
        return "Emprunté par " + object.getStudent().getFirstName() + " " + object.getStudent().getLastName();
    }

    @Override
    protected String getDeleteObjectMessage() {
        return "Voulez-vous vraiment supprimer cet emprunt ?";
    }

    @Override
    protected String getDeleteObjectsMessage() {
        return "Voulez-vous vraiment supprimer ces emprunts ?";
    }
}
