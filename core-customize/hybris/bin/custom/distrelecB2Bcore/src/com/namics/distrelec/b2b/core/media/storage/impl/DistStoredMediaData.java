package com.namics.distrelec.b2b.core.media.storage.impl;

import de.hybris.platform.media.storage.impl.StoredMediaData;

import java.util.Date;

public class DistStoredMediaData extends StoredMediaData {

    private final Date lastModified;
    private final boolean downloaded;

    public DistStoredMediaData(String location, String hashForLocation, long size, String mime, Date lastModified, boolean downloaded) {
        super(location, hashForLocation, size, mime);
        this.lastModified = lastModified;
        this.downloaded = downloaded;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public boolean isDownloaded() {
        return downloaded;
    }
}
