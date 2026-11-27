package org.example.backend.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** PBKDF2-HMAC-SHA256 using only the JDK. Stored format: pbkdf2$iterations$salt$hash (Base64). */
public class Pbkdf2PasswordHasher implements PasswordHasher {
    private static final String PREFIX = "pbkdf2$";
    private static final int ITERATIONS = 65_000;
    private static final int KEY_BITS = 256;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String hash(String password) {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        byte[] key = derive(password, salt, ITERATIONS);
        return PREFIX + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(key);
    }

    @Override
    public boolean verify(String password, String storedHash) {
        if (password == null || !isHashed(storedHash)) return false;
        try {
            String[] parts = storedHash.split("\\$");
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            return MessageDigest.isEqual(expected, derive(password, salt, iterations));
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public boolean isHashed(String stored) {
        return stored != null && stored.startsWith(PREFIX);
    }

    private byte[] derive(String password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing unavailable", e);
        }
    }
}
