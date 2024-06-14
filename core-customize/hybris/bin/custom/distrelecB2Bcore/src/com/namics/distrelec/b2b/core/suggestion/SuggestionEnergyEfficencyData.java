/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.suggestion;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * {@code EnergyEfficencyData}
 * 
 * 
 * @since Distrelec 4.10
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "efficency", "power", "type", "manufacturer" })
public class SuggestionEnergyEfficencyData implements Serializable {

    @JsonProperty("efficency")
    private String efficency;

    @JsonProperty("power")
    private String power;

    @JsonProperty("type")
    private String type;

    @JsonProperty("manufacturer")
    private String manufacturer;

    public String getEfficency() {
        return efficency;
    }

    public void setEfficency(final String efficency) {
        this.efficency = efficency;
    }

    public String getPower() {
        return power;
    }

    public void setPower(final String power) {
        this.power = power;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
