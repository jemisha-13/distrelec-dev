package com.namics.distrelec.b2b.core.pdf.service;

import com.namics.distrelec.b2b.core.pdf.data.DistPdfData;

import java.io.InputStream;

public interface DistPDFService {

    InputStream generatePdfFromData(DistPdfData data);
}
