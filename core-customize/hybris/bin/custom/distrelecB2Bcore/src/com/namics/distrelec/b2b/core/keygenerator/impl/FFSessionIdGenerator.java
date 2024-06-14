package com.namics.distrelec.b2b.core.keygenerator.impl;

import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class FFSessionIdGenerator implements KeyGenerator {

    private final SecureRandom random;
    private final MessageDigest sha;

    public FFSessionIdGenerator() throws NoSuchAlgorithmException {
        random = SecureRandom.getInstance("SHA1PRNG");
        sha = MessageDigest.getInstance("SHA-1");
    }

    @Override
    public Object generate() {
        final String randomNum = String.valueOf(random.nextInt());
        final byte[] result = sha.digest(randomNum.getBytes());
        return String.valueOf(Hex.encodeHex(result));
    }

    @Override
    public Object generateFor(Object o) {
        return generate();
    }

    @Override
    public void reset() {
        // do nothing as not needed
    }
}
