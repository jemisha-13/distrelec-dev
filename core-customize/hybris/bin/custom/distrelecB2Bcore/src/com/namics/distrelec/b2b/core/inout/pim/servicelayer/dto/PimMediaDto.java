package com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto;

/**
 * This DTO represents a hybris Media.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class PimMediaDto {

    private String code;
    private String altText;
    private String description;
    private String url;
    private String mediaFormatCode;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(final String altText) {
        this.altText = altText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getMediaFormatCode() {
        return mediaFormatCode;
    }

    public void setMediaFormatCode(final String mediaFormatCode) {
        this.mediaFormatCode = mediaFormatCode;
    }

}