package io.github.clooooud.barbcd.data.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import java.io.*;

public class PublicCredentials {

    private static final String PUBLIC_CREDENTIAL_PATH_NAME = "pu_credentials.json";

    private final File file = new File(PUBLIC_CREDENTIAL_PATH_NAME);

    private String apiKey;
    private String spreadsheetId;

    private boolean fileExisted = true;

    public PublicCredentials() {
        load();
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

    public boolean isFileExisted() {
        return fileExisted;
    }

    public boolean isEmpty() {
        if (this.spreadsheetId == null || this.apiKey == null) {
            return false;
        }

        return this.spreadsheetId.isEmpty() || this.apiKey.isEmpty();
    }

    public void save() {
        try (JsonWriter jsonWriter = new JsonWriter(new FileWriter(file))) {
            jsonWriter.setIndent("  ");

            jsonWriter.beginObject();
            jsonWriter.name("apiKey").value(apiKey);
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
            this.apiKey = jsonObject.get("apiKey").getAsString();
            this.spreadsheetId = jsonObject.get("spreadsheetId").getAsString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void createDefault() {
        fileExisted = false;
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
