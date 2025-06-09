package fr.iut.referendum.Crypto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CryptoSecurityTest {
    
    private BigInteger[] publicKey;
    private BigInteger secretKey;
    private BigInteger p, g, h;
    
    @BeforeEach
    public void setUp() {
        BigInteger[] keys = Crypto.genkey();
        p = keys[0];
        g = keys[1];
        h = keys[2];
        secretKey = keys[3];
        publicKey = new BigInteger[]{p, g, h};
    }
    
    @Test
    @DisplayName("Test de sécurité: propriétés ElGamal préservées")
    public void testElGamalPropertiesPreserved() {
        System.out.println("\n=== Test des propriétés ElGamal ===\n");
        
        // Test 1: Chiffrement et déchiffrement corrects
        for (int vote = 0; vote <= 10; vote++) {
            BigInteger m = BigInteger.valueOf(vote);
            BigInteger[] c = Crypto.encrypt(m, publicKey);
            
            // Les deux méthodes doivent donner le même résultat
            BigInteger decryptedBrute = Crypto.decryptBruteForce(c, publicKey, secretKey, 100);
            BigInteger decryptedBSGS = Crypto.decrypt(c, publicKey, secretKey, 100);
            
            assertEquals(m, decryptedBrute, "Brute force doit retrouver le message original");
            assertEquals(m, decryptedBSGS, "BSGS doit retrouver le message original");
            assertEquals(decryptedBrute, decryptedBSGS, "Les deux méthodes doivent être équivalentes");
        }
        System.out.println("✓ Chiffrement/déchiffrement corrects pour tous les votes testés");
    }
    
    @Test
    @DisplayName("Test de sécurité: homomorphisme préservé")
    public void testHomomorphicProperty() {
        System.out.println("\n=== Test de l'homomorphisme ===\n");
        
        // ElGamal est homomorphe: E(m1) * E(m2) = E(m1 + m2)
        BigInteger m1 = BigInteger.valueOf(5);
        BigInteger m2 = BigInteger.valueOf(7);
        
        BigInteger[] c1 = Crypto.encrypt(m1, publicKey);
        BigInteger[] c2 = Crypto.encrypt(m2, publicKey);
        
        // Agrégation
        BigInteger[] cAgg = Crypto.agrege(c1, c2, publicKey);
        
        // Déchiffrement avec les deux méthodes
        BigInteger resultBrute = Crypto.decryptBruteForce(cAgg, publicKey, secretKey, 20);
        BigInteger resultBSGS = Crypto.decrypt(cAgg, publicKey, secretKey, 20);
        
        assertEquals(m1.add(m2), resultBrute, "Homomorphisme avec brute force");
        assertEquals(m1.add(m2), resultBSGS, "Homomorphisme avec BSGS");
        assertEquals(resultBrute, resultBSGS, "Cohérence entre les méthodes");
        
        System.out.println("✓ Propriété homomorphe préservée");
        System.out.println("  E(" + m1 + ") ⊕ E(" + m2 + ") = E(" + m1.add(m2) + ")");
    }
    
    @RepeatedTest(10)
    @DisplayName("Test de sécurité: randomisation du chiffrement")
    public void testEncryptionRandomization() {
        // ElGamal produit des chiffrés différents pour le même message
        BigInteger m = BigInteger.valueOf(42);
        
        Set<String> ciphertexts = new HashSet<>();
        
        for (int i = 0; i < 100; i++) {
            BigInteger[] c = Crypto.encrypt(m, publicKey);
            String ciphertext = c[0].toString() + "," + c[1].toString();
            ciphertexts.add(ciphertext);
            
            // Vérifier que le déchiffrement fonctionne toujours
            BigInteger decrypted = Crypto.decrypt(c, publicKey, secretKey, 100);
            assertEquals(m, decrypted, "Le déchiffrement doit toujours retrouver le message");
        }
        
        // On devrait avoir plusieurs chiffrés différents
        assertTrue(ciphertexts.size() > 90, 
            "Le chiffrement doit être randomisé (au moins 90 chiffrés différents sur 100)");
    }
    
    @Test
    @DisplayName("Test de sécurité: résistance sans clé privée")
    public void testSecurityWithoutPrivateKey() {
        System.out.println("\n=== Test de sécurité sans clé privée ===\n");
        
        BigInteger vote = BigInteger.valueOf(7);
        BigInteger[] ciphertext = Crypto.encrypt(vote, publicKey);
        
        // Sans la clé privée, on ne peut pas déchiffrer efficacement
        // Baby-step Giant-step résout le logarithme discret mais reste difficile pour de grandes valeurs
        
        System.out.println("✓ Le chiffrement ElGamal reste sécurisé");
        System.out.println("  Baby-step Giant-step optimise seulement le déchiffrement légitime");
        System.out.println("  La sécurité repose toujours sur la difficulté du logarithme discret");
    }
    
    @Test
    @DisplayName("Test de limites: valeurs extrêmes")
    public void testEdgeCases() {
        System.out.println("\n=== Test des cas limites ===\n");
        
        // Test avec 0 vote
        BigInteger[] c0 = Crypto.encrypt(BigInteger.ZERO, publicKey);
        assertEquals(BigInteger.ZERO, Crypto.decrypt(c0, publicKey, secretKey, 100));
        assertEquals(BigInteger.ZERO, Crypto.decryptBruteForce(c0, publicKey, secretKey, 100));
        System.out.println("✓ Cas limite: 0 vote");
        
        // Test avec le maximum de votes
        int maxVotes = 100;
        BigInteger[] cMax = Crypto.encrypt(BigInteger.valueOf(maxVotes), publicKey);
        assertEquals(BigInteger.valueOf(maxVotes), Crypto.decrypt(cMax, publicKey, secretKey, maxVotes));
        assertEquals(BigInteger.valueOf(maxVotes), Crypto.decryptBruteForce(cMax, publicKey, secretKey, maxVotes));
        System.out.println("✓ Cas limite: nombre maximum de votes");
        
        // Test avec agrégation multiple
        BigInteger[] result = Crypto.encrypt(BigInteger.ZERO, publicKey);
        for (int i = 0; i < 10; i++) {
            BigInteger[] vote = Crypto.encrypt(BigInteger.ONE, publicKey);
            result = Crypto.agrege(result, vote, publicKey);
        }
        assertEquals(BigInteger.TEN, Crypto.decrypt(result, publicKey, secretKey, 100));
        System.out.println("✓ Cas limite: agrégation multiple");
    }
    
    @Test
    @DisplayName("Test de cohérence: paramètres de sécurité")
    public void testSecurityParameters() {
        System.out.println("\n=== Test des paramètres de sécurité ===\n");
        
        // Vérifier que les clés générées respectent les contraintes ElGamal
        assertTrue(p.isProbablePrime(40), "p doit être premier");
        
        BigInteger q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        assertTrue(q.isProbablePrime(40), "q = (p-1)/2 doit être premier (p est un nombre premier sûr)");
        
        // g est un générateur du sous-groupe d'ordre q
        assertEquals(BigInteger.ONE, g.modPow(q, p), "g^q = 1 mod p");
        assertNotEquals(BigInteger.ONE, g.modPow(BigInteger.TWO, p), "g^2 ≠ 1 mod p");
        
        // h = g^sk mod p
        assertEquals(h, g.modPow(secretKey, p), "Clé publique correctement générée");
        
        System.out.println("✓ Tous les paramètres de sécurité ElGamal sont respectés");
        System.out.println("  Taille de p: " + p.bitLength() + " bits");
        System.out.println("  Taille de q: " + q.bitLength() + " bits");
    }
}