package fr.iut.referendum.Crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public abstract class Crypto {
    private static final SecureRandom random = new SecureRandom();

    public static BigInteger[] encrypt(BigInteger m, BigInteger[] pk) {
        BigInteger p = pk[0];
        BigInteger g = pk[1];
        BigInteger h = pk[2];
        BigInteger k = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE)); // k < p-1
        BigInteger c1 = g.modPow(k, p); // c1 = g^k mod p
        BigInteger c2 = g.modPow(m, p).multiply(h.modPow(k, p)).mod(p); // c2 = g^m * publickey^k mod p
        return new BigInteger[]{c1, c2};
    }

    public static BigInteger[] genkey() {
        int tauxPremier = 40; // Taux de certitude de primalité
        BigInteger p;
        BigInteger q;
        do {
            q = new BigInteger(512, tauxPremier, random); // q premier
            p = q.multiply(BigInteger.valueOf(2)).add(BigInteger.ONE); // p = 2q + 1
        } while (!p.isProbablePrime(tauxPremier)); // p premier
        BigInteger g;
        do {
            g = new BigInteger(p.subtract(BigInteger.ONE).bitLength() +128, random).mod(p); // g < p-1
        } while (g.modPow(BigInteger.TWO, p).compareTo(BigInteger.ONE) == 0); // g^2 mod p == 0
        if (g.modPow(q, p).compareTo(BigInteger.ONE) != 0) { // g^q mod p != 0
            g = g.modPow(BigInteger.TWO, p); // g = g^2 mod p
        }
        BigInteger sk = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE));
        BigInteger h = g.modPow(sk, p);
        return new BigInteger[]{p, g, h, sk};
    }

    public static BigInteger[] agrege(BigInteger[] c1, BigInteger[] c2, BigInteger[] pk) {
        BigInteger p = pk[0];
        BigInteger u = c1[0].multiply(c2[0]).mod(p);
        BigInteger v = c1[1].multiply(c2[1]).mod(p);
        return new BigInteger[]{u, v};
    }

    public static BigInteger decrypt(BigInteger[] c, BigInteger[] pk, BigInteger sk, int nbVotants) {
        BigInteger c1 = c[0];
        BigInteger c2 = c[1];
        BigInteger p = pk[0];
        BigInteger g = pk[1];

        BigInteger M = c2.multiply(c1.modPow(sk, p).modInverse(p)).mod(p);   // M = v × (u^x)^−1 mod p

        // Utilisation de Baby-step Giant-step pour trouver m tel que g^m = M mod p
        return babyStepGiantStep(g, M, p, nbVotants);
    }

    /**
     * Version originale avec recherche exhaustive (pour comparaison)
     */
    public static BigInteger decryptBruteForce(BigInteger[] c, BigInteger[] pk, BigInteger sk, int nbVotants) {
        BigInteger c1 = c[0];
        BigInteger c2 = c[1];
        BigInteger p = pk[0];
        BigInteger g = pk[1];

        BigInteger M = c2.multiply(c1.modPow(sk, p).modInverse(p)).mod(p);   // M = v × (u^x)^−1 mod p

        BigInteger B = BigInteger.valueOf(nbVotants);
        for (BigInteger m = BigInteger.ZERO; m.compareTo(B) <= 0; m = m.add(BigInteger.ONE)) {
            BigInteger gPowM = g.modPow(m, p);
            if (gPowM.equals(M)) {
                return m;
            }
        }
        System.out.println("Déchiffrement échoué");
        return null;
    }

    /**
     * Algorithme Baby-step Giant-step pour résoudre le problème du logarithme discret
     * Trouve m tel que g^m = h mod p, avec m dans [0, B]
     */
    private static BigInteger babyStepGiantStep(BigInteger g, BigInteger h, BigInteger p, int B) {
        // Calcul de m = ceil(sqrt(B))
        int m = (int) Math.ceil(Math.sqrt(B + 1));
        
        // Baby steps: Calcul et stockage de g^j mod p pour j = 0, 1, ..., m-1
        Map<BigInteger, Integer> babySteps = new HashMap<>();
        BigInteger gPower = BigInteger.ONE;
        
        for (int j = 0; j < m; j++) {
            babySteps.put(gPower, j);
            gPower = gPower.multiply(g).mod(p);
        }
        
        // Giant steps: Calcul de g^(-m) mod p
        BigInteger gInverseM = g.modPow(BigInteger.valueOf(m).negate(), p);
        
        // Recherche de la collision
        BigInteger gamma = h;
        for (int i = 0; i <= m; i++) {
            if (babySteps.containsKey(gamma)) {
                int j = babySteps.get(gamma);
                BigInteger result = BigInteger.valueOf(i).multiply(BigInteger.valueOf(m)).add(BigInteger.valueOf(j));
                
                // Vérification que le résultat est dans la plage attendue
                if (result.compareTo(BigInteger.valueOf(B)) <= 0) {
                    return result;
                }
            }
            gamma = gamma.multiply(gInverseM).mod(p);
        }
        
        System.out.println("Déchiffrement échoué");
        return null;
    }

}