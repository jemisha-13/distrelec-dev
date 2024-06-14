package com.namics.distrelec.b2b.core.media.storage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;

class AzureBlobLocationNormalizerImpl implements AzureBlobLocationNormalizer {

    @Override
    public String normalizeLocation(String location) {
        String prefix = StringUtils.substringBeforeLast(location, "/");
        String suffix = StringUtils.substringAfterLast(location, "/");
        suffix = suffix.replace("+", "%2B");
        String decodedSuffix = URLDecoder.decode(suffix, StandardCharsets.UTF_8);
        String normalizedSuffix = Normalizer.normalize(decodedSuffix, Normalizer.Form.NFD);
        String encodedSuffix = UriUtils.encodePathSegment(normalizedSuffix, StandardCharsets.UTF_8);
        return prefix + "/" + encodedSuffix;
    }

    @Override
    public String denormalizeLocation(String location) {
        return Normalizer.normalize(location, Normalizer.Form.NFC);
    }
}
