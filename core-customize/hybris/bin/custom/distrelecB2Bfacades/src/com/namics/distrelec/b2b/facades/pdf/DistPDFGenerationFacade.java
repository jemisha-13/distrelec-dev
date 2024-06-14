package com.namics.distrelec.b2b.facades.pdf;

import java.io.InputStream;

public interface DistPDFGenerationFacade {

    InputStream getPDFStreamForCart();

    String getCartPdfHeaderValue();
}
