package io.github.clooooud.barbcd.gui.scenes.admin.student;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.data.model.classes.Student;
import io.github.clooooud.barbcd.data.model.document.Borrowing;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.borrowing.BorrowingsScene;

import java.util.List;

public class ClassScene extends ListAdminScene<Student> {

    private final Class classObject;

    public ClassScene(BarBCD app, Class classObject) {
        super(app);
        this.classObject = classObject;
    }

    @Override
    protected List<Student> getObjects() {
        return this.getLibrary().getDocuments(SaveableType.STUDENT).stream()
                .map(document -> (Student) document)
                .filter(student -> student.getCurrentClass().equals(this.classObject))
                .sorted().toList();
    }

    @Override
    protected void delete(Student object) {
        this.getLibrary().removeDocument(object);
        this.getLibrary().getDocuments(SaveableType.BORROWING).stream()
                .map(document -> (Borrowing) document)
                .filter(borrowing -> borrowing.getStudent().equals(object))
                .forEach(borrowing -> this.getLibrary().removeDocument(borrowing));
    }

    @Override
    protected String getTitle() {
        return "Classe - " + this.classObject.getClassName();
    }

    @Override
    protected String getFilterPrompt() {
        return "Rechercher un élève";
    }

    @Override
    protected String getFilterString(Student object) {
        return object.getFirstName() + " " + object.getLastName();
    }

    @Override
    protected RootAdminScene getRefreshedScene() {
        return new ClassScene(this.getApp(), this.classObject);
    }

    @Override
    protected RootAdminScene getNewObjectScene() {
        return new NewStudentScene(this.getApp(), this.classObject);
    }

    @Override
    protected RootAdminScene getObjectScene(Student object) {
        return new BorrowingsScene(this.getApp(), object.getFirstName() + " " + object.getLastName());
    }

    @Override
    protected String getListObjectName(Student object) {
        return object.getFirstName() + " " + object.getLastName();
    }

    @Override
    protected String getListObjectDesc(Student object) {
        List<Borrowing> borrowings = this.getLibrary().getDocuments(SaveableType.BORROWING).stream()
                .map(document -> (Borrowing) document)
                .filter(borrowing -> borrowing.getStudent().equals(object)).toList();

        long borrowCount = borrowings.size();

        return borrowCount + " emprunt" + (borrowCount > 1 ? "s" : "") + " en cours";
    }

    @Override
    protected String getDeleteObjectMessage() {
        return "Voulez-vous vraiment supprimer cet élève ? Supprimer un élève supprime aussi ses emprunts.";
    }

    @Override
    protected String getDeleteObjectsMessage() {
        return "Voulez-vous vraiment supprimer ces élèves ? Supprimer un élève supprime aussi ses emprunts.";
    }

    @Override
    protected java.lang.Class<?> getParentClass() {
        return ClassesScene.class;
    }
}
