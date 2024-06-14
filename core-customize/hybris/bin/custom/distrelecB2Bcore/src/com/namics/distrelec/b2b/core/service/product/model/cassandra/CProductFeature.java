/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.model.cassandra;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

/**
 * {@code CProductFeature}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.12
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "qualifier", "value", "position", "visibility", "unit", "unitSymbol", "classAttrAssExternalID" })
public class CProductFeature implements Serializable {

    @JsonProperty("qualifier")
    private String qualifier;
    @JsonProperty("value")
    private String value;
    @JsonIgnore
    private Integer featurePosition;
    @JsonProperty("position")
    private Integer valuePosition;
    @JsonProperty("visibility")
    private ClassificationAttributeVisibilityEnum visibility;
    @JsonProperty("attrAssExtID")
    private String classAttrAssExternalID;
    @JsonProperty("unit")
    private String unit;
    @JsonProperty("unitSymbol")
    private String unitSymbol;
    @JsonIgnore
    private boolean langIndependent;

    // Properties which will be ignored by JSON serialisation
    @JsonIgnore
    private ClassAttributeAssignmentModel classificationAttributeAssignment;

    /**
     * Create a new instance of {@code CProductFeature}
     */
    public CProductFeature() {
        super();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }

    public Integer getFeaturePosition() {
        return featurePosition;
    }

    public void setFeaturePosition(final Integer featurePosition) {
        this.featurePosition = featurePosition;
    }

    public Integer getValuePosition() {
        return valuePosition;
    }

    public void setValuePosition(final Integer valuePosition) {
        this.valuePosition = valuePosition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    public String getUnitSymbol() {
        return unitSymbol;
    }

    public void setUnitSymbol(String unitSymbol) {
        this.unitSymbol = unitSymbol;
    }

    public ClassificationAttributeVisibilityEnum getVisibility() {
        return visibility;
    }

    public void setVisibility(ClassificationAttributeVisibilityEnum visibility) {
        this.visibility = visibility;
    }

    public ClassAttributeAssignmentModel getClassificationAttributeAssignment() {
        return classificationAttributeAssignment;
    }

    public void setClassificationAttributeAssignment(final ClassAttributeAssignmentModel classificationAttributeAssignment) {
        this.classificationAttributeAssignment = classificationAttributeAssignment;
    }

    public String getClassAttrAssExternalID() {
        return classAttrAssExternalID;
    }

    public void setClassAttrAssExternalID(String classAttrAssExternalID) {
        this.classAttrAssExternalID = classAttrAssExternalID;
    }

    public void setLangIndependent(boolean langIndependent) {
        this.langIndependent = langIndependent;
    }

    public boolean isLangIndependent() {
        return langIndependent;
    }
}
