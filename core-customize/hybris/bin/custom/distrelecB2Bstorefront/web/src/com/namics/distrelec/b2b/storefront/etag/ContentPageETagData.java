package com.namics.distrelec.b2b.storefront.etag;

import java.util.Date;
import java.util.Objects;

public class ContentPageETagData {
    private final String label;
    private final Date lastModified;
    private final String siteUid;
    private final String languageCode;
    private final String channelId;
    private final String revision;

    public ContentPageETagData(String label, Date lastModified, String siteUid, String languageCode, String channelId, String revision) {
        this.label = label;
        this.lastModified = lastModified;
        this.siteUid = siteUid;
        this.languageCode = languageCode;
        this.channelId = channelId;
        this.revision = revision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentPageETagData that = (ContentPageETagData) o;
        return Objects.equals(label, that.label) && Objects.equals(lastModified, that.lastModified) && Objects.equals(siteUid, that.siteUid) && Objects.equals(languageCode, that.languageCode) && Objects.equals(channelId, that.channelId) && Objects.equals(revision, that.revision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, lastModified, siteUid, languageCode, channelId, revision);
    }
}
