package io.github.clooooud.barbcd.data.api;

import com.google.api.client.googleapis.batch.BatchCallback;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonErrorContainer;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.Library;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.data.model.classes.Responsibility;
import io.github.clooooud.barbcd.data.model.classes.Student;
import io.github.clooooud.barbcd.data.model.document.*;
import io.github.clooooud.barbcd.util.AESUtil;
import javafx.application.Platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

public class GSheetApi {

    private static final String SPREADSHEET_NAME = "BarBCD Data";
    public static final String PRIVATE_CREDENTIAL_PATH_NAME = "pr_credentials.enc";

    private static final List<String> ADMIN_SCOPES = Arrays.asList(SheetsScopes.DRIVE, SheetsScopes.SPREADSHEETS);

    private final PublicCredentials credentials;

    private Drive driveService;
    private Sheets sheetsService;
    private Sheets userSheetsService;

    private final List<String> clearRangeList = new ArrayList<>();
    private final List<ValueRange> updateRangeList = new ArrayList<>();

    public GSheetApi(PublicCredentials credentials) {
        this.credentials = credentials;
        init();
    }

    public void init() {
        initUserSheetsService();
    }

    public void closeAdminMode() {
        sheetsService = null;
        driveService = null;
    }

    public void initAdmin(String adminPassword) {
        AESUtil aesUtil = new AESUtil(adminPassword);
        String credentials = aesUtil.decrypt(PRIVATE_CREDENTIAL_PATH_NAME);

        initSheetsService(credentials);
        initDriveService(credentials);
    }

    public void reset() throws IOException {
        if (driveService == null) {
            throw new IllegalArgumentException();
        }

        BatchRequest batch = driveService.batch();

        List<File> fileList = driveService.files().list().execute().getFiles();
        for (File file : fileList) {
            if (file.getId() == null) {
                continue;
            }
            batch.queue(driveService.files().delete(file.getId()).buildHttpRequest(), Void.class, GoogleJsonErrorContainer.class, new BatchCallback<>() {
                @Override
                public void onSuccess(Void unused, HttpHeaders httpHeaders) {

                }

                @Override
                public void onFailure(GoogleJsonErrorContainer googleJsonErrorContainer, HttpHeaders httpHeaders) {

                }
            });
        }

        if (batch.size() > 0) {
            batch.execute();
        }

        this.credentials.setSpreadsheetId("");
        this.credentials.save();
    }

    public void save(Library library) throws IOException {
        if (driveService == null || sheetsService == null) {
            throw new IllegalArgumentException();
        }

        File file = getFile();
        boolean firstSetup = file == null;
        if (!firstSetup && (credentials.getSpreadsheetId() == null || credentials.getSpreadsheetId().isEmpty())) {
            credentials.setSpreadsheetId(file.getId());
            credentials.save();
        }

        if (firstSetup) {
            Spreadsheet spreadsheet = initializeSpreadsheet();
            this.credentials.setSpreadsheetId(spreadsheet.getSpreadsheetId());
            this.credentials.save();

            for (SaveableType type : SaveableType.values()) {
                pushHeader(
                        type.getSheetName(),
                        type.getHeaders()
                );
            }

            // Default Magazine Categorie
            library.addDocument(Category.MAGAZINE);
        }

        library.getDataUpdateList().get(RequestType.DELETE).removeIf(saveable -> {
            pushClear(saveable);
            return true;
        });
        library.getDataUpdateList().get(RequestType.UPDATE).removeIf(saveable -> {
            pushLine(saveable);
            return true;
        });
        this.clearLines();
        this.writeLines();
    }

    public void load(Library library) throws IOException {
        Spreadsheet spreadsheet = getSpreadsheet();

        for (SaveableType type : SaveableType.getOrderedTypes()) {
            library.getDocuments(type).clear();

            String sheetName = type.getSheetName();
            Sheet sheet = spreadsheet.getSheets().stream()
                    .filter(sheet1 -> sheet1.getProperties().getTitle().equals(sheetName))
                    .findFirst().orElseThrow();

            for (GridData data : sheet.getData()) {
                if (data == null || data.getRowData() == null) {
                    continue;
                }

                List<RowData> dataRowData = data.getRowData();
                for (int i = 0; i < dataRowData.size(); i++) {
                    if (i == 0) { // Skip the header
                        continue;
                    }

                    RowData rowData = dataRowData.get(i);
                    List<CellData> unformattedValues = rowData.getValues();

                    if (unformattedValues == null) {
                        continue;
                    }

                    List<String> values = unformattedValues.stream().map(CellData::getFormattedValue).toList();
                    generateAndStoreComponent(library, values, type);
                }
            }

            // Bypasses every addDocument data update request
            library.getDataUpdateList().values().forEach(Collection::clear);
        }
    }

