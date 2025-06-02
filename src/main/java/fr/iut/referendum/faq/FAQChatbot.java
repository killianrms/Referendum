package fr.iut.referendum.faq;

import java.util.ArrayList;
import java.util.List;

public class FAQChatbot {
    
    private String userRole;
    private DeepSeekService deepSeekService;
    
    public FAQChatbot(String userRole) {
        this.userRole = userRole;
        this.deepSeekService = new DeepSeekService();
    }
    
    public String processQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "Bonjour ! Je suis l'assistant FAQ. Posez-moi une question sur l'utilisation du système de vote.";
        }
        
        // Utiliser directement DeepSeek pour toutes les questions
        try {
            String deepSeekResponse = deepSeekService.askDeepSeek(query, userRole);
            if (deepSeekResponse != null && !deepSeekResponse.isEmpty()) {
                return deepSeekResponse;
            }
        } catch (Exception e) {
            System.err.println("Erreur DeepSeek: " + e.getMessage());
        }
        
        // En cas d'erreur uniquement
        return "Désolé, je ne peux pas répondre pour le moment. Veuillez réessayer ou contacter le support.";
    }
    
    public List<String> getSuggestions() {
        List<String> suggestions = new ArrayList<>();
        
        switch (userRole) {
            case "EMPLOYE":
                suggestions.add("Comment voter ?");
                suggestions.add("Pourquoi je ne peux pas voter ?");
                suggestions.add("Comment voir les résultats ?");
                suggestions.add("Mon vote est-il sécurisé ?");
                break;
            case "SCRUTATEUR":
                suggestions.add("Comment créer un fichier de sécurisation ?");
                suggestions.add("Comment envoyer ma clé publique ?");
                suggestions.add("Comment calculer les résultats ?");
                suggestions.add("Quel est mon rôle ?");
                break;
            case "ADMIN":
                suggestions.add("Comment créer un référendum ?");
                suggestions.add("Comment gérer les utilisateurs ?");
                suggestions.add("Comment supprimer un référendum ?");
                suggestions.add("Comment voter en tant qu'admin ?");
                break;
        }
        
        return suggestions;
    }
}