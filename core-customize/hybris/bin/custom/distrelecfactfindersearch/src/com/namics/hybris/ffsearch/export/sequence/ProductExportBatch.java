package com.namics.hybris.ffsearch.export.sequence;

import java.util.List;

public class ProductExportBatch {

    private final String cmsSiteUid;
    private final String salesOrgCode;
    private final List<Channel> channels;
    private final List<Long> productPks;

    public ProductExportBatch(final String cmsSiteUid, final String salesOrgCode, final List<Channel> channels,
            final List<Long> productPks) {
        this.cmsSiteUid = cmsSiteUid;
        this.salesOrgCode = salesOrgCode;
        this.channels = channels;
        this.productPks = productPks;
    }

    public String getCmsSiteUid() {
        return cmsSiteUid;
    }

    public String getSalesOrgCode() {
        return salesOrgCode;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Long> getProductPks() {
        return productPks;
    }
}
