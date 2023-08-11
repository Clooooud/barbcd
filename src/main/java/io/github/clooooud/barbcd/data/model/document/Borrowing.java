package io.github.clooooud.barbcd.data.model.document;

import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.classes.Student;

import java.util.List;

public class Borrowing implements Saveable {

    private final int id;
    private final ViewableDocument borrowedDocument;
    private final Student student;
    private boolean finished;

    public Borrowing(int id, ViewableDocument borrowedDocument, Student student, boolean finished) {
        this.id = id;
        this.borrowedDocument = borrowedDocument;
        this.student = student;
        this.finished = finished;
    }

    public Borrowing(int id, ViewableDocument borrowedDocument, Student student) {
        this(id, borrowedDocument, student, false);
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Student getStudent() {
        return student;
    }

    public ViewableDocument getBorrowedDocument() {
        return borrowedDocument;
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.BORROWING;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public List<Object> getValues() {
        return List.of(
                id,
                this.student.getId(),
                this.borrowedDocument.getCategorie().equals(Categorie.MAGAZINE),
                ((Saveable) this.borrowedDocument).getId(),
                isFinished()
        );
    }
}
