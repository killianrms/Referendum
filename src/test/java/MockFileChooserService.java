import java.io.File;

import fr.iut.referendum.util.FileChooserService;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MockFileChooserService implements FileChooserService {

    private final File fileToReturn;

    /**
     * Crée un MockFileChooserService qui retournera le fichier spécifié.
     * @param fileToReturn Le fichier que showSaveDialog doit simuler comme étant sélectionné.
     */

    public MockFileChooserService(File fileToReturn) {
        this.fileToReturn = fileToReturn;
    }

    @Override
    public File showSaveDialog(Stage ownerWindow, String title, String initialFileName, FileChooser.ExtensionFilter... extensionFilters) {
        return fileToReturn;
    }

    public File getFile() {
        return fileToReturn;
    }
}