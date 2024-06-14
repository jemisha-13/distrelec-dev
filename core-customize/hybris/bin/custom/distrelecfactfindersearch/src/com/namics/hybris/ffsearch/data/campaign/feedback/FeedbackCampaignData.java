/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.campaign.feedback;

import java.util.List;
import java.util.Map;

/**
 * POJO with Feedback Campaign data.
 * 
 * @author ceberle, Namics AG
 * @since Namics Extensions 1.0
 * 
 * @param <RESULT>
 */
public class FeedbackCampaignData<RESULT> {

    private String id;
    private String category;
    private String name;

    private Map<String, String> feedbackTexts;
    private List<RESULT> pushedProducts;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<RESULT> getPushedProducts() {
        return pushedProducts;
    }

    public void setPushedProducts(final List<RESULT> pushedProducts) {
        this.pushedProducts = pushedProducts;
    }

    public Map<String, String> getFeedbackTexts() {
        return feedbackTexts;
    }

    public void setFeedbackTexts(final Map<String, String> feedbackTexts) {
        this.feedbackTexts = feedbackTexts;
    }

}
