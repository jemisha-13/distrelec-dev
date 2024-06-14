package com.namics.distrelec.b2b.facades.legal;

import java.io.IOException;
import java.util.List;

public interface DistQualityAndLegalParser {

    List<String> getProductCodesFromFile(String filename) throws DistQualityAndLegalInvalidFileUploadException, IOException;

}
