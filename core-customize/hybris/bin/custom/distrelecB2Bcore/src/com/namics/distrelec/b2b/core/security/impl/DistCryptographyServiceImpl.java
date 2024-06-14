package com.namics.distrelec.b2b.core.security.impl;

import com.namics.distrelec.b2b.core.security.DistCryptographyService;
import com.namics.distrelec.b2b.core.util.DistCryptography;
import org.springframework.stereotype.Service;

@Service
public class DistCryptographyServiceImpl implements DistCryptographyService {

    @Override
    public String encryptString(final String plainText, final String secretKey) {
        return DistCryptography.encryptString(plainText, secretKey);
    }
}
