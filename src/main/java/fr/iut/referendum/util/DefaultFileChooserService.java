package fr.iut.referendum.util;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DefaultFileChooserService implements FileChooserService {

    @Override
    public File showSaveDialog(Stage ownerWindow, String title, String initialFileName, FileChooser.ExtensionFilter... extensionFilters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (initialFileName != null) {
            fileChooser.setInitialFileName(initialFileName);
        }
        if (extensionFilters != null) {
            fileChooser.getExtensionFilters().addAll(extensionFilters);
        }
        return fileChooser.showSaveDialog(ownerWindow);
    }
}