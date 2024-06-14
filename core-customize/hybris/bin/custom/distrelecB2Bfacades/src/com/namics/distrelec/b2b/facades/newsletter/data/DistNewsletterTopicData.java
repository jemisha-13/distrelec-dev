/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.newsletter.data;

/**
 * Data object for a newsletter topic.
 * 
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class DistNewsletterTopicData {

    private String id;
    private String title;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

}
