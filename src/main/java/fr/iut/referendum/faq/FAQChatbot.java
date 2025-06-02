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
        
        // Essayer d'abord les réponses prédéfinies
        String predefinedResponse = getPredefinedResponse(query.toLowerCase());
        if (predefinedResponse != null) {
            return predefinedResponse;
        }
        
        // Utiliser DeepSeek en fallback
        try {
            String deepSeekResponse = deepSeekService.askDeepSeek(query, userRole);
            if (deepSeekResponse != null && !deepSeekResponse.trim().isEmpty()) {
                return deepSeekResponse;
            }
        } catch (Exception e) {
            System.err.println("Erreur DeepSeek: " + e.getMessage());
        }
        
        // En cas d'erreur, retourner une réponse basée sur le rôle
        return getFallbackResponse();
    }
    
    private String getPredefinedResponse(String query) {
        switch (userRole) {
            case "EMPLOYE":
                return getEmployeeResponse(query);
            case "SCRUTATEUR":
                return getScrutateurResponse(query);
            case "ADMIN":
                return getAdminResponse(query);
            default:
                return null;
        }
    }
    
    private String getEmployeeResponse(String query) {
        if (query.contains("comment voter") || query.contains("voter")) {
            return "Pour voter :\n" +
                   "1. Sélectionnez un référendum ouvert (statut 'En cours') dans la liste\n" +
                   "2. Choisissez votre réponse : 'Oui' ou 'Non'\n" +
                   "3. Cliquez sur le bouton 'Voter'\n\n" +
                   "⚠️ Attention : Vous ne pouvez voter qu'une seule fois par référendum !";
        }
        
        if (query.contains("je ne peux pas voter") || query.contains("impossible de voter") || query.contains("pourquoi je ne peux pas voter")) {
            return "Il y a plusieurs raisons possibles :\n\n" +
                   "📅 Le référendum n'est pas encore ouvert au vote (statut 'Fermé')\n" +
                   "⏰ Le référendum est terminé (date dépassée)\n" +
                   "✅ Vous avez déjà voté pour ce référendum\n\n" +
                   "Vérifiez le statut du référendum dans la liste des référendums disponibles.";
        }
        
        if (query.contains("résultat") || query.contains("voir les résultats")) {
            return "Pour voir les résultats :\n" +
                   "1. Sélectionnez un référendum TERMINÉ dans la liste\n" +
                   "2. Cliquez sur le bouton 'Résultat'\n\n" +
                   "⚠️ Les résultats ne sont visibles qu'après la fin du vote !";
        }
        
        if (query.contains("sécurisé") || query.contains("sécurité") || query.contains("anonyme")) {
            return "🔒 Votre vote est parfaitement sécurisé :\n\n" +
                   "• Chiffrement SSL/TLS pour les communications\n" +
                   "• Cryptographie homomorphe pour l'anonymat\n" +
                   "• Impossible de relier un vote à une personne\n" +
                   "• Vote définitif et non modifiable\n" +
                   "• Stockage sécurisé en base de données";
        }
        
        return null;
    }
    
    private String getScrutateurResponse(String query) {
        if (query.contains("fichier") && (query.contains("créer") || query.contains("sécurisation"))) {
            return "Pour créer un fichier de sécurisation :\n\n" +
                   "1. Entrez un mot de passe de 16 caractères exactement\n" +
                   "2. Cliquez sur 'Créer Clé Référendum'\n" +
                   "3. Choisissez l'emplacement de sauvegarde\n\n" +
                   "⚠️ Gardez précieusement ce fichier et le mot de passe !";
        }
        
        if (query.contains("clé") && query.contains("envoyer")) {
            return "Pour envoyer votre clé publique :\n\n" +
                   "1. Créez d'abord un fichier de sécurisation\n" +
                   "2. Sélectionnez le référendum dans la liste\n" +
                   "3. Cliquez sur 'Envoyé Clé Référendum'\n\n" +
                   "La clé ne peut être envoyée qu'une seule fois par référendum.";
        }
        
        if (query.contains("résultat") || query.contains("calculer")) {
            return "Pour obtenir les résultats :\n\n" +
                   "1. Attendez que le référendum soit TERMINÉ\n" +
                   "2. Chargez votre fichier de sécurisation\n" +
                   "3. Sélectionnez le référendum\n" +
                   "4. Cliquez sur 'Résultat Référendum'\n\n" +
                   "Vous pourrez alors voir les résultats déchiffrés.";
        }
        
        if (query.contains("rôle") || query.contains("scrutateur")) {
            return "En tant que scrutateur, vous êtes responsable de :\n\n" +
                   "🔑 Gérer les clés de chiffrement\n" +
                   "📁 Créer des fichiers de sécurisation\n" +
                   "📤 Envoyer votre clé publique au système\n" +
                   "🔓 Déchiffrer les résultats après le vote\n\n" +
                   "Vous garantissez ainsi la sécurité et l'anonymat du vote.";
        }
        
        return null;
    }
    
    private String getAdminResponse(String query) {
        if (query.contains("créer") && query.contains("référendum")) {
            return "Pour créer un référendum :\n\n" +
                   "1. Saisissez le nom du référendum\n" +
                   "2. Sélectionnez la date de fin\n" +
                   "3. Définissez l'heure de fin (format HH:MM)\n" +
                   "4. Choisissez un scrutateur dans la liste\n" +
                   "5. Cliquez sur 'Créer un référendum'\n\n" +
                   "Tous les champs sont obligatoires !";
        }
        
        if (query.contains("gérer") && query.contains("utilisateur")) {
            return "Pour gérer les utilisateurs :\n\n" +
                   "👥 Gérer Client : Créer/supprimer des comptes employés\n" +
                   "🔍 Gérer Scrutateur : Créer/supprimer des comptes scrutateurs\n\n" +
                   "⚠️ L'administrateur ne peut pas être supprimé !";
        }
        
        if (query.contains("supprimer") && query.contains("référendum")) {
            return "Pour supprimer un référendum :\n\n" +
                   "1. Sélectionnez le référendum dans la liste\n" +
                   "2. Cliquez sur 'Supprimer un référendum'\n\n" +
                   "⚠️ Un référendum ne peut être supprimé que s'il est TERMINÉ !";
        }
        
        if (query.contains("voter") && query.contains("admin")) {
            return "Pour voter en tant qu'administrateur :\n\n" +
                   "1. Retournez à la section 'Employé' (bouton Retour)\n" +
                   "2. Suivez la procédure normale de vote\n\n" +
                   "L'interface d'administration ne permet pas de voter directement.";
        }
        
        return null;
    }
    
    private String getFallbackResponse() {
        switch (userRole) {
            case "EMPLOYE":
                return "Je peux vous aider avec :\n• Comment voter ?\n• Pourquoi je ne peux pas voter ?\n• Comment voir les résultats ?\n• Mon vote est-il sécurisé ?\n\nPour d'autres questions, contactez le support.";
            case "SCRUTATEUR":
                return "Je peux vous aider avec :\n• Comment créer un fichier de sécurisation ?\n• Comment envoyer ma clé publique ?\n• Comment obtenir les résultats ?\n• Quel est mon rôle ?\n\nPour d'autres questions, contactez le support.";
            case "ADMIN":
                return "Je peux vous aider avec :\n• Comment créer un référendum ?\n• Comment gérer les utilisateurs ?\n• Comment supprimer un référendum ?\n• Comment voter en tant qu'admin ?\n\nPour d'autres questions, contactez le support.";
            default:
                return "Désolé, je ne peux pas répondre pour le moment. Veuillez réessayer ou contacter le support.";
        }
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