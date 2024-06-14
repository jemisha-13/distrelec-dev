/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.survey.impl;

import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyAnswerModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyQuestionModel;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyAnswerData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyQuestionData;
import com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService;
import com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.List;

/**
 * {@code DefaultDistOnlineSurveyFacade}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DefaultDistOnlineSurveyFacade implements DistOnlineSurveyFacade {

    @Autowired
    private DistOnlineSurveyService onlineSurveyService;

    @Autowired
    @Qualifier("onlineSurveyQuestionConverter")
    private Converter<DistOnlineSurveyQuestionModel, DistOnlineSurveyQuestionData> onlineSurveyQuestionConverter;

    @Autowired
    @Qualifier("onlineSurveyConverter")
    private Converter<DistOnlineSurveyModel, DistOnlineSurveyData> onlineSurveyConverter;

    @Autowired
    @Qualifier("onlineSurveyAnswerConverter")
    private Converter<DistOnlineSurveyAnswerModel, DistOnlineSurveyAnswerData> onlineSurveyAnswerConverter;

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#create(com.namics.distrelec.b2b.core.service.survey.data.
     * DistOnlineSurveyData)
     */
    @Override
    public void create(final DistOnlineSurveyData onlineSurveyData) {
        getOnlineSurveyService().create(onlineSurveyData);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#create(com.namics.distrelec.b2b.core.service.survey.data.
     * DistOnlineSurveyAnswerData)
     */
    @Override
    public void create(final DistOnlineSurveyAnswerData onlineSurveyAnswerData) {
        getOnlineSurveyService().create(onlineSurveyAnswerData);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#create(java.util.List)
     */
    @Override
    public void create(final List<DistOnlineSurveyAnswerData> surveyAnswers) {
        getOnlineSurveyService().create(surveyAnswers);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#findByVersion(java.lang.String)
     */
    @Override
    public DistOnlineSurveyData findByVersion(final String version) {
        return getOnlineSurveyConverter().convert(getOnlineSurveyService().findByVersion(version));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#findByVersion(java.lang.String, java.util.Date)
     */
    @Override
    public DistOnlineSurveyData findByVersion(final String version, final Date date) {
        return getOnlineSurveyConverter().convert(getOnlineSurveyService().findByVersion(version, date));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#findActiveOnDate(java.util.Date)
     */
    @Override
    public List<DistOnlineSurveyData> findActiveOnDate(final Date date) {
        return Converters.convertAll(getOnlineSurveyService().findActiveOnDate(date), getOnlineSurveyConverter());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#getAnswers(java.lang.String)
     */
    @Override
    public List<DistOnlineSurveyAnswerData> getAnswers(final String version) {
        return Converters.convertAll(getOnlineSurveyService().getAnswers(version), getOnlineSurveyAnswerConverter());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#getAnswers(com.namics.distrelec.b2b.core.service.survey.data.
     * DistOnlineSurveyData, boolean)
     */
    @Override
    public List<DistOnlineSurveyAnswerData> getAnswers(final String version, final boolean exported) {
        return Converters.convertAll(getOnlineSurveyService().getAnswers(version, exported), getOnlineSurveyAnswerConverter());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#getAnswersForExport(java.lang.String)
     */
    @Override
    public List<DistOnlineSurveyAnswerData> getAnswersForExport(final String version) {
        return Converters.convertAll(getOnlineSurveyService().getAnswersForExport(version), getOnlineSurveyAnswerConverter());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#getSurveyQuestion(java.lang.String)
     */
    @Override
    public DistOnlineSurveyQuestionData getSurveyQuestion(final String questionUid) {
        return getOnlineSurveyQuestionConverter().convert(getOnlineSurveyService().getSurveyQuestion(questionUid));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#getAnswersForQuestion(java.lang.String)
     */
    @Override
    public List<DistOnlineSurveyAnswerData> getAnswersForQuestion(final String questionUid) {
        return Converters.convertAll(getOnlineSurveyService().getAnswersForQuestion(questionUid), getOnlineSurveyAnswerConverter());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade#getAnswersForQuestion(java.lang.String, java.lang.String)
     */
    @Override
    public List<DistOnlineSurveyAnswerData> getAnswersForQuestion(final String version, final String questionUid) {
        return Converters.convertAll(getOnlineSurveyService().getAnswersForQuestion(version, questionUid), getOnlineSurveyAnswerConverter());
    }

    public DistOnlineSurveyService getOnlineSurveyService() {
        return onlineSurveyService;
    }

    public void setOnlineSurveyService(final DistOnlineSurveyService onlineSurveyService) {
        this.onlineSurveyService = onlineSurveyService;
    }

    public Converter<DistOnlineSurveyModel, DistOnlineSurveyData> getOnlineSurveyConverter() {
        return onlineSurveyConverter;
    }

    public void setOnlineSurveyConverter(final Converter<DistOnlineSurveyModel, DistOnlineSurveyData> onlineSurveyConverter) {
        this.onlineSurveyConverter = onlineSurveyConverter;
    }

    public Converter<DistOnlineSurveyQuestionModel, DistOnlineSurveyQuestionData> getOnlineSurveyQuestionConverter() {
        return onlineSurveyQuestionConverter;
    }

    public void setOnlineSurveyQuestionConverter(final Converter<DistOnlineSurveyQuestionModel, DistOnlineSurveyQuestionData> onlineSurveyQuestionConverter) {
        this.onlineSurveyQuestionConverter = onlineSurveyQuestionConverter;
    }

    public Converter<DistOnlineSurveyAnswerModel, DistOnlineSurveyAnswerData> getOnlineSurveyAnswerConverter() {
        return onlineSurveyAnswerConverter;
    }

    public void setOnlineSurveyAnswerConverter(final Converter<DistOnlineSurveyAnswerModel, DistOnlineSurveyAnswerData> onlineSurveyAnswerConverter) {
        this.onlineSurveyAnswerConverter = onlineSurveyAnswerConverter;
    }
}
