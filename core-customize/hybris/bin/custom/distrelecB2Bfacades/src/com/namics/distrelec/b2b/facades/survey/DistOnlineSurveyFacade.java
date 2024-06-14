/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.survey;

import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyAnswerData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyQuestionData;

import java.util.Date;
import java.util.List;

/**
 * {@code DistOnlineSurveyFacade}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public interface DistOnlineSurveyFacade {
    /**
     * Create and persist the survey model from the {@code DistOnlineSurveyData}
     *
     * @param onlineSurveyData
     *            the source object.
     */
    public void create(final DistOnlineSurveyData onlineSurveyData);

    /**
     * Create and persist the survey answer model from the {@code DistOnlineSurveyAnswerData}
     *
     * @param onlineSurveyAnswerData
     *            the source object.
     */
    public void create(final DistOnlineSurveyAnswerData onlineSurveyAnswerData);

    /**
     * Create and persist the survey answer model from the {@code DistOnlineSurveyAnswerData}
     *
     * @param surveyAnswers
     *            the list of source object.
     */
    public void create(final List<DistOnlineSurveyAnswerData> surveyAnswers);

    /**
     * Fetch the {@code DistOnlineSurveyData} having the specified version id.
     *
     * @param version
     *            the version
     * @return a {@code DistOnlineSurveyData}
     */
    public DistOnlineSurveyData findByVersion(final String version);

    /**
     * Fetch the {@code DistOnlineSurveyData} having the specified version id and active on that date.
     *
     * @param version
     *            the version
     * @param date
     *            the date
     * @return a {@code DistOnlineSurveyData}
     */
    public DistOnlineSurveyData findByVersion(final String version, final Date date);

    /**
     * Fetch all {@code DistOnlineSurveyData} which are active on the specified date.
     *
     * @param date
     * @return a list of {@code DistOnlineSurveyData}
     */
    public List<DistOnlineSurveyData> findActiveOnDate(final Date date);

    /**
     * Fetch all {@code DistOnlineSurveyAnswerData} of the {@code DistOnlineSurveyData} given by its version.
     *
     * @param version
     *            the {@code DistOnlineSurveyData} version.
     * @return a list of {@code DistOnlineSurveyAnswerData}
     */
    public List<DistOnlineSurveyAnswerData> getAnswers(final String version);

    /**
     * Fetch all answers of the specified survey with the specified {@code exported} flag.
     *
     * @param version
     *            the survey version id
     * @param exported
     *            the flag exported
     * @return a list of {@code DistOnlineSurveyAnswerData}
     */
    public List<DistOnlineSurveyAnswerData> getAnswers(final String version, final boolean exported);

    /**
     * Fetch all answers of the specified survey with the {@code exported} flag set to {@code false}. This method is nothing else then
     * {@code getAnswers(survey, false)}
     *
     * @param version
     *            the survey version id.
     * @return a list of {@code DistOnlineSurveyAnswerData}
     */
    public List<DistOnlineSurveyAnswerData> getAnswersForExport(final String version);

    /**
     * Fetch the {@code DistOnlineSurveyQuestionData} having the specified {@code uid}.
     *
     * @param uid
     *            the question uid
     * @return a {@code DistOnlineSurveyQuestionData}
     */
    public DistOnlineSurveyQuestionData getSurveyQuestion(final String uid);

    /**
     * Fetch all answers for the specified question given by its {@code uid}.
     *
     * @param uid
     *            the question uid.
     * @return a list of {@code DistOnlineSurveyAnswerData}
     */
    public List<DistOnlineSurveyAnswerData> getAnswersForQuestion(final String questionUid);

    /**
     * Fetch all answers for survey, given by its version id, and the question given by its {@code uid}.
     *
     * @param version
     *            the survey version.
     * @param uid
     *            the question uid.
     * @return a list of {@code DistOnlineSurveyAnswerData}
     */
    public List<DistOnlineSurveyAnswerData> getAnswersForQuestion(final String version, final String questionUid);
}