    private void generateAndStoreComponent(Library library, List<String> values, SaveableType type) {
        final int id = Integer.parseInt(values.get(0));
        switch (type) {
            case CATEGORY -> {
                Category category = new Category(
                        id,
                        values.get(1),
                        Integer.parseInt(values.get(2))
                );
                library.addDocument(category);
            }
            case EDITOR -> {
                Editor editor = new Editor(
                        id,
                        values.get(1)
                );
                library.addDocument(editor);
            }
            case MAGAZINE_SERIE -> {
                MagazineSerie magazineSerie = new MagazineSerie(
                        id,
                        values.get(1),
                        values.get(2),
                        (Editor) library.getDocumentById(SaveableType.EDITOR, Integer.parseInt(values.get(3)))
                );
                library.addDocument(magazineSerie);
            }
            case MAGAZINE -> {
                Magazine magazine = new Magazine(
                        id,
                        values.get(1),
                        Integer.parseInt(values.get(2)),
                        Integer.parseInt(values.get(3)),
                        Integer.parseInt(values.get(4)),
                        Integer.parseInt(values.get(5)),
                        (MagazineSerie) library.getDocumentById(SaveableType.MAGAZINE_SERIE, Integer.parseInt(values.get(6)))
                );
                library.addDocument(magazine);
            }
            case OEUVRE -> {
                Oeuvre oeuvre = new Oeuvre(
                        id,
                        values.get(1),
                        values.get(2),
                        values.get(3),
                        (Editor) library.getDocumentById(SaveableType.EDITOR, Integer.parseInt(values.get(4))),
                        (Category) library.getDocumentById(SaveableType.CATEGORY, Integer.parseInt(values.get(5))),
                        Integer.parseInt(values.get(6)),
                        Integer.parseInt(values.get(7))
                );
                library.addDocument(oeuvre);
            }
            case USER -> {
                User user = new User(
                        id,
                        values.get(1),
                        values.get(2),
                        values.get(3)
                );
                library.addDocument(user);
            }
            case BORROWING -> {
                Student student = (Student) library.getDocumentById(SaveableType.STUDENT, Integer.parseInt(values.get(1)));
                boolean isMagazine = Boolean.parseBoolean(values.get(2));
                SaveableType documentType = isMagazine ? SaveableType.MAGAZINE : SaveableType.OEUVRE;
                ViewableDocument document = (ViewableDocument) library.getDocumentById(documentType, Integer.parseInt(values.get(3)));

                Borrowing borrowing = new Borrowing(
                        id,
                        document,
                        student
                );
                library.addDocument(borrowing);
            }
            case CLASS -> {
                Class clazz = new Class(
                        id,
                        values.get(1)
                );
                library.addDocument(clazz);
            }
            case STUDENT -> {
                Student student = new Student(
                        id,
                        values.get(1),
                        values.get(2),
                        (Class) library.getDocumentById(SaveableType.CLASS, Integer.parseInt(values.get(3)))
                );
                library.addDocument(student);
            }
            case SETTINGS -> {
                library.getDocuments(SaveableType.SETTINGS).add(library);
                library.setName(values.get(1));
                Platform.runLater(() -> library.getApp().getStageWrapper().getScene().updateHeader());
            }
            case RESPONSIBILITY -> {
                User user = (User) library.getDocumentById(SaveableType.USER, Integer.parseInt(values.get(1)));
                Class clazz = (Class) library.getDocumentById(SaveableType.CLASS, Integer.parseInt(values.get(2)));

                Responsibility responsibility = new Responsibility(
                        id,
                        user,
                        clazz
                );
                library.addDocument(responsibility);
            }
        }
    }

    private void pushClear(Saveable saveable) {
        pushClear(saveable.getSaveableType().getSheetName(), saveable.getId());

    }

