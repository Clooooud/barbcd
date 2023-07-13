package io.github.clooooud.barbcd.data.model;

import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.document.Borrowing;
import io.github.clooooud.barbcd.data.model.document.ViewableDocument;
import io.github.clooooud.barbcd.util.AESUtil;
import io.github.clooooud.barbcd.util.Sha256Util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Library {

    private final Map<SaveableType, Set<Saveable>> saveables;

    private final Set<GSheetApi.DataRequest> dataUpdateList;

    private final String name;
    private String adminPassword;
    private User user;

    public Library(String name) {
        this.name = name;

        this.saveables = new HashMap<>();

        for (SaveableType saveableType : SaveableType.values()) {
            saveables.put(saveableType, ConcurrentHashMap.newKeySet());
        }

        dataUpdateList = new HashSet<>();
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

    public Set<GSheetApi.DataRequest> getDataUpdateList() {
        return dataUpdateList;
    }

    public Saveable getDocumentById(SaveableType type, int id) {
        return this.saveables.get(type)
                .stream()
                .filter(saveable -> saveable.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void createBorrowing(User user, ViewableDocument viewableDocument) {
        Borrowing borrowing = new Borrowing(
                getNextDocumentId(SaveableType.BORROWING),
                user,
                viewableDocument
        );
        addDocument(borrowing);
        markDocumentAsUpdated(borrowing);
    }

    public void createUser(String login, String password, String adminPassword) {
        User user = new User(
                this.getNextDocumentId(SaveableType.USER),
                login,
                Sha256Util.passToSha256(password),
                new AESUtil(password).encryptString(adminPassword)
        );
        addDocument(user);
        markDocumentAsUpdated(user);
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
        dataUpdateList.add(new GSheetApi.DataRequest(GSheetApi.RequestType.UPDATE, saveable));
    }

    public void removeDocument(Saveable saveable) {
        saveables.get(saveable.getSaveableType()).remove(saveable);
        dataUpdateList.add(new GSheetApi.DataRequest(GSheetApi.RequestType.DELETE, saveable));
    }

    public void addDocument(Saveable saveable) {
        saveables.get(saveable.getSaveableType()).add(saveable);
    }

    public String getName() {
        return this.name;
    }
}
