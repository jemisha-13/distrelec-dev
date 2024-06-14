/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.message.queue.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * {@code CRelatedData}
 * 
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "type", "values" })
public class CRelatedData implements Serializable {

    @JsonProperty("type")
    private RelatedDataType type;
    @JsonProperty("values")
    private List<CIValue> values;

    /**
     * Create a new instance of {@code CRelatedData}
     */
    public CRelatedData() {
        this(null);
    }

    /**
     * Create a new instance of {@code CRelatedData}
     * 
     * @param type
     */
    public CRelatedData(final RelatedDataType type) {
        this(type, null);
    }

    /**
     * Create a new instance of {@code CRelatedData}
     * 
     * @param type
     * @param values
     */
    public CRelatedData(final RelatedDataType type, final List<CIValue> values) {
        this.type = type;
        this.values = values;
    }
    
    @Override
    public String toString() {
        return String.format("CRelatedData [getType()=%s, getValues()=%s]", getType(), Arrays.toString(getValues().toArray()));
    }

    // Getters & Setters

    public RelatedDataType getType() {
        return type;
    }

    public void setType(final RelatedDataType type) {
        this.type = type;
    }

    public List<CIValue> getValues() {
        return values;
    }

    public void setValues(final List<CIValue> values) {
        this.values = values;
    }
}
