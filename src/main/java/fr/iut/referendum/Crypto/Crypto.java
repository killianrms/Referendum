package fr.iut.referendum.Crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public abstract class Crypto {
    private static final SecureRandom random = new SecureRandom();

    public static BigInteger[][] encrypt(BigInteger m, BigInteger[] pk) {
        BigInteger p = pk[0];
        BigInteger g = pk[1];
        BigInteger h = pk[2];
        BigInteger k = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE)); // k < p-1
        BigInteger c1 = g.modPow(k, p); // c1 = g^k mod p
        BigInteger c2 = g.modPow(m, p).multiply(h.modPow(k, p)).mod(p); // c2 = g^m * publickey^k mod p
        return new BigInteger[][] {{c1, c2}, {k}};
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

    // Zero Knowledge Protocol
    public static BigInteger[] createZKProof(BigInteger m, BigInteger[] c, BigInteger[] pk, BigInteger r) throws NoSuchAlgorithmException {
        BigInteger c1 = c[0];
        BigInteger c2 = c[1];
        BigInteger p = pk[0];
        BigInteger g = pk[1];
        BigInteger h = pk[2];

        int mi = m.intValue();
        int mj = 1 - m.intValue();

        BigInteger q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);

        // False value
        BigInteger[] A = new BigInteger[2];
        BigInteger[] B = new BigInteger[2];
        BigInteger[] chall = new BigInteger[2];
        BigInteger[] rep = new BigInteger[2];

        chall[mj] = new BigInteger(q.bitLength(), random).mod(q);
        rep[mj] = new BigInteger(q.bitLength(), random).mod(q);

        A[mj] = ((g.modPow(rep[mj], p)).multiply(c1.modPow(chall[mj], p))).mod(p);
        B[mj] = ((h.modPow(rep[mj], p)).multiply((c2.multiply(g.modInverse(p))).modPow(chall[mj], p))).mod(p);

        // True value
        BigInteger w = new BigInteger(q.bitLength(), random).mod(q);
        A[mi] = g.modPow(w, p);
        B[mi] = h.modPow(w, p);

        BigInteger[] values = {c1, c2, A[0], B[0], A[1], B[1]};

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for (BigInteger v : values) {
            byte[] b = v.toByteArray();
            md.update(b);
        }

        byte[] hashBytes = md.digest();
        BigInteger hashInt = new BigInteger(1, hashBytes).mod(p); // e

        chall[mi] = hashInt.subtract(chall[mj]).mod(q);
        rep[mi] = w.subtract(chall[mj].multiply(r).mod(q)).mod(q);

        return new BigInteger[]{chall[0], rep[0], chall[1], rep[1]};
    }

    public static boolean verifyZKProof(BigInteger[] c, BigInteger[] pk, BigInteger[] pi) throws NoSuchAlgorithmException {
        BigInteger c1 = c[0];
        BigInteger c2 = c[1];
        BigInteger p = pk[0];
        BigInteger g = pk[1];
        BigInteger h = pk[2];

        BigInteger chall0 = pi[0];
        BigInteger rep0 = pi[1];
        BigInteger chall1 = pi[2];
        BigInteger rep1 = pi[3];

        BigInteger q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);

        BigInteger A0 = (g.modPow(rep0, p)).multiply(c1.modPow(chall0, p)).mod(p);
        BigInteger A1 = (g.modPow(rep1, p)).multiply(c1.modPow(chall1, p)).mod(p);
        BigInteger B0 = (h.modPow(rep0, p)).multiply(c2.modPow(chall0, p)).mod(p);
        BigInteger B1 = (h.modPow(rep1, p)).multiply((c2.multiply(g.modInverse(p)).mod(p)).modPow(chall1, p)).mod(p);

        BigInteger[] values = {c1, c2, A0, B0, A1, B1};

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for (BigInteger v : values) {
            byte[] b = v.toByteArray();
            md.update(b);
        }

        BigInteger verif = chall0.add(chall1).mod(q);

        byte[] hashBytes = md.digest();
        BigInteger result = new BigInteger(1, hashBytes).mod(q);

        return result.equals(verif);
    }

}