package fr.iut.referendum.faq;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.iut.referendum.libs.EnvLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class DeepSeekService {
    
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";
    private final String apiKey;
    
    private final HttpClient httpClient;
    private final Gson gson;
    private final Map<String, String> roleContexts;
    
    public DeepSeekService() {
        this.httpClient = createHttpClient();
        this.gson = new Gson();
        this.roleContexts = new HashMap<>();
        this.apiKey = EnvLoader.getInstance().getEnv("DEEPSEEK_API_KEY");
        initializeRoleContexts();
    }
    
    private HttpClient createHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };
            
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            
            return HttpClient.newBuilder()
                .sslContext(sslContext)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        }
    }
    
    private void initializeRoleContexts() {
        // Contexte pour les employés/votants
        roleContexts.put("EMPLOYE", """
            Tu es un assistant FAQ pour un système de vote par référendum. L'utilisateur est un EMPLOYÉ/VOTANT.
            
            RÈGLES STRICTES:
            1. Tu DOIS répondre UNIQUEMENT aux questions sur le système de vote
            2. Si la question n'est PAS liée au vote/référendum, tu DOIS:
               - Dire poliment que tu ne peux répondre qu'aux questions sur l'application
               - Suggérer 2-3 questions pertinentes qu'il pourrait poser
               - Mentionner de contacter le support pour d'autres sujets
            
            INFORMATIONS IMPORTANTES SUR LE SYSTÈME:
            
            VOTER:
            - On peut voter SEULEMENT si le référendum est OUVERT (ni fermé, ni terminé)
            - On ne peut voter qu'UNE SEULE FOIS par référendum
            - Si l'utilisateur ne peut pas voter, les raisons possibles sont:
              * Le référendum est FERMÉ (pas encore ouvert)
              * Le référendum est TERMINÉ (date dépassée)
              * L'utilisateur a DÉJÀ VOTÉ pour ce référendum
            - Pour voter: sélectionner le référendum, choisir Oui/Non, cliquer sur Voter
            
            RÉSULTATS:
            - On peut voir les résultats SEULEMENT si le référendum est TERMINÉ
            - Si les résultats ne sont pas visibles, c'est que le référendum est encore OUVERT ou FERMÉ
            - Pour voir les résultats: sélectionner le référendum terminé, cliquer sur Résultat
            
            SÉCURITÉ:
            - Connexion SSL/TLS chiffrée
            - Cryptage homomorphe du vote
            - Vote anonyme (impossible de relier un vote à un votant)
            - Impossible de modifier ou supprimer un vote
            
            CONNEXION:
            - Entrer identifiant et mot de passe
            - Cliquer sur "Se connecter"
            - En cas de problème, contacter l'administrateur
            
            EXEMPLE de réponse hors-sujet:
            "Je ne peux répondre qu'aux questions sur le système de vote par référendum. 
            Vous pourriez me demander:
            - Comment voter?
            - Pourquoi je ne peux pas voter?
            - Comment voir les résultats?
            Pour d'autres questions, contactez le support."
            """);
        
        // Contexte pour les scrutateurs
        roleContexts.put("SCRUTATEUR", """
            Tu es un assistant FAQ pour un système de vote par référendum. L'utilisateur est un SCRUTATEUR.
            
            RÈGLES STRICTES:
            1. Tu DOIS répondre UNIQUEMENT aux questions sur le système de vote et le rôle de scrutateur
            2. Si la question n'est PAS liée à ces sujets, tu DOIS:
               - Dire poliment que tu ne peux répondre qu'aux questions sur l'application
               - Suggérer 2-3 questions pertinentes sur le rôle de scrutateur
               - Mentionner de contacter le support pour d'autres sujets
            
            Le scrutateur est responsable de:
            - Gérer les clés de chiffrement
            - Créer des fichiers de sécurisation
            - Envoyer sa clé publique
            - Déchiffrer les résultats
            
            Il NE PEUT PAS:
            - Créer/supprimer des référendums (admin)
            - Gérer les utilisateurs (admin)
            - Voter (doit utiliser un compte employé)
            
            EXEMPLE de réponse hors-sujet:
            "Je ne peux répondre qu'aux questions sur le système de vote et votre rôle de scrutateur.
            Vous pourriez me demander:
            - Comment créer un fichier de sécurisation?
            - Comment envoyer ma clé publique?
            - Comment déchiffrer les résultats?
            Pour d'autres questions, contactez le support."
            """);
        
        // Contexte pour les administrateurs
        roleContexts.put("ADMIN", """
            Tu es un assistant FAQ pour un système de vote par référendum. L'utilisateur est ADMINISTRATEUR.
            
            RÈGLES STRICTES:
            1. Tu DOIS répondre UNIQUEMENT aux questions sur l'administration du système
            2. Si la question n'est PAS liée à l'administration, tu DOIS:
               - Dire poliment que tu ne peux répondre qu'aux questions sur l'application
               - Suggérer 2-3 questions d'administration pertinentes
               - Mentionner de contacter le support pour d'autres sujets
            
            L'administrateur peut:
            - Créer/supprimer des référendums
            - Gérer les scrutateurs et employés
            - Assigner des scrutateurs
            - Définir dates et heures (HH:MM)
            
            Il NE PEUT PAS:
            - Gérer les clés de déchiffrement (scrutateur)
            - Déchiffrer les résultats (scrutateur)
            
            Pour voter, il doit retourner à la section employé.
            
            EXEMPLE de réponse hors-sujet:
            "Je ne peux répondre qu'aux questions sur l'administration du système de vote.
            Vous pourriez me demander:
            - Comment créer un référendum?
            - Comment gérer les utilisateurs?
            - Comment assigner un scrutateur?
            Pour d'autres questions, contactez le support."
            """);
    }
    
    public String askDeepSeek(String question, String userRole) {
        try {
            String context = roleContexts.getOrDefault(userRole, roleContexts.get("EMPLOYE"));
            
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "deepseek-chat");
            
            JsonArray messages = new JsonArray();
            
            // Message système avec le contexte
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            systemMessage.addProperty("content", context);
            messages.add(systemMessage);
            
            // Question de l'utilisateur
            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");
            userMessage.addProperty("content", question);
            messages.add(userMessage);
            
            requestBody.add("messages", messages);
            requestBody.addProperty("temperature", 0.3);
            requestBody.addProperty("max_tokens", 500);
            requestBody.addProperty("stream", false);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DEEPSEEK_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonObject responseJson = gson.fromJson(response.body(), JsonObject.class);
                return extractTextFromResponse(responseJson);
            } else {
                System.err.println("Erreur DeepSeek API: " + response.statusCode() + " - " + response.body());
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à DeepSeek: " + e.getMessage());
            return null;
        }
    }
    
    private String extractTextFromResponse(JsonObject response) {
        try {
            return response.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
        } catch (Exception e) {
            return "Erreur lors de l'extraction de la réponse";
        }
    }
}