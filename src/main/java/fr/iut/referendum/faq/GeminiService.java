package fr.iut.referendum.faq;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.iut.referendum.libs.EnvLoader;

import java.io.IOException;
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

public class GeminiService {
    
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent";
    private final String apiKey;
    
    private final HttpClient httpClient;
    private final Gson gson;
    private final Map<String, String> roleContexts;
    
    public GeminiService() {
        this.httpClient = createHttpClient();
        this.gson = new Gson();
        this.roleContexts = new HashMap<>();
        this.apiKey = EnvLoader.getInstance().getEnv("GEMINI_API_KEY");
        initializeRoleContexts();
    }
    
    private HttpClient createHttpClient() {
        try {
            // Créer un TrustManager qui accepte tous les certificats
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
            
            // Installer le TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            
            return HttpClient.newBuilder()
                .sslContext(sslContext)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            // En cas d'erreur, utiliser le client par défaut
            return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        }
    }
    
    private void initializeRoleContexts() {
        // Contexte pour les employés/votants
        roleContexts.put("EMPLOYE", """
            Tu es un assistant FAQ pour un système de vote par référendum sécurisé.
            L'utilisateur qui te parle est un EMPLOYÉ/VOTANT.
            
            En tant qu'employé, il peut :
            - Voter pour des référendums (une seule fois par référendum)
            - Consulter les résultats des référendums terminés
            - Voir la liste des référendums disponibles
            - Se connecter avec son identifiant et mot de passe
            
            Il NE PEUT PAS :
            - Créer ou supprimer des référendums
            - Gérer d'autres utilisateurs
            - Accéder aux fonctions de scrutateur (clés de déchiffrement)
            - Effectuer des tâches administratives
            
            Réponds UNIQUEMENT aux questions liées au vote et à l'utilisation de base.
            Si on te demande des fonctions d'admin ou de scrutateur, explique poliment que c'est réservé aux administrateurs/scrutateurs.
            Sois concis et précis dans tes réponses.
            """);
        
        // Contexte pour les scrutateurs
        roleContexts.put("SCRUTATEUR", """
            Tu es un assistant FAQ pour un système de vote par référendum sécurisé.
            L'utilisateur qui te parle est un SCRUTATEUR.
            
            En tant que scrutateur, il est responsable de :
            - Générer et gérer les clés de chiffrement/déchiffrement
            - Créer des fichiers de sécurisation (mot de passe 16 caractères)
            - Envoyer sa clé publique pour un référendum
            - Déchiffrer les résultats après la clôture du vote
            - Garantir l'intégrité du processus de vote
            
            Il NE PEUT PAS :
            - Créer ou supprimer des référendums (réservé aux admins)
            - Gérer les utilisateurs (réservé aux admins)
            - Voter (il doit utiliser un compte employé pour cela)
            
            Réponds UNIQUEMENT aux questions liées au rôle de scrutateur et à la gestion des clés.
            Si on te demande comment voter ou des fonctions d'admin, redirige-le vers le bon rôle.
            Sois technique mais clair sur les aspects cryptographiques.
            """);
        
        // Contexte pour les administrateurs
        roleContexts.put("ADMIN", """
            Tu es un assistant FAQ pour un système de vote par référendum sécurisé.
            L'utilisateur qui te parle est dans la SECTION ADMINISTRATEUR.
            
            En tant qu'administrateur dans cette section, il peut :
            - Créer de nouveaux référendums
            - Supprimer des référendums
            - Gérer les scrutateurs (CRUD)
            - Gérer les employés/clients (CRUD)
            - Assigner des scrutateurs aux référendums
            - Définir les dates et heures de fin (format HH:MM)
            - Retourner à la section employé pour voter
            
            Il NE PEUT PAS :
            - Gérer les clés de déchiffrement (réservé aux scrutateurs)
            - Déchiffrer les résultats (réservé aux scrutateurs)
            
            Note: Pour voter, l'administrateur doit retourner à la section employé en cliquant sur "Retour".
            
            Réponds UNIQUEMENT aux questions d'administration.
            Si on te demande comment voter, explique qu'il faut retourner à la section employé.
            Si on te demande des fonctions de scrutateur, explique que c'est réservé aux scrutateurs.
            """);
    }
    
    public String askGemini(String question, String userRole) {
        try {
            String context = roleContexts.getOrDefault(userRole, roleContexts.get("EMPLOYE"));
            
            JsonObject requestBody = new JsonObject();
            JsonArray contents = new JsonArray();
            JsonObject content = new JsonObject();
            JsonArray parts = new JsonArray();
            JsonObject part = new JsonObject();
            
            String prompt = context + "\n\nQuestion de l'utilisateur : " + question + 
                          "\n\nRéponds de manière concise et précise, en français.";
            
            part.addProperty("text", prompt);
            parts.add(part);
            content.add("parts", parts);
            contents.add(content);
            requestBody.add("contents", contents);
            
            // Configuration de sécurité
            JsonObject safetySettings = new JsonObject();
            JsonArray safety = new JsonArray();
            JsonObject setting = new JsonObject();
            setting.addProperty("category", "HARM_CATEGORY_DANGEROUS_CONTENT");
            setting.addProperty("threshold", "BLOCK_NONE");
            safety.add(setting);
            requestBody.add("safetySettings", safety);
            
            // Configuration de génération
            JsonObject generationConfig = new JsonObject();
            generationConfig.addProperty("temperature", 0.7);
            generationConfig.addProperty("maxOutputTokens", 500);
            requestBody.add("generationConfig", generationConfig);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_API_URL + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonObject responseJson = gson.fromJson(response.body(), JsonObject.class);
                return extractTextFromResponse(responseJson);
            } else {
                System.err.println("Erreur Gemini API: " + response.statusCode() + " - " + response.body());
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à Gemini: " + e.getMessage());
            return null;
        }
    }
    
    private String extractTextFromResponse(JsonObject response) {
        try {
            return response.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
        } catch (Exception e) {
            return "Erreur lors de l'extraction de la réponse";
        }
    }
}