package com.namics.distrelec.b2b.core.security;

public interface DistCryptographyService {
    String encryptString(String plainText, String secretKey);
}
