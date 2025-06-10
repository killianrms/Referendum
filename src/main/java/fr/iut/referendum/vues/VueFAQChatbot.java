package fr.iut.referendum.vues;

import fr.iut.referendum.faq.FAQChatbot;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VueFAQChatbot extends BorderPane {
    
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private VBox chatContainer;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private FlowPane suggestionsPane;
    @FXML
    private Label roleLabel;
    
    private FAQChatbot chatbot;
    private String userRole;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    public VueFAQChatbot(String userRole, String userName) {
        this.userRole = userRole;
        this.chatbot = new FAQChatbot(userRole);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/faqChatbot.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        initialize(userName);
    }
    
    private void initialize(String userName) {
        roleLabel.setText("FAQ - " + userName + " (" + getRoleName() + ")");
        
        // Configuration du chat
        chatContainer.setSpacing(10);
        chatContainer.setPadding(new Insets(10));
        
        // Message de bienvenue
        addBotMessage("Bonjour ! Je suis votre assistant FAQ pour le système de vote par référendum. Comment puis-je vous aider ?");
        
        // Afficher les suggestions
        loadSuggestions();
        
        // Bindings
        sendButton.setOnAction(e -> sendMessage());
        messageField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
        
        // Auto-scroll
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            chatScrollPane.setVvalue(1.0);
        });
    }
    
    private String getRoleName() {
        switch (userRole) {
            case "EMPLOYE":
                return "Employé/Votant";
            case "SCRUTATEUR":
                return "Scrutateur";
            case "ADMIN":
                return "Administrateur";
            default:
                return "Utilisateur";
        }
    }
    
    private void loadSuggestions() {
        suggestionsPane.getChildren().clear();
        
        for (String suggestion : chatbot.getSuggestions()) {
            Button suggestionBtn = new Button(suggestion);
            suggestionBtn.getStyleClass().add("suggestion-button");
            suggestionBtn.setStyle("-fx-background-color: #d000ff; -fx-text-fill: white ;-fx-background-radius: 5; -fx-padding: 8 20; -fx-font-weight: bold; -fx-cursor: hand;");
            suggestionBtn.setOnAction(e -> {
                messageField.setText(suggestion);
                sendMessage();
            });
            suggestionsPane.getChildren().add(suggestionBtn);
        }
    }
    
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) return;
        
        // Ajouter le message de l'utilisateur
        addUserMessage(message);
        
        // Obtenir et afficher la réponse du chatbot
        String response = chatbot.processQuery(message);
        addBotMessage(response);
        
        // Vider le champ de message
        messageField.clear();
        messageField.requestFocus();
    }
    
    private void addUserMessage(String message) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_RIGHT);
        messageBox.setPadding(new Insets(5));
        
        VBox bubble = new VBox();
        bubble.setMaxWidth(400);
        bubble.setStyle("-fx-background-color: #007bff; -fx-background-radius: 10; -fx-padding: 10;");
        
        Text messageText = new Text(message);
        messageText.setFill(Color.WHITE);
        messageText.setWrappingWidth(380);
        
        Text timeText = new Text(LocalDateTime.now().format(timeFormatter));
        timeText.setFill(Color.rgb(200, 200, 200));
        timeText.setStyle("-fx-font-size: 10;");
        
        bubble.getChildren().addAll(messageText, timeText);
        messageBox.getChildren().add(bubble);
        
        chatContainer.getChildren().add(messageBox);
    }
    
    private void addBotMessage(String message) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageBox.setPadding(new Insets(5));
        
        VBox bubble = new VBox();
        bubble.setMaxWidth(400);
        bubble.setStyle("-fx-background-color: #f1f1f1; -fx-background-radius: 10; -fx-padding: 10;");
        
        Text messageText = new Text(message);
        messageText.setWrappingWidth(380);
        
        Text timeText = new Text(LocalDateTime.now().format(timeFormatter));
        timeText.setFill(Color.GRAY);
        timeText.setStyle("-fx-font-size: 10;");
        
        bubble.getChildren().addAll(messageText, timeText);
        messageBox.getChildren().add(bubble);
        
        chatContainer.getChildren().add(messageBox);
    }
}