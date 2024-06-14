package com.namics.distrelec.b2b.core.mail.internal.data;

import java.util.List;

public class DistMediaLocationData {

    private List<String> missingMedias;
    private List<String> nonExistingBlobs;

    public List<String> getMissingMedias() {
        return missingMedias;
    }

    public void setMissingMedias(final List<String> missingMedias) {
        this.missingMedias = missingMedias;
    }

    public List<String> getNonExistingBlobs() {
        return nonExistingBlobs;
    }

    public void setNonExistingBlobs(final List<String> nonExistingBlobs) {
        this.nonExistingBlobs = nonExistingBlobs;
    }
}
