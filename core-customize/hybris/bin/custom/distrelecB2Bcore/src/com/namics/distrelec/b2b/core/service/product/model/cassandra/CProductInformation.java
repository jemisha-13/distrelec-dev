/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.model.cassandra;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * {@code CProductInformation}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.13
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "productCode", "articleInformation", "environmentalInformation", "countryOfOrigin", "articleNotes", "familyInformation" })
public class CProductInformation implements Serializable {

    @JsonProperty("productCode")
    private String productCode;

    @JsonProperty("articleNotes")
    private StringBuffer articleNotes;

    @JsonProperty("familyInformation")
    private StringBuffer familyInformation;

    @JsonProperty("articleInformation")
    private String articleInformation;
    
    @JsonProperty("articleInformation_16_17")
    private String articleInformation_16_17;

    @JsonProperty("environmentalInformation")
    private String environmentalInformation;

    @JsonProperty("countryOfOrigin")
    private String countryOfOrigin;

    /**
     * Create a new instance of {@code CProductInformation}
     */
    public CProductInformation() {
        familyInformation = new StringBuffer();
        articleNotes = new StringBuffer();
    }

    public String[] toCsvRow() {
    	String[] row = new String[7];
        row[0] = productCode;
        row[1] = articleInformation;
        row[2] = environmentalInformation;
        row[3] = countryOfOrigin;
        row[4] = articleNotes.toString();
        row[5] = familyInformation.toString();
        row[6] = articleInformation_16_17;
        return row;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getEnvironmentalInformation() {
        return environmentalInformation;
    }

    public void setEnvironmentalInformation(String environmentalInformation) {
        this.environmentalInformation = environmentalInformation;
    }

    public String getArticleInformation() {
        return articleInformation;
    }

    public void setArticleInformation(String articleInformation) {
        this.articleInformation = articleInformation;
    }

    public String getArticleInformation_16_17() {
		return articleInformation_16_17;
	}

	public void setArticleInformation_16_17(String articleInformation_16_17) {
		this.articleInformation_16_17 = articleInformation_16_17;
	}
	
	public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public StringBuffer getArticleNotes() {
        return articleNotes;
    }

    public void setArticleNotes(StringBuffer articleNotes) {
        this.articleNotes = articleNotes;
    }

    public StringBuffer getFamilyInformation() {
        return familyInformation;
    }

    public void setFamilyInformation(StringBuffer familyInformation) {
        this.familyInformation = familyInformation;
    }

}
