package com.namics.distrelec.b2b.core.radware;

import com.namics.distrelec.b2b.core.radware.exception.DistRadwareAPIException;

public interface DistrelecRadwareAPIClient {

    String getRadwareSessionToken(String requestString) throws DistRadwareAPIException;

    String getAuthTokenResponse(String requestString) throws DistRadwareAPIException;

    String getRadwareAppListResponse(String authToken) throws DistRadwareAPIException;

    void clearCDNCacheOnRadware(String applicationId, String authToken) throws DistRadwareAPIException;

}
