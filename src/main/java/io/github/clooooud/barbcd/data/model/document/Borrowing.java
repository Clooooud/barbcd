package io.github.clooooud.barbcd.data.model.document;

import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.model.classes.Student;

import java.util.List;

public class Borrowing implements Saveable {

    private final int id;
    private final ViewableDocument borrowedDocument;
    private final Student student;

    public Borrowing(int id, ViewableDocument borrowedDocument, Student student) {
        this.id = id;
        this.borrowedDocument = borrowedDocument;
        this.student = student;
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
                this.borrowedDocument.getCategory().equals(Category.MAGAZINE),
                ((Saveable) this.borrowedDocument).getId()
        );
    }
}
