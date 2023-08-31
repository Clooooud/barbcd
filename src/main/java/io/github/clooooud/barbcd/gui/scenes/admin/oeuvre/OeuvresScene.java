package io.github.clooooud.barbcd.gui.scenes.admin.oeuvre;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.model.document.Borrowing;
import io.github.clooooud.barbcd.data.model.document.Oeuvre;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;

import java.util.List;

public class OeuvresScene extends ListAdminScene<Oeuvre> {

    public OeuvresScene(BarBCD app) {
        super(app);
    }

    @Override
    protected List<Oeuvre> getObjects() {
        return this.getLibrary().getDocuments(SaveableType.OEUVRE).stream()
                .map(document -> (Oeuvre) document)
                .sorted().toList();
    }

    @Override
    protected void delete(Oeuvre oeuvre) {
        this.getLibrary().removeDocument(oeuvre);
        // We must also remove borrowings that have the oeuvre as borrowed document
        this.getLibrary().getDocuments(SaveableType.BORROWING).stream()
                .map(document -> (Borrowing) document)
                .filter(borrowing -> borrowing.getBorrowedDocument().equals(oeuvre))
                .forEach(this.getLibrary()::removeDocument);
    }

    @Override
    protected String getTitle() {
        return "Oeuvres";
    }

    @Override
    protected String getFilterString(Oeuvre oeuvre) {
        return oeuvre.getSearchString(this.getLibrary());
    }

    @Override
    protected RootAdminScene getRefreshedScene() {
        return new OeuvresScene(this.getApp());
    }

    @Override
    protected RootAdminScene getNewObjectScene() {
        return new NewOeuvreScene(this.getApp());
    }

    @Override
    protected RootAdminScene getObjectScene(Oeuvre oeuvre) {
        return new OeuvreScene(this.getApp(), oeuvre);
    }

    @Override
    protected String getListObjectName(Oeuvre oeuvre) {
        return oeuvre.getTitle() + " - " + oeuvre.getAuthor() + " (x" + oeuvre.getQuantity() + ")";
    }

    @Override
    protected String getListObjectDesc(Oeuvre oeuvre) {
        return "Édité par " + oeuvre.getEditor().getName() + " en " + oeuvre.getDate();
    }

    @Override
    protected String getDeleteObjectMessage() {
        return "Voulez-vous vraiment supprimer cette oeuvre ? Elle sera supprimée de la bibliothèque ainsi que tous les emprunts associés.";
    }

    @Override
    protected String getDeleteObjectsMessage() {
        return "Voulez-vous vraiment supprimer ces oeuvres ? Elles seront supprimées de la bibliothèque ainsi que tous les emprunts associés.";
    }
}
