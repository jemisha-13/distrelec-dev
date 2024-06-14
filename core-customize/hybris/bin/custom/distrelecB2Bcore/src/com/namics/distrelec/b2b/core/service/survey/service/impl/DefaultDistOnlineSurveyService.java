/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.survey.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyAnswerModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyQuestionModel;
import com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyAnswerData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyData;
import com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService;

import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

/**
 * {@code DefaultDistOnlineSurveyService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DefaultDistOnlineSurveyService implements DistOnlineSurveyService {

    @Autowired
    private DistOnlineSurveyDao onlineSurveyDao;

    private KeyGenerator sequenceIDGenerator;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#create(com.namics.distrelec.b2b.core.service.survey.
     * data.DistOnlineSurveyData)
     */
    @Override
    public void create(final DistOnlineSurveyData onlineSurveyData) {
        getOnlineSurveyDao().create(onlineSurveyData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#create(com.namics.distrelec.b2b.core.service.survey.
     * data.DistOnlineSurveyAnswerData)
     */
    @Override
    public void create(final DistOnlineSurveyAnswerData onlineSurveyAnswerData) {
        create(onlineSurveyAnswerData, ((Long) getSequenceIDGenerator().generate()).longValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#create(java.util.List)
     */
    @Override
    public void create(final List<DistOnlineSurveyAnswerData> surveyAnswers) {
        final long sequenceID = Long.valueOf(getSequenceIDGenerator().generate().toString()).longValue();
        for (final DistOnlineSurveyAnswerData answer : surveyAnswers) {
            create(answer, sequenceID);
        }
    }

    /**
     * Set the sequence ID to the source object and create and persist {@code DistOnlineSurveyAnswerModel} from the
     * {@code DistOnlineSurveyAnswerData}
     * 
     * @param onlineSurveyAnswerData
     * @param sequenceID
     */
    private void create(final DistOnlineSurveyAnswerData onlineSurveyAnswerData, final long sequenceID) {
        onlineSurveyAnswerData.setSequenceID(sequenceID);
        getOnlineSurveyDao().create(onlineSurveyAnswerData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#findByVersion(java.lang.String)
     */
    @Override
    public DistOnlineSurveyModel findByVersion(final String version) {
        return getOnlineSurveyDao().findByVersion(version);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#findByVersion(java.lang.String, java.util.Date)
     */
    @Override
    public DistOnlineSurveyModel findByVersion(final String version, final Date date) {
        return getOnlineSurveyDao().findByVersion(version, date);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#findActiveOnDate(java.util.Date)
     */
    @Override
    public List<DistOnlineSurveyModel> findActiveOnDate(final Date date) {
        return getOnlineSurveyDao().findActiveOnDate(date);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#getAnswers(java.lang.String)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswers(final String version) {
        return getOnlineSurveyDao().getAnswers(version);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#getAnswers(com.namics.distrelec.b2b.core.model.feedback
     * .DistOnlineSurveyModel)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswers(final DistOnlineSurveyModel survey) {
        return getOnlineSurveyDao().getAnswers(survey);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#getAnswers(java.lang.String, boolean)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswers(final String version, final boolean exported) {
        return getAnswers(findByVersion(version), exported);
    }

    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswers(final DistOnlineSurveyModel survey, final boolean exported) {
        return getOnlineSurveyDao().getAnswers(survey, exported);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#getAnswersForExport(java.lang.String)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswersForExport(final String version) {
        return getAnswersForExport(findByVersion(version));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#getAnswersForExport(com.namics.distrelec.b2b.core.model
     * .feedback.DistOnlineSurveyModel)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswersForExport(final DistOnlineSurveyModel survey) {
        return getAnswers(survey, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#getSurveyQuestion(java.lang.String)
     */
    @Override
    public DistOnlineSurveyQuestionModel getSurveyQuestion(final String uid) {
        return getOnlineSurveyDao().getSurveyQuestion(uid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#getAnswersForQuestion(java.lang.String)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final String uid) {
        return getOnlineSurveyDao().getAnswersForQuestion(uid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#getAnswersForQuestion(com.namics.distrelec.b2b.core.
     * model.feedback.DistOnlineSurveyQuestionModel)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final DistOnlineSurveyQuestionModel question) {
        return getOnlineSurveyDao().getAnswersForQuestion(question);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#getAnswersForQuestion(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final String version, final String uid) {
        return getOnlineSurveyDao().getAnswersForQuestion(version, uid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.service.DistOnlineSurveyService#getAnswersForQuestion(java.lang.String,
     * com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyQuestionModel)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final String version, final DistOnlineSurveyQuestionModel question) {
        return getOnlineSurveyDao().getAnswersForQuestion(version, question);
    }

    public DistOnlineSurveyDao getOnlineSurveyDao() {
        return onlineSurveyDao;
    }

    public void setOnlineSurveyDao(final DistOnlineSurveyDao onlineSurveyDao) {
        this.onlineSurveyDao = onlineSurveyDao;
    }

    public KeyGenerator getSequenceIDGenerator() {
        return sequenceIDGenerator;
    }

    public void setSequenceIDGenerator(final KeyGenerator sequenceIDGenerator) {
        this.sequenceIDGenerator = sequenceIDGenerator;
    }
}