    private void pushClear(String sheetName, int lineId) {
        lineId++;
        clearRangeList.add(sheetName + "!" + lineId + ":" + lineId);
    }

    private void clearLines() {
        if (sheetsService == null) {
            throw new IllegalArgumentException();
        }

        if (clearRangeList.isEmpty()) {
            return;
        }

        try {
            sheetsService.spreadsheets().values().batchClear(
                    credentials.getSpreadsheetId(),
                    new BatchClearValuesRequest()
                            .setRanges(clearRangeList)
            ).execute();
            clearRangeList.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void pushHeader(String sheetName, List<Object> values) {
        pushLine(sheetName, 0, values);
    }

    private void pushLine(Saveable saveable) {
        pushLine(saveable.getSaveableType().getSheetName(), saveable.getId(), saveable.getValues());
    }

    private void pushLine(String sheetName, int lineId, List<Object> values) {
        ValueRange valueRange = new ValueRange();
        valueRange.setRange(sheetName + "!A" + (lineId+1));
        valueRange.setValues(Collections.singletonList(values));

        updateRangeList.add(valueRange);
    }

    private void writeLines() {
        if (sheetsService == null) {
            throw new IllegalArgumentException();
        }

        if (updateRangeList.isEmpty()) {
            return;
        }

        try {
            sheetsService.spreadsheets().values().batchUpdate(
                    credentials.getSpreadsheetId(),
                    new BatchUpdateValuesRequest()
                            .setValueInputOption("RAW")
                            .setData(updateRangeList)
            ).execute();
            updateRangeList.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Spreadsheet getSpreadsheet() throws IOException {
        if (userSheetsService == null) {
            throw new IllegalArgumentException();
        }

        Sheets.Spreadsheets.Get request = userSheetsService.spreadsheets().get(this.credentials.getSpreadsheetId())
                .setIncludeGridData(true);
        // Set the API key in the request
        HttpHeaders requestHeaders = request.getRequestHeaders();
        requestHeaders.set("x-goog-api-key", this.credentials.getApiKey());

        return request.execute();
    }

    private File getFile() throws IOException {
        if (driveService == null) {
            throw new IllegalArgumentException();
        }

        if (credentials.getSpreadsheetId().isEmpty()) {
            return null;
        }

        Drive.Files.Get request = driveService.files().get(credentials.getSpreadsheetId());

        return request.execute();
    }

    private Spreadsheet initializeSpreadsheet() throws IOException {
        // Initialize the spreadsheet
        Spreadsheet spreadsheet = new Spreadsheet();
        spreadsheet.setProperties(new SpreadsheetProperties());
        spreadsheet.getProperties().setTitle(SPREADSHEET_NAME);

        // Creation of the sheets
        List<String> sheetNames = SaveableType.getOrderedTypes().stream().map(SaveableType::getSheetName).toList();
        spreadsheet.setSheets(sheetNames
                .stream()
                .map(name -> new Sheet().setProperties(new SheetProperties().setTitle(name)))
                .collect(Collectors.toList())
        );
        spreadsheet = sheetsService.spreadsheets().create(spreadsheet).execute();

        // Reading permissions for everyone
        driveService.permissions().create(spreadsheet.getSpreadsheetId(), new Permission().setRole("reader").setType("anyone")).execute();

        return spreadsheet;
    }

    private void initSheetsService(String decryptedFile) {
        if (decryptedFile == null) {
            throw new IllegalArgumentException();
        }

        try {
            this.sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), getCredentials(decryptedFile))
                    .setApplicationName("barbcd")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initUserSheetsService() {
        try {
            this.userSheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), null)
                    .setApplicationName("barbcd")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initDriveService(String decryptedFile) {
        if (decryptedFile == null) {
            throw new IllegalArgumentException();
        }

        try {
            this.driveService = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), getCredentials(decryptedFile))
                    .setApplicationName("barbcd")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequestInitializer getCredentials(String decryptedCredentials) {
        try {
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(new ByteArrayInputStream(decryptedCredentials.getBytes()));
            credentials = (ServiceAccountCredentials) credentials.createScoped(ADMIN_SCOPES);
            return new HttpCredentialsAdapter(credentials);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public enum RequestType {
        DELETE, UPDATE
    }
}
