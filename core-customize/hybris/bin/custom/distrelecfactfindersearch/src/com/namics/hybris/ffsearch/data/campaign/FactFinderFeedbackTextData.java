/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.campaign;

/**
 * POJO for a feedback text data object.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class FactFinderFeedbackTextData {

    private int id;
    private boolean html;
    private String label;
    private String text;

    // // BEGIN GENERATED CODE

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(final boolean html) {
        this.html = html;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

}
