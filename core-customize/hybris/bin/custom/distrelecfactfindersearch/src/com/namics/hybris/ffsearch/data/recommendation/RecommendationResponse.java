/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.recommendation;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import de.factfinder.webservice.ws71.FFrecommender.ArrayOfRecord;
import de.factfinder.webservice.ws71.FFrecommender.RecommenderResult;

/**
 * POJO for a recommendation response.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class RecommendationResponse {

    private RecommenderResult recommendations;

    // / BEGIN GENERATED CODE

    public RecommenderResult getRecommendations() {
        if (recommendations == null) {
            recommendations = new RecommenderResult();
        }
        return recommendations;
    }

    public void setRecommendations(final RecommenderResult recommendations) {
        this.recommendations = recommendations;
    }

    @Override
    public String toString() {
        return "RecommendationResponse [recommendations=" + ReflectionToStringBuilder.toString(getRecommendations().getResultRecords().getRecord()) + "]";
    }

}
