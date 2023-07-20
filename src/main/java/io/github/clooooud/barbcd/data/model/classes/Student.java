package io.github.clooooud.barbcd.data.model.classes;

import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;

import java.util.List;

public class Student implements Saveable {

    private int id;
    private String firstName;
    private String lastName;
    private Class currentClass;

    public Student(int id, String firstName, String lastName, Class currentClass) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentClass = currentClass;

        this.currentClass.addStudent(this);
    }

    public Class getCurrentClass() {
        return currentClass;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.STUDENT;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public List<Object> getValues() {
        return List.of(this.id, this.firstName, this.lastName, this.currentClass.getId());
    }
}
