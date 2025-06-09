import fr.iut.referendum.Client.MainClient;
import fr.iut.referendum.Scrutateur.MainScrutateur;
import fr.iut.referendum.Serveur.Serveur;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import oracle.jdbc.proxy.annotation.Methods;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import fr.iut.referendum.vues.VueScrutateur;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestAuto extends ApplicationTest {

    private static Thread serveurThread;
    private Stage primaryStageReference;

    private static MockFileChooserService mockFileChooserService;

    @BeforeAll
    static void setUp() throws InterruptedException {
        serveurThread = new Thread(() -> {
            try {
                Serveur.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serveurThread.start();
        Thread.sleep(1000);

        // Initialisation du fichier de test
        String projectPath = System.getProperty("user.dir");
        Path resourcePath = Paths.get(projectPath, "src", "test", "resources", "KeyTestAuto.txt");
        File testFile = resourcePath.toFile();

        mockFileChooserService = new MockFileChooserService(testFile);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStageReference = stage;
        MainClient clientApp = new MainClient();
        clientApp.start(stage);
    }

    public void connection(String login, String password) {
        TextField usernameField = lookup("#usernameField").queryAs(TextField.class);
        clickOn(usernameField).write(login);
        PasswordField passwordField = lookup("#passwordField").queryAs(PasswordField.class);
        clickOn(passwordField).write(password);
        Button connectButton = lookup("#buttonConnecter").queryAs(Button.class);
        clickOn(connectButton);
    }

    private void connectionAdmin() {
        connection("admin", "Admin123.");
        Button buttonAdmin = lookup("#buttonAdmin").queryAs(Button.class);
        clickOn(buttonAdmin);
    }

    private void createReferendum(String name, String Scrutateur, int plusMinutes) {
        sleep(500);

        ListView<String> listViewReferendum = lookup("#listViewReferendums").queryAs(ListView.class);
        Button buttonReload = lookup("#buttonReload").queryAs(Button.class);
        Button buttonSupprimerReferendum = lookup("#buttonSuprReferendum").queryAs(Button.class);

        for (int i = 0; i < listViewReferendum.getItems().size(); i++) {
            if (listViewReferendum.getItems().get(i).contains(name)) {
                listViewReferendum.getSelectionModel().select(i);
                clickOn(buttonSupprimerReferendum);
                i--;
            }
        }

        ListView<String> listViewScrutateur = lookup("#listViewScrutateur").queryAs(ListView.class);
        TextField nomField = lookup("#nomReferendum").queryAs(TextField.class);
        DatePicker dateDebutField = lookup("#datePickerFin").queryAs(DatePicker.class);
        TextField heureDebutField = lookup("#heureFin").queryAs(TextField.class);
        Button buttonCreerReferendum = lookup("#buttonCreerReferendum").queryAs(Button.class);

        int nbReferendumInitial = listViewReferendum.getItems().size();
        nomField.setText(name);
        dateDebutField.setValue(LocalDate.now());
        heureDebutField.setText(LocalTime.now().plusMinutes(plusMinutes).format(DateTimeFormatter.ofPattern("HH:mm")));

        for (String s : listViewScrutateur.getItems()) {
            if (s.contains(Scrutateur)) {
                listViewScrutateur.getSelectionModel().select(s);
                break;
            }
        }

        clickOn(buttonCreerReferendum);
        clickOn(buttonReload);
        sleep(500);

        assertEquals(nbReferendumInitial + 1, listViewReferendum.getItems().size(), "Le référendum n'a pas été créé");

        Platform.runLater(() -> {
            Stage currentStage = (Stage) buttonReload.getScene().getWindow();
            currentStage.close();
        });
    }

    private void createKeyAndSendFile(String login, String pwd, String pwdKey, String referendumName) {
        MainScrutateur scrutateurApp = new MainScrutateur();
        Platform.runLater(() -> {
            primaryStageReference.close();
            scrutateurApp.start(primaryStageReference);
        });

        sleep(500);

        connection(login, pwd);

        VueScrutateur vueScrutateurInstance = scrutateurApp.getVueScrutateur();
        if (vueScrutateurInstance != null) {
            vueScrutateurInstance.setFileChooserService(mockFileChooserService);
        } else {
            fail("Impossible de récupérer l'instance de VueScrutateur pour injecter le mock.");
        }

        PasswordField mdpField = lookup("#mdpfichier").queryAs(PasswordField.class);
        Button buttonNewFile = lookup("#buttonNewFile").queryAs(Button.class);

        mdpField.setText(pwdKey);
        clickOn(buttonNewFile);

        ListView<String> listViewReferendumVueScrutateur = lookup("#listViewReferendums").queryAs(ListView.class);
        for (String s : listViewReferendumVueScrutateur.getItems()) {
            if (s.contains(referendumName)) {
                if (s.contains("Terminé")) {
                    fail("Le référendum est déjà fermé avant le vote." + s);
                }
                listViewReferendumVueScrutateur.getSelectionModel().select(s);
                break;
            }
        }

        Button buttonEnvoyer = lookup("#buttonEnvoyer").queryAs(Button.class);
        clickOn(buttonEnvoyer);

        Button buttonReload2 = lookup("#buttonReload").queryAs(Button.class);
        clickOn(buttonReload2);

        sleep(500);
    }

    private void voteReferendum(String referendumName, Boolean[] voteAFaire, int voteBourrageUrne) {
        for (int i = 0; i < voteAFaire.length; i++) {
            MainClient clientApp = new MainClient();
            Platform.runLater(() -> {
                primaryStageReference.close();
                clientApp.start(primaryStageReference);
            });
            sleep(500);

            connection("ClientTestAuto" + (i + 1), "ClientTestAuto123.");

            sleep(500);

            Button buttonReload3 = lookup("#buttonReload").queryAs(Button.class);
            clickOn(buttonReload3);
            sleep(500);

            ListView<String> listViewReferendumClient = lookup("#listViewReferendums").queryAs(ListView.class);

            for (String s : listViewReferendumClient.getItems()) {
                if (s.contains(referendumName)) {
                    listViewReferendumClient.getSelectionModel().select(s);
                    break;
                }
            }

            if (voteAFaire[i]) {
                RadioButton radioOui = lookup("#radioOui").queryAs(RadioButton.class);
                clickOn(radioOui);
            } else {
                RadioButton radioNon = lookup("#radioNon").queryAs(RadioButton.class);
                clickOn(radioNon);
            }

            Button buttonVoter = lookup("#buttonSelect").queryAs(Button.class);
            clickOn(buttonVoter);

            sleep(500);
        }

        if (voteBourrageUrne != 0) {
            MainClient clientApp = new MainClient();
            clientApp.setVoteBourrageUrne(voteBourrageUrne);

            Platform.runLater(() -> {
                primaryStageReference.close();
                clientApp.start(primaryStageReference);
            });
            sleep(500);

            connection("ClientTestAuto5", "ClientTestAuto123.");

            sleep(500);

            Button buttonReload3 = lookup("#buttonReload").queryAs(Button.class);
            clickOn(buttonReload3);
            sleep(500);

            ListView<String> listViewReferendumClient = lookup("#listViewReferendums").queryAs(ListView.class);

            for (String s : listViewReferendumClient.getItems()) {
                if (s.contains(referendumName)) {
                    listViewReferendumClient.getSelectionModel().select(s);
                    break;
                }
            }

            RadioButton radioOui = lookup("#radioOui").queryAs(RadioButton.class);
            clickOn(radioOui);

            sleep(500);
        }
    }

    private String resultatReferendum(String login, String pwd, String pwdKey, String referendumName) {
        MainScrutateur scrutateurApp = new MainScrutateur();
        Platform.runLater(() -> {
            primaryStageReference.close();
            scrutateurApp.start(primaryStageReference);
        });

        sleep(500);

        connection(login, pwd);

        VueScrutateur vueScrutateurInstance = scrutateurApp.getVueScrutateur();
        if (vueScrutateurInstance != null) {
            vueScrutateurInstance.setFileChooserService(mockFileChooserService);
        } else {
            fail("Impossible de récupérer l'instance de VueScrutateur pour injecter le mock.");
        }

        PasswordField mdpField = lookup("#mdpfichier").queryAs(PasswordField.class);
        Button buttonLoadFile = lookup("#buttonLoadFile").queryAs(Button.class);

        mdpField.setText(pwdKey);
        clickOn(buttonLoadFile);

        sleep(500);

        Button buttonReload = lookup("#buttonReload").queryAs(Button.class);
        clickOn(buttonReload);

        ListView<String> listViewReferendumVueScrutateur = lookup("#listViewReferendums").queryAs(ListView.class);
        clickOn(buttonReload);

        int x = -1;
        int nbReferendum = listViewReferendumVueScrutateur.getItems().size();
        for (int i = 0; i < nbReferendum; i++) {
            if (listViewReferendumVueScrutateur.getItems().get(i).contains(referendumName)) {
                x = i;
                break;
            }
        }

        assertNotEquals(-1, x, "Le référendum n'a pas été trouvé dans la liste des référendums.");
        boolean referendumFound = false;

        while (!referendumFound) {
            if (!listViewReferendumVueScrutateur.getItems().get(x).contains("Terminé")) {
                sleep(5000);
                clickOn(buttonReload);
            } else {
                referendumFound = true;
                listViewReferendumVueScrutateur.getSelectionModel().select(x);
            }
        }

        Button buttonResultat = lookup("#buttonResultat").queryAs(Button.class);
        clickOn(buttonResultat);

        sleep(1000);

        Label labelStatue = lookup("#statue").queryAs(Label.class);
        if (labelStatue.getText().contains("Oui")) {
            return "Oui";
        } else if (labelStatue.getText().contains("Non")) {
            return "Non";
        } else if (labelStatue.getText().contains("Egalité")) {
            return "Egalité";
        } else {
            return "Erreur";
        }
    }

    private void suprimerReferendum(String referendumName) {
        MainClient clientApp = new MainClient();
        Platform.runLater(() -> {
            primaryStageReference.close();
            clientApp.start(primaryStageReference);
        });
        sleep(500);

        connectionAdmin();

        int nbReferendumInitial = lookup("#listViewReferendums").queryAs(ListView.class).getItems().size();

        ListView<String> listViewReferendum = lookup("#listViewReferendums").queryAs(ListView.class);
        for (String s : listViewReferendum.getItems()) {
            if (s.contains(referendumName)) {
                listViewReferendum.getSelectionModel().select(s);
                break;
            }
        }

        Button buttonSupprimerReferendum = lookup("#buttonSuprReferendum").queryAs(Button.class);
        clickOn(buttonSupprimerReferendum);

        Button buttonReload = lookup("#buttonReload").queryAs(Button.class);
        clickOn(buttonReload);

        assertEquals(nbReferendumInitial - 1, listViewReferendum.getItems().size(), "Le référendum n'a pas été supprimé");
    }

    @Test
    void TestServeur() {
        assertTrue(serveurThread.isAlive(), "Le thread du serveur ne semble pas être en cours d'exécution.");
    }

    @Test
    void TestClientConnection() {
        connection("jean", "Jean123.");
        assertEquals("Choix Referendums", primaryStageReference.getTitle(), "Le titre de la fenêtre n'est pas 'Choix Referendums' après la connexion.");
    }

    @Test
    void TestClientCGU() {
        connection("admin", "Admin123.");
        Button buttonCGU = lookup("#buttonCGU").queryAs(Button.class);
        clickOn(buttonCGU);
    }

    @Test
    void TestClientML() {
        connection("admin", "Admin123.");
        Button buttonML = lookup("#buttonML").queryAs(Button.class);
        clickOn(buttonML);
    }

    @ParameterizedTest(name = "{index} - Vote {0} => Résultat attendu : {1}")
    @MethodSource("voteReferendumTestCases")
    void TestReferendum(
            Boolean[] voteAFaire,
            String resultatAttendu
    ) {
        String referendumName = "TestClientVote";
        String loginScrutateur = "TestAuto";
        String pwdScrutateur = "TestAuto123.";
        String pwdKey = "azertyuiopqsdfgh";
        int plusMinutes = 2;

        connectionAdmin();

        createReferendum(referendumName, loginScrutateur, plusMinutes);

        createKeyAndSendFile(loginScrutateur, pwdScrutateur, pwdKey, referendumName);

        voteReferendum(referendumName, voteAFaire, 0);

        String resultat = resultatReferendum(loginScrutateur, pwdScrutateur, pwdKey, referendumName);

        System.out.println("Résultat du référendum : " + resultat);

        suprimerReferendum(referendumName);

        assertEquals(resultatAttendu, resultat, "Le résultat du référendum n'est pas celui attendu");
    }

    private static Stream<Arguments> voteReferendumTestCases() {
        return Stream.of(
                Arguments.of(new Boolean[]{false, false, true, true, true}, "Oui"),
                Arguments.of(new Boolean[]{true, true, false, false, false}, "Non"),
                Arguments.of(new Boolean[]{true, false, true, false}, "Egalité"),
                Arguments.of(new Boolean[]{false, false, false, false, false}, "Non"),
                Arguments.of(new Boolean[]{true, true, true, true, true}, "Oui")
        );
    }

    @ParameterizedTest(name = "{index} - Vote {0} => Résultat attendu : {1}")
    @MethodSource("voteReferendumBourrageUrneTestCases")
    void TestReferendumBourrageUrne(
            Boolean[] voteAFaire,
            int voteBourrageUrne,
            String resultatAttendu
    ) {
        String referendumName = "TestClientVote";
        String loginScrutateur = "TestAuto";
        String pwdScrutateur = "TestAuto123.";
        String pwdKey = "azertyuiopqsdfgh";
        int plusMinutes = 2;

        connectionAdmin();

        createReferendum(referendumName, loginScrutateur, plusMinutes);

        createKeyAndSendFile(loginScrutateur, pwdScrutateur, pwdKey, referendumName);

        voteReferendum(referendumName, voteAFaire, voteBourrageUrne);

        String resultat = resultatReferendum(loginScrutateur, pwdScrutateur, pwdKey, referendumName);

        System.out.println("Résultat du référendum : " + resultat);

        suprimerReferendum(referendumName);

        assertEquals(resultatAttendu, resultat, "Le résultat du référendum n'est pas celui attendu");
    }

    private static Stream<Arguments> voteReferendumBourrageUrneTestCases() {
        return Stream.of(
                Arguments.of(new Boolean[]{true, false}, 10, "Erreur"),
                Arguments.of(new Boolean[]{true, false}, -10, "Erreur"),
                Arguments.of(new Boolean[]{false, false}, 3, "Erreur"),
                Arguments.of(new Boolean[]{true, true}, -2, "Erreur")
        );
    }

    @Test
    void TestScrutateur() {
        MainScrutateur scrutateurApp = new MainScrutateur();

        TextField usernameField = lookup("#usernameField").queryAs(TextField.class);

        Platform.runLater(() -> {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();
            primaryStageReference.close();
            scrutateurApp.start(primaryStageReference);
        });

        sleep(500);

        connection("TestAuto", "TestAuto123.");

        sleep(500);

        VueScrutateur vueScrutateurInstance = scrutateurApp.getVueScrutateur();
        if (vueScrutateurInstance != null) {
            vueScrutateurInstance.setFileChooserService(mockFileChooserService);
        } else {
            fail("Impossible de récupérer l'instance de VueScrutateur pour injecter le mock.");
        }

        PasswordField mdpField = lookup("#mdpfichier").queryAs(PasswordField.class);
        Button buttonNewFile = lookup("#buttonNewFile").queryAs(Button.class);
        Button buttonLoadFile = lookup("#buttonLoadFile").queryAs(Button.class);

        mdpField.setText("TropCourt");
        clickOn(buttonNewFile);

        mdpField.setText("azertyuiopqsdfgh");
        clickOn(buttonNewFile);

        clickOn(buttonLoadFile);

        assertTrue(mockFileChooserService.getFile().exists(), "Le fichier n'a pas été créé ou n'a pas été chargé.");
        assertTrue(mockFileChooserService.getFile().length() > 0, "Le fichier est vide après écriture.");
    }

    @Test
    void TestAdminConnection() {
        connectionAdmin();
        assertEquals("Choix Referendums", primaryStageReference.getTitle(), "Le titre de la fenêtre n'est pas 'Choix Referendums' après la connexion.");
    }

    @Test
    void TestAdminReferendum1() throws InterruptedException {
        connectionAdmin();
        ListView<String> listViewReferendum = lookup("#listViewReferendums").queryAs(ListView.class);
        ListView<String> listViewScrutateur = lookup("#listViewScrutateur").queryAs(ListView.class);
        TextField nomField = lookup("#nomReferendum").queryAs(TextField.class);
        DatePicker dateDebutField = lookup("#datePickerFin").queryAs(DatePicker.class);
        TextField heureDebutField = lookup("#heureFin").queryAs(TextField.class);
        Button buttonCreerReferendum = lookup("#buttonCreerReferendum").queryAs(Button.class);
        Button buttonReload = lookup("#buttonReload").queryAs(Button.class);

        int nbR = listViewReferendum.getItems().size();
        clickOn(buttonCreerReferendum);
        assertEquals(nbR, listViewReferendum.getItems().size(), "Le nombre de referendum n'est pas le bon après la création du referendum");
        nomField.setText("TestAuto");
        clickOn(buttonCreerReferendum);
        assertEquals(nbR, listViewReferendum.getItems().size(), "Le nombre de referendum n'est pas le bon après la création du referendum");
        dateDebutField.setValue(LocalDate.now());
        clickOn(buttonCreerReferendum);
        assertEquals(nbR, listViewReferendum.getItems().size(), "Le nombre de referendum n'est pas le bon après la création du referendum");
        heureDebutField.setText(LocalTime.now().getHour() + ":" + (LocalTime.now().getMinute() + 1));
        clickOn(buttonCreerReferendum);
        assertEquals(nbR, listViewReferendum.getItems().size(), "Le nombre de referendum n'est pas le bon après la création du referendum");
        listViewScrutateur.getSelectionModel().select("TestAuto");
        clickOn(buttonCreerReferendum);
        clickOn(buttonReload);
        assertEquals(nbR + 1, listViewReferendum.getItems().size(), "Le nombre de referendum n'est pas le bon après la création du referendum");

        String referendum = null;
        for (String s : listViewReferendum.getItems()) {
            if (s.contains("TestAuto")) {
                referendum = s;
            }
        }
        assertNotNull(referendum, "Le referendum n'a pas été créé avec le bon nom ou n'est pas dans la liste");

        assertTrue(referendum.contains("TestAuto"), "Le nom du référendum n'est pas le bon");
        assertTrue(referendum.contains(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))), "La date de début du referendum n'est pas la bonne");
        LocalTime now = LocalTime.now();
        LocalTime oneMinuteLater = now.plusMinutes(1);
        assertTrue(referendum.contains(oneMinuteLater.format(DateTimeFormatter.ofPattern("HH:mm"))), "L'heure de début du referendum n'est pas la bonne");
        assertTrue(referendum.contains("Fermé"), "Le referendum est en cours alors qu'il n'est pas encore commencé");
        assertTrue(referendum.contains("1 minute(s)"), "Le referendum n'est pas en cours");

        Button buttonSupprimerReferendum = lookup("#buttonSuprReferendum").queryAs(Button.class);
        clickOn(buttonSupprimerReferendum);
        assertEquals(nbR + 1, listViewReferendum.getItems().size(), "Le referendum ne doit pas être supprimé car le vote n'est pas terminé");
        boolean bool = true;
        while (bool) {
            for (String s : listViewReferendum.getItems()) {
                if (s.contains("TestAuto")) {
                    if (s.contains("Terminé")) {
                        bool = false;
                        break;
                    }
                }
            }
            clickOn(buttonReload);
            sleep(1000);
        }
        listViewReferendum.getSelectionModel().select(referendum);
        clickOn(buttonSupprimerReferendum);
        assertEquals(nbR, listViewReferendum.getItems().size(), "Le nombre de referendum n'est pas le bon après la suppression du referendum");
    }

    @Test
    void TestAdminClient() {
        connectionAdmin();
        Button buttonGererClient = lookup("#buttonGererClient").queryAs(Button.class);
        clickOn(buttonGererClient);
        ListView<String> listViewClient = lookup("#listViewClient").queryAs(ListView.class);
        Button buttonCreerClient = lookup("#buttonCreerClient").queryAs(Button.class);
        Button buttonSupprimerClient = lookup("#buttonSuprClient").queryAs(Button.class);
        Button buttonReload = lookup("#buttonReload").queryAs(Button.class);
        TextField usernameField = lookup("#usernameField").queryAs(TextField.class);
        TextField passwordField = lookup("#passwordField").queryAs(TextField.class);

        int u = listViewClient.getItems().size();
        clickOn(buttonCreerClient);
        assertEquals(u, listViewClient.getItems().size(), "Le nombre de client n'est pas le bon après la création du client");
        usernameField.setText("newUserTestAuto");
        passwordField.setText("NewPassTestAuto123!");
        clickOn(buttonCreerClient);
        assertEquals(u + 1, listViewClient.getItems().size(), "Le nombre de client n'est pas le bon après la création du client");
        clickOn(buttonReload);
        assertTrue(listViewClient.getItems().contains("newUserTestAuto"), "Le client n'est pas créé avec le bon nom");
        listViewClient.getSelectionModel().select("newUserTestAuto");
        clickOn(buttonSupprimerClient);
        assertEquals(u, listViewClient.getItems().size(), "Le nombre de client n'est pas le bon après la suppression du client");
    }

    @Test
    void TestAdminClient2() {
        connectionAdmin();
        Button buttonGererClient = lookup("#buttonGererClient").queryAs(Button.class);
        clickOn(buttonGererClient);
        ListView<String> listViewClient = lookup("#listViewClient").queryAs(ListView.class);
        Button buttonSupprimerClient = lookup("#buttonSuprClient").queryAs(Button.class);

        int u = listViewClient.getItems().size();
        listViewClient.getSelectionModel().select("admin - admin");
        clickOn(buttonSupprimerClient);
        assertEquals(u, listViewClient.getItems().size(), "Le nombre de client n'est pas le bon après la suppression du client");
    }

    @Test
    void TestAdminScrutateur() {
        connectionAdmin();
        Button buttonGererScrutateur = lookup("#buttonGererScrutateur").queryAs(Button.class);
        clickOn(buttonGererScrutateur);
        ListView<String> listViewScrutateur = lookup("#listViewScrutateur").queryAs(ListView.class);
        Button buttonCreerScrutateur = lookup("#buttonCreerScrutateur").queryAs(Button.class);
        Button buttonSupprimerScrutateur = lookup("#buttonSuprScrutateur").queryAs(Button.class);
        Button buttonReload = lookup("#buttonReload").queryAs(Button.class);
        TextField usernameField = lookup("#usernameField").queryAs(TextField.class);
        TextField passwordField = lookup("#passwordField").queryAs(TextField.class);

        int u = listViewScrutateur.getItems().size();
        clickOn(buttonCreerScrutateur);
        assertEquals(u, listViewScrutateur.getItems().size(), "Le nombre de scrutateur n'est pas le bon après la création du scrutateur");
        usernameField.setText("newUserTestAuto");
        passwordField.setText("NewPassTestAuto123!");
        clickOn(buttonCreerScrutateur);
        assertEquals(u + 1, listViewScrutateur.getItems().size(), "Le nombre de scrutateur n'est pas le bon après la création du scrutateur");
        clickOn(buttonReload);
        assertTrue(listViewScrutateur.getItems().contains("newUserTestAuto"), "Le scrutateur n'est pas créé avec le bon nom");
        listViewScrutateur.getSelectionModel().select("newUserTestAuto");
        clickOn(buttonSupprimerScrutateur);
        assertEquals(u, listViewScrutateur.getItems().size(), "Le nombre de scrutateur n'est pas le bon après la suppression du scrutateur");
    }
}