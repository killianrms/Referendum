package fr.iut.referendum.util;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public interface FileChooserService {

    /**
     * Affiche une boîte de dialogue d'enregistrement de fichier et retourne le fichier sélectionné.
     * @param ownerWindow La fenêtre propriétaire de la boîte de dialogue.
     * @param title Le titre de la boîte de dialogue.
     * @param initialFileName Le nom de fichier initial suggéré.
     * @param extensionFilters Les filtres d'extension de fichier.
     * @return Le fichier sélectionné par l'utilisateur, ou null si l'opération est annulée.
     */

    File showSaveDialog(Stage ownerWindow, String title, String initialFileName, FileChooser.ExtensionFilter... extensionFilters);
}