/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import com.namics.hybris.ffsearch.data.campaign.advisor.AdvisorCampaignAnswerData;
import com.namics.hybris.ffsearch.data.campaign.advisor.AdvisorCampaignData;
import com.namics.hybris.ffsearch.data.campaign.advisor.AdvisorCampaignQuestionData;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts {@link AdvisorCampaignData} objects.
 *
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 *
 * @param <QUERY>
 * @param <STATE>
 */
public class AdvisorCampaignConverter<QUERY, STATE> extends AbstractConverter<AdvisorCampaignData<QUERY>, AdvisorCampaignData<STATE>> {

    private Converter<QUERY, STATE> searchStateConverter;

    @Override
    protected AdvisorCampaignData<STATE> createTarget() {
        return new AdvisorCampaignData<STATE>();
    }

    @Override
    public void populate(final AdvisorCampaignData source, final AdvisorCampaignData target) {
        target.setQuestions(convertQuestions(source.getQuestions()));
    }

    private List<AdvisorCampaignQuestionData> convertQuestions(final List<AdvisorCampaignQuestionData> sourceQuestions) {
        final List<AdvisorCampaignQuestionData> targetQuestions = new ArrayList<AdvisorCampaignQuestionData>();
        for (final AdvisorCampaignQuestionData sourceQuestion : sourceQuestions) {
            targetQuestions.add(convertQuestion(sourceQuestion));
        }
        return targetQuestions;
    }

    private AdvisorCampaignQuestionData convertQuestion(final AdvisorCampaignQuestionData sourceQuestion) {
        final AdvisorCampaignQuestionData targetQuestion = new AdvisorCampaignQuestionData();
        targetQuestion.setQuestion(sourceQuestion.getQuestion());
        targetQuestion.setAnswers(convertAnswers(sourceQuestion.getAnswers()));
        return targetQuestion;
    }

    private List<AdvisorCampaignAnswerData> convertAnswers(final List<AdvisorCampaignAnswerData> sourceAnswers) {
        final List<AdvisorCampaignAnswerData> targetAnswers = new ArrayList<AdvisorCampaignAnswerData>();
        for (final AdvisorCampaignAnswerData sourceAnswer : sourceAnswers) {
            targetAnswers.add(convertAnswer(sourceAnswer));
        }
        return targetAnswers;
    }

    private AdvisorCampaignAnswerData convertAnswer(final AdvisorCampaignAnswerData<QUERY> sourceAnswer) {
        final AdvisorCampaignAnswerData targetAnswer = new AdvisorCampaignAnswerData();
        targetAnswer.setText(sourceAnswer.getText());
        targetAnswer.setImage(sourceAnswer.getImage());
        targetAnswer.setQuery(getSearchStateConverter().convert(sourceAnswer.getQuery()));
        return targetAnswer;
    }

    public Converter<QUERY, STATE> getSearchStateConverter() {
        return searchStateConverter;
    }

    @Required
    public void setSearchStateConverter(final Converter<QUERY, STATE> searchStateConverter) {
        this.searchStateConverter = searchStateConverter;
    }

}
