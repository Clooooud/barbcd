package io.github.clooooud.barbcd.data.model;

import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.auth.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Library {

    private final Map<SaveableType, Set<Saveable>> saveables;

    private final Set<GSheetApi.DataRequest> dataUpdateList;

    private final String name;
    private String adminPassword;

    public Library(String name) {
        this.name = name;

        this.saveables = new HashMap<>();

        for (SaveableType saveableType : SaveableType.values()) {
            saveables.put(saveableType, new HashSet<>());
        }

        dataUpdateList = new HashSet<>();
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void clearAdminPassword() {
        setAdminPassword("");
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public boolean isAdmin() {
        return !adminPassword.isEmpty();
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
}
