package fr.iut.referendum.Crypto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CryptoPerformanceTest {
    
    private BigInteger[] publicKey;
    private BigInteger secretKey;
    private BigInteger p, g, h;
    
    @BeforeEach
    public void setUp() {
        // Génération d'une clé pour les tests
        BigInteger[] keys = Crypto.genkey();
        p = keys[0];
        g = keys[1];
        h = keys[2];
        secretKey = keys[3];
        publicKey = new BigInteger[]{p, g, h};
    }
    
    @Test
    @DisplayName("Test de performance: Baby-step Giant-step vs Brute Force")
    public void testPerformanceComparison() {
        System.out.println("\n=== Test de performance: Baby-step Giant-step vs Brute Force ===\n");
        
        // Test avec différentes tailles de groupes de votants
        int[] nbVotantsArray = {10, 50, 100, 500, 1000, 2000};
        
        for (int nbVotants : nbVotantsArray) {
            System.out.println("Test avec " + nbVotants + " votants maximum:");
            
            // Choisir un nombre de votes aléatoire
            int nbVotes = (int)(Math.random() * nbVotants);
            BigInteger vote = BigInteger.valueOf(nbVotes);
            
            // Chiffrer le vote
            BigInteger[] ciphertext = Crypto.encrypt(vote, publicKey);
            
            // Test avec Brute Force
            long startBruteForce = System.nanoTime();
            BigInteger resultBruteForce = Crypto.decryptBruteForce(ciphertext, publicKey, secretKey, nbVotants);
            long timeBruteForce = System.nanoTime() - startBruteForce;
            
            // Test avec Baby-step Giant-step
            long startBSGS = System.nanoTime();
            BigInteger resultBSGS = Crypto.decrypt(ciphertext, publicKey, secretKey, nbVotants);
            long timeBSGS = System.nanoTime() - startBSGS;
            
            // Vérifier que les deux méthodes donnent le même résultat
            assertEquals(resultBruteForce, resultBSGS, 
                "Les deux méthodes doivent donner le même résultat");
            assertEquals(vote, resultBSGS, 
                "Le déchiffrement doit retrouver le vote original");
            
            // Afficher les résultats
            System.out.printf("  Vote original: %d\n", nbVotes);
            System.out.printf("  Brute Force: %.3f ms\n", timeBruteForce / 1_000_000.0);
            System.out.printf("  Baby-step Giant-step: %.3f ms\n", timeBSGS / 1_000_000.0);
            System.out.printf("  Amélioration: %.1fx plus rapide\n\n", 
                (double)timeBruteForce / timeBSGS);
        }
    }
    
    @Test
    @DisplayName("Test de performance avec agrégation de votes")
    public void testPerformanceWithAggregation() {
        System.out.println("\n=== Test de performance avec agrégation ===\n");
        
        int nbVotants = 1000;
        int nbVotesIndividuels = 100;
        
        // Créer plusieurs votes
        List<BigInteger[]> votes = new ArrayList<>();
        int totalVotes = 0;
        
        System.out.println("Création de " + nbVotesIndividuels + " votes individuels...");
        for (int i = 0; i < nbVotesIndividuels; i++) {
            int vote = Math.random() < 0.7 ? 1 : 0; // 70% de votes "pour"
            totalVotes += vote;
            votes.add(Crypto.encrypt(BigInteger.valueOf(vote), publicKey));
        }
        
        // Agréger tous les votes
        System.out.println("Agrégation des votes...");
        BigInteger[] aggregatedVote = votes.get(0);
        for (int i = 1; i < votes.size(); i++) {
            aggregatedVote = Crypto.agrege(aggregatedVote, votes.get(i), publicKey);
        }
        
        System.out.println("Déchiffrement du résultat agrégé:");
        System.out.println("Total de votes attendu: " + totalVotes);
        
        // Test avec Brute Force
        long startBruteForce = System.nanoTime();
        BigInteger resultBruteForce = Crypto.decryptBruteForce(aggregatedVote, publicKey, secretKey, nbVotants);
        long timeBruteForce = System.nanoTime() - startBruteForce;
        
        // Test avec Baby-step Giant-step
        long startBSGS = System.nanoTime();
        BigInteger resultBSGS = Crypto.decrypt(aggregatedVote, publicKey, secretKey, nbVotants);
        long timeBSGS = System.nanoTime() - startBSGS;
        
        // Vérifications
        assertEquals(BigInteger.valueOf(totalVotes), resultBruteForce);
        assertEquals(BigInteger.valueOf(totalVotes), resultBSGS);
        
        System.out.printf("Résultat déchiffré: %d\n", resultBSGS.intValue());
        System.out.printf("Temps Brute Force: %.3f ms\n", timeBruteForce / 1_000_000.0);
        System.out.printf("Temps Baby-step Giant-step: %.3f ms\n", timeBSGS / 1_000_000.0);
        System.out.printf("Amélioration: %.1fx plus rapide\n", 
            (double)timeBruteForce / timeBSGS);
    }
    
    @Test
    @DisplayName("Test de complexité algorithmique")
    public void testAlgorithmicComplexity() {
        System.out.println("\n=== Test de complexité algorithmique ===\n");
        System.out.println("Brute Force: O(n) où n est le nombre maximum de votants");
        System.out.println("Baby-step Giant-step: O(√n) en temps et en espace\n");
        
        // Démonstration avec des valeurs croissantes
        int[] sizes = {100, 400, 900, 1600, 2500};
        
        for (int size : sizes) {
            BigInteger vote = BigInteger.valueOf(size - 1); // Pire cas
            BigInteger[] ciphertext = Crypto.encrypt(vote, publicKey);
            
            long startBSGS = System.nanoTime();
            BigInteger result = Crypto.decrypt(ciphertext, publicKey, secretKey, size);
            long timeBSGS = System.nanoTime() - startBSGS;
            
            assertEquals(vote, result);
            
            System.out.printf("n=%d: temps=%.3f ms, √n=%.0f\n", 
                size, timeBSGS / 1_000_000.0, Math.sqrt(size));
        }
    }
}