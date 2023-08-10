package io.github.clooooud.barbcd.data.model;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.classes.Student;
import io.github.clooooud.barbcd.data.model.document.Borrowing;
import io.github.clooooud.barbcd.data.model.document.Editor;
import io.github.clooooud.barbcd.data.model.document.ViewableDocument;
import io.github.clooooud.barbcd.util.AESUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Library implements Saveable {

    private final Map<SaveableType, Set<Saveable>> saveables;

    private final Map<GSheetApi.RequestType, Collection<Saveable>> dataUpdateList;

    private final BarBCD app;
    private String name;
    private String adminPassword;
    private User user;

    public Library(BarBCD app, String name) {
        this.app = app;
        this.name = name;

        this.saveables = new HashMap<>();

        for (SaveableType saveableType : SaveableType.values()) {
            saveables.put(saveableType, ConcurrentHashMap.newKeySet());
        }

        saveables.get(SaveableType.SETTINGS).add(this);

        dataUpdateList = new ConcurrentHashMap<>();

        for (GSheetApi.RequestType requestType : GSheetApi.RequestType.values()) {
            dataUpdateList.put(requestType, ConcurrentHashMap.newKeySet());
        }
    }

    public BarBCD getApp() {
        return app;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void disconnectUser() {
        setAdminPassword("");
        setUser(null);
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public boolean isLoggedIn() {
        return adminPassword != null && !adminPassword.isEmpty();
    }

    public Set<User> getUsers() {
        return this.saveables.get(SaveableType.USER).stream()
                .map(saveable -> (User) saveable)
                .collect(Collectors.toSet());
    }

    public Set<Saveable> getDocuments(SaveableType type) {
        return this.saveables.get(type);
    }

    public Map<GSheetApi.RequestType, Collection<Saveable>> getDataUpdateList() {
        return dataUpdateList;
    }

    public Saveable getDocumentById(SaveableType type, int id) {
        return this.saveables.get(type)
                .stream()
                .filter(saveable -> saveable.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void createEditor(String name) {
        Editor editor = new Editor(
                getNextDocumentId(SaveableType.EDITOR),
                name
        );
        addDocument(editor);
    }

    public void createBorrowing(User user, ViewableDocument viewableDocument, Student student) {
        Borrowing borrowing = new Borrowing(
                getNextDocumentId(SaveableType.BORROWING),
                user,
                viewableDocument,
                student
        );
        addDocument(borrowing);
    }

    public void createUser(String login, String password, String adminPassword) {
        User user = new User(
                this.getNextDocumentId(SaveableType.USER),
                login,
                new AESUtil(adminPassword).encryptString(password),
                new AESUtil(password).encryptString(adminPassword)
        );
        addDocument(user);
    }

    public User getUser(String login) {
        return getUsers().stream()
                .filter(user -> user.getLogin().equalsIgnoreCase(login))
                .findFirst().orElse(null);
    }

    public int getNextDocumentId(SaveableType type) {
        return saveables.get(type).stream().mapToInt(Saveable::getId).max().orElse(0) + 1;
    }

    public void markDocumentAsUpdated(Saveable saveable) {
        dataUpdateList.get(GSheetApi.RequestType.UPDATE).add(saveable);
    }

    public void removeDocument(Saveable saveable) {
        saveables.get(saveable.getSaveableType()).remove(saveable);
        dataUpdateList.get(GSheetApi.RequestType.DELETE).add(saveable);
    }

    public boolean addDocument(Saveable saveable) {
        boolean result = saveables.get(saveable.getSaveableType()).add(saveable);
        if (result) {
            markDocumentAsUpdated(saveable);
        }
        return result;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.markDocumentAsUpdated(this);
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.SETTINGS;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public List<Object> getValues() {
        return List.of(getId(), this.name);
    }
}
