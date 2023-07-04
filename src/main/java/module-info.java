module io.github.clooooud.barbcd {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.google.gson;
    requires com.google.auth;
    requires com.google.api.client;
    requires google.api.services.sheets.v4.rev612;
    requires com.google.auth.oauth2;
    requires google.api.client;
    requires com.google.common;
    requires com.google.api.client.json.gson;
    requires google.api.services.drive.v3.rev197;

    requires com.dlsc.formsfx;

    opens io.github.clooooud.barbcd to javafx.fxml;
    exports io.github.clooooud.barbcd;
}