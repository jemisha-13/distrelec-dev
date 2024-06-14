package com.namics.distrelec.b2b.facades.legal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface DistQualityAndLegalFacade {
    List<String> getProductCodes(final String file) throws DistQualityAndLegalInvalidFileUploadException, IOException;

    List<String> findExistingProductCodes(List<String> codes) throws DistQualityAndLegalInvalidFileUploadException;

    List<String> filterInvalidProductCodes(final List<String> rawCodes, final List<String> existingCodes);

    String cleanup(String line);
}
