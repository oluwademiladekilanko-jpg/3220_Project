package org.example.backend.security;

public interface PasswordHasher {
    String hash(String password);

    boolean verify(String password, String storedHash);

    /** False for legacy plain-text values that predate hashing. */
    boolean isHashed(String stored);
}
