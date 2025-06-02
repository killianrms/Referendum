package fr.iut.referendum.faq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FAQChatbot {
    
    private Map<String, List<FAQEntry>> faqDatabase;
    private String userRole;
    
    public FAQChatbot(String userRole) {
        this.userRole = userRole;
        this.faqDatabase = new HashMap<>();
        initializeFAQDatabase();
    }
    
    private void initializeFAQDatabase() {
        // FAQ pour les employés/votants
        List<FAQEntry> employeeFAQ = new ArrayList<>();
        employeeFAQ.add(new FAQEntry(
            new String[]{"comment voter", "voter", "vote"},
            "Pour voter :\n1. Sélectionnez un référendum dans la liste\n2. Choisissez 'Oui' ou 'Non'\n3. Cliquez sur 'Voter'\n\nNote: Vous ne pouvez voter qu'une seule fois par référendum."
        ));
        employeeFAQ.add(new FAQEntry(
            new String[]{"résultat", "résultats", "voir résultat"},
            "Pour voir les résultats :\n1. Sélectionnez un référendum terminé\n2. Cliquez sur 'Résultat'\n\nNote: Les résultats ne sont disponibles qu'après la clôture du référendum."
        ));
        employeeFAQ.add(new FAQEntry(
            new String[]{"connexion", "connecter", "login"},
            "Pour vous connecter :\n1. Entrez votre identifiant\n2. Entrez votre mot de passe\n3. Cliquez sur 'Se connecter'\n\nEn cas de problème, contactez votre administrateur."
        ));
        employeeFAQ.add(new FAQEntry(
            new String[]{"référendum ouvert", "référendums disponibles", "liste référendum"},
            "Pour voir les référendums disponibles :\n- Cliquez sur 'Actualiser' pour rafraîchir la liste\n- Les référendums ouverts sont affichés avec leur date de fin\n- Vous ne verrez que les référendums auxquels vous n'avez pas encore voté"
        ));
        employeeFAQ.add(new FAQEntry(
            new String[]{"déjà voté", "voter deux fois", "double vote"},
            "Vous ne pouvez voter qu'une seule fois par référendum. Si vous avez déjà voté, le système affichera 'Vous avez déjà voté pour ce référendum'."
        ));
        employeeFAQ.add(new FAQEntry(
            new String[]{"sécurité", "vote sécurisé", "cryptage"},
            "Votre vote est sécurisé par :\n- Connexion SSL/TLS chiffrée\n- Cryptage homomorphe du vote\n- Anonymisation du vote\n- Impossibilité de relier un vote à un votant"
        ));
        employeeFAQ.add(new FAQEntry(
            new String[]{"admin", "administrateur", "section admin"},
            "Si vous êtes administrateur, un bouton 'Section administrateur' apparaîtra. Cliquez dessus pour accéder aux fonctions d'administration."
        ));
        faqDatabase.put("EMPLOYE", employeeFAQ);
        
        // FAQ pour les scrutateurs
        List<FAQEntry> scrutateurFAQ = new ArrayList<>();
        scrutateurFAQ.add(new FAQEntry(
            new String[]{"clé publique", "envoyer clé", "clé cryptage"},
            "Pour envoyer votre clé publique :\n1. Créez ou chargez un fichier de sécurisation\n2. Sélectionnez un référendum\n3. Cliquez sur 'Envoyer clé publique'\n\nNote: Une clé ne peut être envoyée qu'une fois par référendum."
        ));
        scrutateurFAQ.add(new FAQEntry(
            new String[]{"fichier sécurisation", "créer fichier", "nouveau fichier"},
            "Pour créer un fichier de sécurisation :\n1. Entrez un mot de passe de 16 caractères exactement\n2. Cliquez sur 'Nouveau fichier'\n3. Choisissez l'emplacement de sauvegarde\n\nGardez ce fichier et ce mot de passe en sécurité!"
        ));
        scrutateurFAQ.add(new FAQEntry(
            new String[]{"charger fichier", "ouvrir fichier", "fichier existant"},
            "Pour charger un fichier existant :\n1. Cliquez sur 'Charger fichier'\n2. Sélectionnez votre fichier .txt\n3. Entrez le mot de passe (16 caractères)\n\nLe fichier sera décrypté et les clés chargées."
        ));
        scrutateurFAQ.add(new FAQEntry(
            new String[]{"calculer résultat", "déchiffrer", "résultat référendum"},
            "Pour calculer le résultat :\n1. Assurez-vous d'avoir chargé vos clés\n2. Sélectionnez un référendum terminé\n3. Cliquez sur 'Calculer résultat'\n\nLe système déchiffrera les votes agrégés."
        ));
        scrutateurFAQ.add(new FAQEntry(
            new String[]{"mot de passe fichier", "16 caractères", "longueur mot de passe"},
            "Le mot de passe doit faire exactement 16 caractères pour des raisons de sécurité AES. Utilisez un mot de passe fort avec lettres, chiffres et symboles."
        ));
        scrutateurFAQ.add(new FAQEntry(
            new String[]{"clé déjà envoyée", "erreur clé publique"},
            "Si vous recevez 'Clé publique déjà enregistrée', cela signifie qu'un scrutateur a déjà envoyé sa clé pour ce référendum ou que le référendum est terminé."
        ));
        scrutateurFAQ.add(new FAQEntry(
            new String[]{"rôle scrutateur", "responsabilité", "mission"},
            "En tant que scrutateur, vous êtes responsable de :\n- Générer et sécuriser les clés de déchiffrement\n- Envoyer la clé publique avant le vote\n- Déchiffrer les résultats après la clôture\n- Garantir l'intégrité du processus"
        ));
        faqDatabase.put("SCRUTATEUR", scrutateurFAQ);
        
        // FAQ pour les administrateurs
        List<FAQEntry> adminFAQ = new ArrayList<>();
        adminFAQ.add(new FAQEntry(
            new String[]{"créer référendum", "nouveau référendum", "ajouter référendum"},
            "Pour créer un référendum :\n1. Entrez le nom du référendum\n2. Sélectionnez la date de fin\n3. Entrez l'heure de fin (format HH:MM)\n4. Sélectionnez un scrutateur\n5. Cliquez sur 'Créer référendum'"
        ));
        adminFAQ.add(new FAQEntry(
            new String[]{"supprimer référendum", "effacer référendum", "retirer référendum"},
            "Pour supprimer un référendum :\n1. Sélectionnez le référendum dans la liste\n2. Cliquez sur 'Supprimer référendum'\n\nAttention: Cette action est irréversible!"
        ));
        adminFAQ.add(new FAQEntry(
            new String[]{"gérer scrutateur", "scrutateurs", "crud scrutateur"},
            "Pour gérer les scrutateurs :\n1. Cliquez sur 'Gérer scrutateurs'\n2. Vous pouvez :\n   - Ajouter un nouveau scrutateur\n   - Modifier un scrutateur existant\n   - Supprimer un scrutateur"
        ));
        adminFAQ.add(new FAQEntry(
            new String[]{"gérer client", "employés", "crud client"},
            "Pour gérer les employés :\n1. Cliquez sur 'Gérer clients'\n2. Vous pouvez :\n   - Ajouter un nouvel employé\n   - Modifier un employé existant\n   - Supprimer un employé\n   - Définir les droits admin"
        ));
        adminFAQ.add(new FAQEntry(
            new String[]{"heure format", "format heure", "HH:MM"},
            "L'heure doit être au format HH:MM (24h) :\n- HH : heures de 00 à 23\n- MM : minutes de 00 à 59\nExemple : 14:30 pour 14h30"
        ));
        adminFAQ.add(new FAQEntry(
            new String[]{"scrutateur obligatoire", "assigner scrutateur"},
            "Chaque référendum doit avoir un scrutateur assigné. Le scrutateur sera responsable de fournir la clé publique et de déchiffrer les résultats."
        ));
        adminFAQ.add(new FAQEntry(
            new String[]{"retour votant", "section votant", "quitter admin"},
            "Pour retourner à la section votant, cliquez sur 'Retour'. Vous pourrez toujours revenir à la section admin plus tard."
        ));
        faqDatabase.put("ADMIN", adminFAQ);
        
        // Questions communes à tous
        List<FAQEntry> commonFAQ = new ArrayList<>();
        commonFAQ.add(new FAQEntry(
            new String[]{"cgu", "conditions utilisation", "conditions générales"},
            "Pour lire les Conditions Générales d'Utilisation, cliquez sur le bouton 'CGU' disponible dans l'interface."
        ));
        commonFAQ.add(new FAQEntry(
            new String[]{"mentions légales", "ml", "informations légales"},
            "Pour lire les Mentions Légales, cliquez sur le bouton 'ML' disponible dans l'interface."
        ));
        commonFAQ.add(new FAQEntry(
            new String[]{"actualiser", "rafraîchir", "reload"},
            "Pour actualiser les données, cliquez sur le bouton 'Actualiser'. Cela rechargera la liste des référendums ou des utilisateurs."
        ));
        commonFAQ.add(new FAQEntry(
            new String[]{"erreur serveur", "connexion perdue", "erreur liaison"},
            "En cas d'erreur de connexion :\n1. Vérifiez votre connexion internet\n2. Assurez-vous que le serveur est en ligne\n3. Redémarrez l'application si nécessaire\n4. Contactez l'administrateur système"
        ));
        commonFAQ.add(new FAQEntry(
            new String[]{"sécurité système", "protection données", "confidentialité"},
            "Le système garantit :\n- Connexions SSL/TLS chiffrées\n- Mots de passe hashés avec BCrypt\n- Votes cryptés de manière homomorphe\n- Anonymat des votes\n- Traçabilité des actions admin"
        ));
        faqDatabase.put("COMMON", commonFAQ);
    }
    
    public String processQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "Bonjour ! Je suis l'assistant FAQ. Posez-moi une question sur l'utilisation du système de vote.";
        }
        
        String lowerQuery = query.toLowerCase().trim();
        
        // Recherche dans les FAQ spécifiques au rôle
        String response = searchInFAQ(faqDatabase.get(userRole), lowerQuery);
        if (response != null) {
            return response;
        }
        
        // Recherche dans les FAQ communes
        response = searchInFAQ(faqDatabase.get("COMMON"), lowerQuery);
        if (response != null) {
            return response;
        }
        
        // Réponse par défaut avec suggestions
        return getDefaultResponse();
    }
    
    private String searchInFAQ(List<FAQEntry> faqList, String query) {
        if (faqList == null) return null;
        
        for (FAQEntry entry : faqList) {
            for (String keyword : entry.keywords) {
                if (query.contains(keyword)) {
                    return entry.response;
                }
            }
        }
        return null;
    }
    
    private String getDefaultResponse() {
        StringBuilder response = new StringBuilder();
        response.append("Je n'ai pas trouvé de réponse exacte à votre question. ");
        response.append("Voici quelques sujets sur lesquels je peux vous aider :\n\n");
        
        switch (userRole) {
            case "EMPLOYE":
                response.append("- Comment voter ?\n");
                response.append("- Comment voir les résultats ?\n");
                response.append("- Problèmes de connexion\n");
                response.append("- Sécurité du vote\n");
                break;
            case "SCRUTATEUR":
                response.append("- Gestion des clés publiques\n");
                response.append("- Fichiers de sécurisation\n");
                response.append("- Calcul des résultats\n");
                response.append("- Rôle du scrutateur\n");
                break;
            case "ADMIN":
                response.append("- Créer un référendum\n");
                response.append("- Gérer les utilisateurs\n");
                response.append("- Gérer les scrutateurs\n");
                response.append("- Supprimer un référendum\n");
                break;
        }
        
        response.append("\nEssayez de reformuler votre question ou contactez le support.");
        return response.toString();
    }
    
    public List<String> getSuggestions() {
        List<String> suggestions = new ArrayList<>();
        
        switch (userRole) {
            case "EMPLOYE":
                suggestions.add("Comment voter ?");
                suggestions.add("Voir les résultats");
                suggestions.add("J'ai déjà voté ?");
                suggestions.add("Mon vote est-il sécurisé ?");
                break;
            case "SCRUTATEUR":
                suggestions.add("Créer un fichier de sécurisation");
                suggestions.add("Envoyer ma clé publique");
                suggestions.add("Calculer les résultats");
                suggestions.add("Mot de passe du fichier");
                break;
            case "ADMIN":
                suggestions.add("Créer un référendum");
                suggestions.add("Gérer les scrutateurs");
                suggestions.add("Supprimer un référendum");
                suggestions.add("Format de l'heure");
                break;
        }
        
        suggestions.add("Sécurité du système");
        suggestions.add("Erreur de connexion");
        return suggestions;
    }
    
    private static class FAQEntry {
        String[] keywords;
        String response;
        
        FAQEntry(String[] keywords, String response) {
            this.keywords = keywords;
            this.response = response;
        }
    }
}