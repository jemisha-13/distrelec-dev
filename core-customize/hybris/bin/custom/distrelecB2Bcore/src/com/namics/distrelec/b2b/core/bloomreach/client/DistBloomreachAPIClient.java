package com.namics.distrelec.b2b.core.bloomreach.client;

import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachBatchException;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachExportException;

public interface DistBloomreachAPIClient {
    
    void callBatchRequest(String requestString) throws DistBloomreachBatchException;

    String exportCustomerConsents(String requestString) throws DistBloomreachExportException;
    
}
