package com.namics.distrelec.b2b.core.service.eol.service;

public interface DistEOLProductsRemovalService {
    void removeEOLProducts(int monthsWithReferences, int monthsWithoutReferences, Integer removeCount);
}
