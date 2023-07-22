package io.github.clooooud.barbcd.data.model.classes;

import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Class implements Saveable {

    private int id;
    private String className;
    private final List<Student> students;

    public Class(int id, String className) {
        this.id = id;
        this.className = className;
        this.students = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    void addStudent(Student student) {
        students.add(student);
    }

    public List<Student> getStudents() {
        return students;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Class aClass = (Class) o;
        return Objects.equals(className, aClass.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className);
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.CLASS;
    }

    @Override
    public List<Object> getValues() {
        return List.of(id, className);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
