package com.namics.distrelec.b2b.core.message.queue.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * {@code CInternalLink}
 * 
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @since Distrelec 6.1
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "name", "url", "type" })
public class RelatedFieldName implements Serializable {

    // Related Product or Category or Manufacturer Name which is specific to the below language
    @JsonProperty("name")
    private String name;
    @JsonProperty("url")
    private String url;
    @JsonProperty("type")
    private RelatedDataType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the type
     */
    public RelatedDataType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(RelatedDataType type) {
        this.type = type;
    }
}
