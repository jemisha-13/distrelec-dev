/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.cms.data;

import de.hybris.platform.commercefacades.product.data.ImageData;

/**
 * {@code DistrelecCategoryManagerCardData}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.7
 */
public class DistrelecCategoryManagerCardData {

    private String name;
    private String jobTitle;
    private String organisation;
    private String quote;
    private String tipp;
    private String ctaText;
    private String ctaLink;
    private ImageData image;
    private boolean rightFloat;

    /**
     * Create a new instance of {@code DistrelecCategoryManagerCardData}
     */
    public DistrelecCategoryManagerCardData() {
        super();
    }

    /**
     * Create a new instance of {@code DistrelecCategoryManagerCardData}
     *
     * @param name
     * @param jobTitle
     * @param organisation
     * @param quote
     * @param tipp
     * @param ctaText
     * @param ctaLink
     */
    public DistrelecCategoryManagerCardData(final String name, final String jobTitle, final String organisation, final String quote, final String tipp,
            final String ctaText, final String ctaLink, final boolean rightFloat) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.organisation = organisation;
        this.quote = quote;
        this.tipp = tipp;
        this.ctaText = ctaText;
        this.ctaLink = ctaLink;
        this.rightFloat = rightFloat;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(final String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(final String organisation) {
        this.organisation = organisation;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(final String quote) {
        this.quote = quote;
    }

    public String getTipp() {
        return tipp;
    }

    public void setTipp(final String tipp) {
        this.tipp = tipp;
    }

    public ImageData getImage() {
        return image;
    }

    public void setImage(final ImageData image) {
        this.image = image;
    }

    public String getCtaText() {
        return ctaText;
    }

    public void setCtaText(final String ctaText) {
        this.ctaText = ctaText;
    }

    public String getCtaLink() {
        return ctaLink;
    }

    public void setCtaLink(final String ctaLink) {
        this.ctaLink = ctaLink;
    }

    public boolean isRightFloat() {
        return rightFloat;
    }

    public void setRightFloat(final boolean rightFloat) {
        this.rightFloat = rightFloat;
    }
}
