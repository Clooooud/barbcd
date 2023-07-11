package io.github.clooooud.barbcd.data.api;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
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
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.Library;
import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.model.document.*;
import io.github.clooooud.barbcd.util.AESUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class GSheetApi {

    private static final String SPREADSHEET_NAME = "BarBCD Data";
    public static final String PRIVATE_CREDENTIAL_PATH_NAME = "pr_credentials.enc";

    private static final List<String> ADMIN_SCOPES = Arrays.asList(SheetsScopes.DRIVE, SheetsScopes.SPREADSHEETS);

    private final PublicCredentials credentials;

    private Drive driveService;
    private Sheets sheetsService;
    private Sheets userSheetsService;

    public GSheetApi(PublicCredentials credentials) {
        this.credentials = credentials;
    }

    public void init() {
        initUserSheetsService();
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

        for (File file : driveService.files().list().execute().getFiles()) {
            if (file.getId() == null) {
                continue;
            }
            driveService.files().delete(file.getId()).execute();
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

            // Default Magazine Categorie
            library.addDocument(Categorie.MAGAZINE);
        }

        for (SaveableType type : SaveableType.values()) {
            if (firstSetup) {
                writeHeader(
                        type.getSheetName(),
                        type.getHeaders()
                );
            }

            for (Saveable saveable : library.getDocuments(type)) {
                if (saveable.needsUpdate(library) && !firstSetup) {
                    continue;
                }

                writeLine(
                        saveable.getSaveableType().getSheetName(),
                        saveable.getId(),
                        saveable.getValues()
                );
            }
        }
    }

    public void load(Library library) throws IOException {
        Spreadsheet spreadsheet = getSpreadsheet();

        for (SaveableType type : SaveableType.getOrderedTypes()) {
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
                    List<String> values = rowData.getValues().stream().map(CellData::getFormattedValue).toList();
                    generateAndStoreComponent(library, values, type);
                }
            }
        }
    }

    private void generateAndStoreComponent(Library library, List<String> values, SaveableType type) {
        final int id = Integer.parseInt(values.get(0));
        switch (type) {
            case CATEGORIE -> {
                Categorie categorie = new Categorie(
                        id,
                        values.get(1)
                );
                library.addDocument(categorie);
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
                        (Categorie) library.getDocumentById(SaveableType.CATEGORIE, Integer.parseInt(values.get(5))),
                        Integer.parseInt(values.get(6)),
                        Integer.parseInt(values.get(7))
                );
                library.addDocument(oeuvre);
            }
            case USER -> {
                User user = new User(
                        id,
                        values.get(1),
                        values.get(2)
                );
                library.addDocument(user);
            }
            case BORROWING -> {
                User user = (User) library.getDocumentById(SaveableType.USER, Integer.parseInt(values.get(1)));
                boolean isMagazine = Boolean.parseBoolean(values.get(2));
                SaveableType documentType = isMagazine ? SaveableType.MAGAZINE : SaveableType.OEUVRE;
                ViewableDocument document = (ViewableDocument) library.getDocumentById(documentType, Integer.parseInt(values.get(3)));

                Borrowing borrowing = new Borrowing(
                        id,
                        user,
                        document,
                        Boolean.parseBoolean(values.get(4))
                );
                library.addDocument(borrowing);
            }
        }
    }

    private void clearLine(String sheetName, int lineId) {
        if (sheetsService == null) {
            throw new IllegalArgumentException();
        }

        try {
            sheetsService.spreadsheets()
                    .values()
                    .clear(credentials.getSpreadsheetId(), sheetName + "!" + (lineId+1), new ClearValuesRequest())
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeHeader(String sheetName, List<Object> values) {
        writeLine(sheetName, 0, values);
    }

    private void writeLine(String sheetName, int lineId, List<Object> values) {
        if (sheetsService == null) {
            throw new IllegalArgumentException();
        }

        ValueRange valueRange = new ValueRange();
        valueRange.setValues(Collections.singletonList(values));

        try {
            sheetsService.spreadsheets()
                    .values()
                    .update(credentials.getSpreadsheetId(), sheetName + "!A" + (lineId+1), valueRange)
                    .setValueInputOption("RAW")
                    .execute();
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

        List<File> fileList = driveService.files().list()
                .setQ("name contains '" + SPREADSHEET_NAME + "' and mimeType='application/vnd.google-apps.spreadsheet'")
                .setOrderBy("modifiedTime desc")
                .setSpaces("drive")
                .execute().getFiles();

        if (fileList.size() < 1) {
            return null;
        }

        return fileList.get(0);
    }

    private Spreadsheet initializeSpreadsheet() throws IOException {
        // Initialize de la spreadsheet
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
        String fileId = getFile().getId();
        driveService.permissions().create(fileId, new Permission().setRole("reader").setType("anyone")).execute();

        return spreadsheet;
    }

    private void initSheetsService(String decryptedFile) {
        if (decryptedFile == null) {
            throw new IllegalArgumentException();
        }

        try {
            this.sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), getCredentials(decryptedFile, ADMIN_SCOPES))
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
            this.driveService = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), getCredentials(decryptedFile, ADMIN_SCOPES))
                    .setApplicationName("barbcd")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequestInitializer getCredentials(String decryptedCredentials, List<String> scopes) {
        try {
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(new ByteArrayInputStream(decryptedCredentials.getBytes()));
            credentials = (ServiceAccountCredentials) credentials.createScoped(scopes);
            return new HttpCredentialsAdapter(credentials);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public record DataRequest(RequestType type, Saveable saveable) {

        public int id() {
            return saveable.getId();
        }
    }

    public enum RequestType {
        UPDATE((gSheetApi, saveable) -> gSheetApi.writeLine(saveable.getSaveableType().getSheetName(), saveable.getId(), saveable.getValues())),
        DELETE((gSheetApi, saveable) -> gSheetApi.clearLine(saveable.getSaveableType().getSheetName(), saveable.getId()));

        private final BiConsumer<GSheetApi, Saveable> consumer;

        RequestType(BiConsumer<GSheetApi, Saveable> consumer) {
            this.consumer = consumer;
        }

        public void consume(GSheetApi api, Saveable saveable) {
            this.consumer.accept(api, saveable);
        }
    }
}
