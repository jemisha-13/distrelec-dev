/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal.data;

/**
 * {@code MissingOrder}
 * 
 *
 * @since Distrelec 5.10
 */
public class MissingOrder {

    private String code;
    private String created;
    private String salesOrg;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(final String created) {
        this.created = created;
    }

    public String getSalesOrg() {
        return salesOrg;
    }

    public void setSalesOrg(final String salesOrg) {
        this.salesOrg = salesOrg;
    }

    @Override
    public String toString() {
        return "code=" + code + "created=" + created + "SalesOrg" + salesOrg;
    }
}
