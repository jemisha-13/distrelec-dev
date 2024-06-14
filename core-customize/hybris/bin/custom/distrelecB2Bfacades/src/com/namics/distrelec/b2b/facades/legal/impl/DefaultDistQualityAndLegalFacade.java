package com.namics.distrelec.b2b.facades.legal.impl;

import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalFacade;
import com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException;
import com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultDistQualityAndLegalFacade implements DistQualityAndLegalFacade {

    @Autowired
    private DistQualityAndLegalParser distQualityAndLegalParser;

    @Autowired
    private DistProductService productService;

    public List<String> getProductCodes(final String file) throws DistQualityAndLegalInvalidFileUploadException, IOException {
        return distQualityAndLegalParser.getProductCodesFromFile(file);
    }

    @Override
    public List<String> findExistingProductCodes(List<String> codes) throws DistQualityAndLegalInvalidFileUploadException {
        try {
            return productService.findExistingProductCodes(codes);
        } catch (SQLException e) {
            throw new DistQualityAndLegalInvalidFileUploadException(null);
        }
    }

    @Override
    public List<String> filterInvalidProductCodes(final List<String> rawCodes, final List<String> existingCodes) {
        if (rawCodes.size() == existingCodes.size()) {
            return new ArrayList<>();
        }

        return rawCodes.stream()
                .filter(rawCode -> existingCodes.stream()
                        .noneMatch(code -> code.equals(cleanup(rawCode))))
                .collect(Collectors.toList());
    }

    @Override
    public String cleanup(String line) {
        return line.replaceAll("\\D", StringUtils.EMPTY);
    }
}
