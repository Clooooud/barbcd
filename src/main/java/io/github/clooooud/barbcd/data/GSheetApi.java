package io.github.clooooud.barbcd.data;

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
import io.github.clooooud.barbcd.model.Library;
import io.github.clooooud.barbcd.model.document.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GSheetApi {

    private static final String SPREADSHEET_NAME = "BarBCD Data";
    private static final String PRIVATE_CREDENTIAL_PATH_NAME = "pr_credentials.enc";

    private static final List<String> ADMIN_SCOPES = Arrays.asList(SheetsScopes.DRIVE, SheetsScopes.SPREADSHEETS);

    private final PublicCredentials publicCredentials;

    public GSheetApi(PublicCredentials publicCredentials) {
        this.publicCredentials = publicCredentials;
    }

    public void reset(String adminPassword) throws IOException {
        AESUtil aesUtil = new AESUtil(adminPassword);
        Drive driveService = getDriveService(aesUtil.decrypt(PRIVATE_CREDENTIAL_PATH_NAME));

        for (File file : driveService.files().list().execute().getFiles()) {
            if (file.getId() == null) {
                continue;
            }
            driveService.files().delete(file.getId()).execute();
        }

        this.publicCredentials.setSpreadsheetId("");
        this.publicCredentials.save();
    }

    public void save(Library library, String adminPassword) throws IOException {
        AESUtil aesUtil = new AESUtil(adminPassword);
        String decryptedFile = aesUtil.decrypt(PRIVATE_CREDENTIAL_PATH_NAME);

        Drive driveService = getDriveService(decryptedFile);
        Sheets sheetsService = getSheetsService(decryptedFile);

        boolean firstSetup = getFile(driveService) == null;

        if (firstSetup) {
            Spreadsheet spreadsheet = initializeSpreadsheet(driveService, sheetsService);
            this.publicCredentials.setSpreadsheetId(spreadsheet.getSpreadsheetId());
            this.publicCredentials.save();

            // Default Magazine Categorie
            library.addCategorie(Categorie.MAGAZINE);
        }

        for (Set<? extends Saveable> saveableList : List.of(
                library.getEditorList(),
                library.getMagazineList(),
                library.getMagazineSerieList(),
                library.getOeuvreList(),
                library.getCategorieList())
        ) {
            for (Saveable saveable : saveableList) {
                if (!saveable.needsSave() && !firstSetup) {
                    continue;
                }

                if (firstSetup) {
                    writeHeader(
                            sheetsService,
                            this.publicCredentials.getSpreadsheetId(),
                            saveable.getSheetName(),
                            saveable.getHeaders()
                    );
                }

                writeLine(
                        sheetsService,
                        this.publicCredentials.getSpreadsheetId(),
                        saveable.getSheetName(),
                        saveable.getId(),
                        saveable.getValues()
                );
            }
        }
    }

    public void load(Library library) throws IOException {
        Spreadsheet spreadsheet = getSpreadsheet();

        for (String sheetName : List.of("Categories", "Editors", "MagazineSeries", "Magazines", "Oeuvres")) {
            // Load the sheets in the right order (!!! important)
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
                    generateAndStoreComponent(library, values, sheetName);
                }
            }
        }
    }

    private void generateAndStoreComponent(Library library, List<String> values, String sheetName) {
        switch (sheetName) {
            case "Categories" -> {
                Categorie categorie = new Categorie(Integer.parseInt(values.get(0)), values.get(1));
                library.addCategorie(categorie);
            }
            case "Editors" -> {
                Editor editor = new Editor(Integer.parseInt(values.get(0)), values.get(1));
                library.addEditor(editor);
            }
            case "MagazineSeries" -> {
                MagazineSerie magazineSerie = new MagazineSerie(
                        Integer.parseInt(values.get(0)),
                        values.get(1),
                        values.get(2),
                        library.getEditor(Integer.parseInt(values.get(3)))
                );
                library.addMagazineSerie(magazineSerie);
            }
            case "Magazines" -> {
                Magazine magazine = new Magazine(Integer.parseInt(values.get(0)),
                        values.get(1),
                        Integer.parseInt(values.get(2)),
                        Integer.parseInt(values.get(3)),
                        Integer.parseInt(values.get(4)),
                        Integer.parseInt(values.get(5)),
                        library.getMagazineSerie(Integer.parseInt(values.get(6)))
                );
                library.addMagazine(magazine);
            }
            case "Oeuvres" -> {
                Oeuvre oeuvre = new Oeuvre(
                        Integer.parseInt(values.get(0)),
                        values.get(1),
                        values.get(2),
                        values.get(3),
                        library.getEditor(Integer.parseInt(values.get(4))),
                        library.getCategorie(Integer.parseInt(values.get(5))),
                        Integer.parseInt(values.get(6)),
                        Integer.parseInt(values.get(7))
                );
                library.addOeuvre(oeuvre);
            }
        }
    }

    private void writeHeader(Sheets sheetsService, String spreadsheetId, String sheetName, List<Object> values) throws IOException {
        writeLine(sheetsService, spreadsheetId, sheetName, 0, values);
    }

    private void writeLine(Sheets sheetsService, String spreadsheetId, String sheetName, int lineId, List<Object> values) throws IOException {
        ValueRange valueRange = new ValueRange();
        valueRange.setValues(Collections.singletonList(values));
        sheetsService.spreadsheets()
                .values()
                .update(spreadsheetId, sheetName + "!A" + (lineId+1), valueRange)
                .setValueInputOption("RAW")
                .execute();
    }

    private Spreadsheet getSpreadsheet() throws IOException {
        Sheets sheetsService = getUserSheetsService();
        Sheets.Spreadsheets.Get request = sheetsService.spreadsheets().get(this.publicCredentials.getSpreadsheetId())
                .setIncludeGridData(true);
        // Set the API key in the request
        HttpHeaders requestHeaders = request.getRequestHeaders();
        requestHeaders.set("x-goog-api-key", this.publicCredentials.getApiKey());

        return request.execute();
    }

    private File getFile(Drive driveService) throws IOException {
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

    private Spreadsheet initializeSpreadsheet(Drive driveService, Sheets sheetsService) throws IOException {
        // Initialize de la spreadsheet
        Spreadsheet spreadsheet = new Spreadsheet();
        spreadsheet.setProperties(new SpreadsheetProperties());
        spreadsheet.getProperties().setTitle(SPREADSHEET_NAME);

        // Creation of the sheets
        List<String> sheetNames = Arrays.asList("Oeuvres", "Editors", "Magazines", "MagazineSeries", "Categories");
        spreadsheet.setSheets(sheetNames
                .stream()
                .map(name -> new Sheet().setProperties(new SheetProperties().setTitle(name)))
                .collect(Collectors.toList())
        );
        spreadsheet = sheetsService.spreadsheets().create(spreadsheet).execute();

        // Reading permissions for everyone
        String fileId = getFile(driveService).getId();
        driveService.permissions().create(fileId, new Permission().setRole("reader").setType("anyone")).execute();

        return spreadsheet;
    }

    private Sheets getSheetsService(String decryptedFile) {
        try {
            return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), getCredentials(decryptedFile, ADMIN_SCOPES))
                    .setApplicationName("barbcd")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Sheets getUserSheetsService() {
        try {
            return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), null)
                    .setApplicationName("barbcd")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Drive getDriveService(String decryptedFile) {
        try {
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), getCredentials(decryptedFile, ADMIN_SCOPES))
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
}
