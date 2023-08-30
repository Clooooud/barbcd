package io.github.clooooud.barbcd.gui.scenes.admin.student;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.gui.element.ButtonComponent;
import io.github.clooooud.barbcd.gui.element.FieldComponent;
import io.github.clooooud.barbcd.gui.element.FormBox;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NewClassScene extends RootAdminScene {

    private FormBox formBox;
    private FieldComponent nameField;

    private final List<String[]> studentInfoList = new ArrayList<>();

    public NewClassScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        nameField = new FieldComponent("Nom de la classe");

        formBox = new FormBox.Builder("Nouvelle classe")
                .addComponent("Nom de la classe", nameField)
                .addComponent("Elèves", new ButtonComponent(
                        "Importer les élèves",
                        "Importer",
                        event -> importFile(),
                        "Pour importer les élèves, il faut un fichier CSV qui contiendrait les noms et prénoms des élèves, le nom doit être dans la première colonne et le prénom dans la deuxième. Si les autres colonnes sont remplis, elles ne seront pas prises en compte."
                ))
                .addButton("Sauvegarder", event -> this.consumeForm())
                .addButton("Annuler", event -> this.getApp().getStageWrapper().setContent(new ClassesScene(this.getApp())))
                .build();

        vBox.getChildren().add(formBox);
    }

    private void importFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer une classe");
        File file = fileChooser.showOpenDialog(this.getApp().getStageWrapper().getStage());

        if (file == null) {
            return;
        }

        readFile(file);
    }

    private void readFile(File file) {
        readFile(file, "windows-1252");
    }

    private void readFile(File file, String charset) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
            studentInfoList.clear();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(line.contains(";") ? ";" : ",");

                if (data.length < 2) {
                    continue;
                }

                String[] info = new String[]{data[0], data[1]}; // nom, prénom

                if (info[0].isBlank() || info[1].isBlank()) {
                    continue;
                }

                studentInfoList.add(info);
            }
            GuiUtil.wrapAlert(new Alert(Alert.AlertType.INFORMATION, "Fichier importé avec succès !")).showAndWait();
        } catch (IOException e) {
            GuiUtil.alertError("Une erreur est survenue lors de la lecture du fichier !");
        }
    }

    private void consumeForm() {
        String className = nameField.getField().getText();

        if (className.isBlank()) {
            GuiUtil.alertError("Veuillez remplir tous les champs !");
            return;
        }

        if (this.studentInfoList.isEmpty()) {
            GuiUtil.alertError("Veuillez importer un fichier CSV avec les élèves de la classe !");
            return;
        }

        if (this.getLibrary().getDocuments(SaveableType.CLASS).stream()
                .map(document -> (Class) document)
                .anyMatch(aClass -> aClass.getClassName().equalsIgnoreCase(className))) {
            GuiUtil.alertError("Une classe avec ce nom existe déjà !");
            return;
        }

        Class newClass = this.getLibrary().createClass(className);
        this.studentInfoList.forEach(info -> this.getLibrary().createStudent(info[1], info[0], newClass));
        SaveRunnable.create(this.getApp()).run();
        this.getApp().getStageWrapper().setContent(new ClassScene(this.getApp(), newClass));
    }
}
