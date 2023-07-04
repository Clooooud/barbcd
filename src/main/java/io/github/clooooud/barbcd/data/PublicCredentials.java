package io.github.clooooud.barbcd.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import java.io.*;

public class PublicCredentials {

    private static final String PUBLIC_CREDENTIAL_PATH_NAME = "pu_credentials.json";

    private final File file = new File(PUBLIC_CREDENTIAL_PATH_NAME);

    private String passwordHash;
    private String apiKey;
    private String spreadsheetId;

    public PublicCredentials() {
        load();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSpreadsheetId() {
        return spreadsheetId;
    }

    public void setSpreadsheetId(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }

    public void save() {
        try (JsonWriter jsonWriter = new JsonWriter(new FileWriter(file))) {
            jsonWriter.setIndent("  ");

            jsonWriter.beginObject();

            jsonWriter.name("apiKey").value(apiKey);
            jsonWriter.name("passwordHash").value(passwordHash);
            jsonWriter.name("spreadsheetId").value(spreadsheetId);

            jsonWriter.endObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        if (!file.exists()) {
            createDefault();
            return;
        }

        try {
            JsonObject jsonObject = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
            this.passwordHash = jsonObject.get("passwordHash").getAsString();
            this.apiKey = jsonObject.get("apiKey").getAsString();
            this.spreadsheetId = jsonObject.get("spreadsheetId").getAsString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void createDefault() {
        passwordHash = "";
        apiKey = "";
        spreadsheetId = "";

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        save();
    }
}
