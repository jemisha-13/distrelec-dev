package com.namics.distrelec.b2b.core.media.storage;

public interface AzureBlobLocationNormalizer {

    String normalizeLocation(String location);

    String denormalizeLocation(String location);
}
