/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.survey.service;

import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyAnswerModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyQuestionModel;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyAnswerData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyData;

/**
 * {@code DistOnlineSurveyService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public interface DistOnlineSurveyService {

    /**
     * Create and persist {@code DistOnlineSurveyModel} from the {@code DistOnlineSurveyData}
     * 
     * @param onlineSurveyData
     *            the source object.
     */
    public void create(final DistOnlineSurveyData onlineSurveyData);

    /**
     * Create and persist {@code DistOnlineSurveyAnswerModel} from the {@code DistOnlineSurveyAnswerData}
     * 
     * @param onlineSurveyAnswerData
     *            the source object.
     */
    public void create(final DistOnlineSurveyAnswerData onlineSurveyAnswerData);

    /**
     * Create and persist {@code DistOnlineSurveyAnswerModel} from the {@code DistOnlineSurveyAnswerData}
     * 
     * @param surveyAnswers
     *            the list of source object.
     */
    public void create(final List<DistOnlineSurveyAnswerData> surveyAnswers);

    /**
     * Fetch the {@code DistOnlineSurveyModel} having the specified version id.
     * 
     * @param version
     *            the version
     * @return a {@code DistOnlineSurveyModel}
     */
    public DistOnlineSurveyModel findByVersion(final String version);

    /**
     * Fetch the {@code DistOnlineSurveyModel} having the specified version id and active on that date.
     * 
     * @param version
     *            the version
     * @param date
     *            the date
     * @return a {@code DistOnlineSurveyModel}
     */
    public DistOnlineSurveyModel findByVersion(final String version, final Date date);

    /**
     * Fetch all {@code DistOnlineSurveyModel} which are active on the specified date.
     * 
     * @param date
     * @return a list of {@code DistOnlineSurveyModel}
     */
    public List<DistOnlineSurveyModel> findActiveOnDate(final Date date);

    /**
     * Fetch all {@code DistOnlineSurveyAnswerModel} of the {@code DistOnlineSurveyModel} given by its version.
     * 
     * @param version
     *            the {@code DistOnlineSurveyModel} version.
     * @return a list of {@code DistOnlineSurveyAnswerModel}
     * @see #getAnswers(DistOnlineSurveyModel)
     */
    public List<DistOnlineSurveyAnswerModel> getAnswers(final String version);

    /**
     * Fetch all answers of the survey.
     * 
     * @param survey
     *            the survey
     * @return a list of {@code DistOnlineSurveyAnswerModel}
     */
    public List<DistOnlineSurveyAnswerModel> getAnswers(final DistOnlineSurveyModel survey);

    /**
     * Fetch all answers of the specified survey with the specified {@code exported} flag.
     * 
     * @param version
     *            the survey version
     * @param exported
     *            the flag exported
     * @return a list of {@code DistOnlineSurveyAnswerModel}
     */
    public List<DistOnlineSurveyAnswerModel> getAnswers(final String version, final boolean exported);

    /**
     * Fetch all answers of the specified survey with the specified {@code exported} flag.
     * 
     * @param survey
     *            the survey
     * @param exported
     *            the flag exported
     * @return a list of {@code DistOnlineSurveyAnswerModel}
     */
    public List<DistOnlineSurveyAnswerModel> getAnswers(final DistOnlineSurveyModel survey, final boolean exported);

    /**
     * Fetch all answers of the specified survey with the {@code exported} flag set to {@code false}. This method is nothing else then
     * {@code getAnswers(survey, false)}
     * 
     * @param version
     *            the survey version id
     * @return a list of {@code DistOnlineSurveyAnswerModel}
     * @see #getAnswers(DistOnlineSurveyModel, boolean)
     */
    public List<DistOnlineSurveyAnswerModel> getAnswersForExport(final String version);

    /**
     * Fetch all answers of the specified survey with the {@code exported} flag set to {@code false}. This method is nothing else then
     * {@code getAnswers(survey, false)}
     * 
     * @param survey
     *            the survey
     * @return a list of {@code DistOnlineSurveyAnswerModel}
     * @see #getAnswers(DistOnlineSurveyModel, boolean)
     */
    public List<DistOnlineSurveyAnswerModel> getAnswersForExport(final DistOnlineSurveyModel survey);

    /**
     * Fetch the {@code DistOnlineSurveyQuestionModel} having the specified {@code uid}.
     * 
     * @param questionUid
     *            the question uid
     * @return a {@code DistOnlineSurveyQuestionModel}
     */
    public DistOnlineSurveyQuestionModel getSurveyQuestion(final String questionUid);

    /**
     * Fetch all answers for the specified question given by its {@code uid}.
     * 
     * @param questionUid
     *            the question uid.
     * @return a list of {@code DistOnlineSurveyAnswerModel}
     * @see #getAnswersForQuestion(DistOnlineSurveyQuestionModel)
     */
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final String questionUid);

    /**
     * Fetch all answers for the specified question.
     * 
     * @param question
     *            the question.
     * @return a list of {@code DistOnlineSurveyAnswerModel}
     */
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final DistOnlineSurveyQuestionModel question);

    /**
     * Fetch all answers for survey, given by its version id, and the question given by its {@code uid}.
     * 
     * @param version
     *            the survey version.
     * @param questionUid
     *            the question uid.
     * @return a list of {@code DistOnlineSurveyAnswerModel}
     * @see #getAnswersForQuestion(String, DistOnlineSurveyQuestionModel)
     */
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final String version, final String questionUid);

    /**
     * Fetch all answers for the specified question and survey.
     * 
     * @param version
     *            the survey version id.
     * @param question
     *            the question.
     * @return a list of {@code DistOnlineSurveyAnswerModel}
     */
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final String version, final DistOnlineSurveyQuestionModel question);
}
