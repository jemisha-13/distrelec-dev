/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.model.cassandra;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * {@code ProductFeatures}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.12
 */
@JsonPropertyOrder({ "product", "language", "features" })
public class CProductFeatures implements Serializable {

    @JsonProperty("article_number")
    private String product;
    @JsonProperty("language")
    private String language;
    @JsonProperty("features")
    private List<CProductFeature> features = new ArrayList<CProductFeature>();

    /**
     * Create a new instance of {@code ProductFeatures}
     */
    public CProductFeatures() {
        super();
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(final String product) {
        this.product = product;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public List<CProductFeature> getFeatures() {
        return features;
    }

    public void setFeatures(final List<CProductFeature> features) {
        this.features = features;
    }
}
