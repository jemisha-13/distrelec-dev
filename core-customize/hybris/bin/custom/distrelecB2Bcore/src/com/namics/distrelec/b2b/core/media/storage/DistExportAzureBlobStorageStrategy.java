package com.namics.distrelec.b2b.core.media.storage;

import org.apache.commons.lang.StringUtils;

public class DistExportAzureBlobStorageStrategy extends DistWindowsAzureBlobStorageStrategy {

    @Override
    protected String assembleLocation(String mediaId, String realFileName) {
        if (StringUtils.isNotBlank(realFileName)) {
            return realFileName;
        } else {
            return mediaId;
        }
    }
}
