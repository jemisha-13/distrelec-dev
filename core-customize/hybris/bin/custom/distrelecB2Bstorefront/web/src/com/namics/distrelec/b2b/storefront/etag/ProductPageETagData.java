package com.namics.distrelec.b2b.storefront.etag;

import java.util.Date;
import java.util.Objects;

public class ProductPageETagData {

    private final String productCode;
    private final Date lastModified;
    private final String siteUid;
    private final String languageCode;
    private final String channelId;
    private final String revision;

    public ProductPageETagData(String productCode, Date lastModified, String siteUid, String languageCode, String channelId, String revision) {
        this.productCode = productCode;
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
        ProductPageETagData that = (ProductPageETagData) o;
        return Objects.equals(productCode, that.productCode) && Objects.equals(lastModified, that.lastModified) && Objects.equals(siteUid, that.siteUid) && Objects.equals(languageCode, that.languageCode) && Objects.equals(channelId, that.channelId) && Objects.equals(revision, that.revision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, lastModified, siteUid, languageCode, channelId, revision);
    }
}