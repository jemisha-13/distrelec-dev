package com.namics.distrelec.b2b.core.inout.erp;

import de.hybris.platform.commerceservices.order.CommerceCartModification;

import java.util.List;

public interface ErpErrorHandlerService {

    boolean isProductStatusMisalignmentException(String message);
    
    String extractProductCodeFromMessage(String message);

    boolean isTemporaryQualityBlockException(String message);

    List<CommerceCartModification> updateProductQuantity(String code, long quantity);
}
