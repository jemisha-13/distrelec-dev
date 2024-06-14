/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.survey.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyAnswerModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyQuestionModel;
import com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyAnswerData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyData;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * {@code DefaultDistOnlineSurveyDao}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DefaultDistOnlineSurveyDao extends AbstractItemDao implements DistOnlineSurveyDao {

    /**
     * Find survey by its version flexible search query
     */
    private final String FIND_SURVEY_BY_VERSION = "SELECT {pk} FROM {" + DistOnlineSurveyModel._TYPECODE + "} WHERE {" + DistOnlineSurveyModel.VERSION + "}=?"
            + DistOnlineSurveyModel.VERSION;

    /**
     * Find survey by its version and date flexible search query
     */
    private final String FIND_SURVEY_BY_VERSION_AND_DATE = FIND_SURVEY_BY_VERSION + " AND {" + DistOnlineSurveyModel.STARTDATE + "} <= ?sDate and {"
            + DistOnlineSurveyModel.ENDDATE + "} >= ?eDate";

    /**
     * Find active survey on date flexible search query
     */
    private final String FIND_ACTIVE_SURVEY_ON_DATE = "SELECT {pk} FROM {" + DistOnlineSurveyModel._TYPECODE + "} WHERE {" + DistOnlineSurveyModel.STARTDATE
            + "} <= ?sDate and {" + DistOnlineSurveyModel.ENDDATE + "} >= ?eDate";

    /**
     * ORDER BY close for the survey answers queries.
     */
    private static final String SERVEY_ANSWERS_ORDER = " ORDER BY {" + DistOnlineSurveyAnswerModel.SEQUENCEID + "}";

    /**
     * Find survey answers for survey flexible search query
     */
    private static final String FIND_ANSWERS_FOR_SERVEY = "SELECT {pk} FROM {" + DistOnlineSurveyAnswerModel._TYPECODE + "} WHERE {"
            + DistOnlineSurveyAnswerModel.SURVEY + "}=?" + DistOnlineSurveyAnswerModel.SURVEY + SERVEY_ANSWERS_ORDER;

    /**
     * Find survey answers which are exported or not depending on the boolean flag value.
     */
    private static final String FIND_ANSWERS_FOR_SERVEY_EXPORT = "SELECT {pk} FROM {" + DistOnlineSurveyAnswerModel._TYPECODE + "} WHERE {"
            + DistOnlineSurveyAnswerModel.SURVEY + "}=?" + DistOnlineSurveyAnswerModel.SURVEY + " AND {" + DistOnlineSurveyAnswerModel.EXPORTED + "}=?"
            + DistOnlineSurveyAnswerModel.EXPORTED + SERVEY_ANSWERS_ORDER;

    /**
     * Find survey answers for question flexible search query
     */
    private static final String FIND_ANSWERS_FOR_QUESTION = "SELECT {pk} FROM {" + DistOnlineSurveyAnswerModel._TYPECODE + "} WHERE {"
            + DistOnlineSurveyAnswerModel.QUESTION + "}=?" + DistOnlineSurveyAnswerModel.QUESTION + SERVEY_ANSWERS_ORDER;

    /**
     * Find survey answers for survey and question flexible search query
     */
    private static final String FIND_ANSWERS_FOR_SERVEY_AND_QUESTION = FIND_ANSWERS_FOR_SERVEY + " AND {" + DistOnlineSurveyAnswerModel.QUESTION + "}=?"
            + DistOnlineSurveyAnswerModel.QUESTION + SERVEY_ANSWERS_ORDER;

    /**
     * Find survey question by its UID flexible search query
     */
    private final String FIND_SURVEY_QUESTION_BY_UID = "SELECT {pk} FROM {" + DistOnlineSurveyQuestionModel._TYPECODE + "} WHERE {"
            + DistOnlineSurveyQuestionModel.UID + "}=?" + DistOnlineSurveyQuestionModel.UID;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#create(com.namics.distrelec.b2b.core.service.survey.data.
     * DistOnlineSurveyData)
     */
    @Override
    public void create(final DistOnlineSurveyData surveyData) {
        final DistOnlineSurveyModel model = getModelService().create(DistOnlineSurveyModel.class);
        model.setUid(UUID.randomUUID().toString());
        model.setVersion(surveyData.getVersion());
        model.setTitle(surveyData.getTitle());
        model.setStartDate(surveyData.getStartDate());
        model.setEndDate(surveyData.getEndDate());
        model.setResponsible(surveyData.getResponsible());
        getModelService().save(model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#create(com.namics.distrelec.b2b.core.service.survey.data.
     * DistOnlineSurveyAnswerData)
     */
    @Override
    public void create(final DistOnlineSurveyAnswerData surveyAnswerData) {
        final DistOnlineSurveyQuestionModel question = getSurveyQuestion(surveyAnswerData.getQuestion().getUid());
        if (question.isPersistentAnswer()) {
            final DistOnlineSurveyAnswerModel model = getModelService().create(DistOnlineSurveyAnswerModel.class);
            model.setUid(UUID.randomUUID().toString());
            model.setExported(false);
            model.setLanguage(surveyAnswerData.getLanguage());
            model.setValue(surveyAnswerData.getValue());
            model.setSequenceID(surveyAnswerData.getSequenceID());
            // Setting the parent survey
            model.setSurvey(findByVersion(surveyAnswerData.getSurvey().getVersion()));
            // Setting the target question
            model.setQuestion(question);
            // Save the model
            getModelService().save(model);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#create(java.util.List)
     */
    @Override
    public void create(final List<DistOnlineSurveyAnswerData> surveyAnswers) {
        for (final DistOnlineSurveyAnswerData answer : surveyAnswers) {
            create(answer);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#findByVersion(java.lang.String)
     */
    @Override
    public DistOnlineSurveyModel findByVersion(final String version) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_SURVEY_BY_VERSION);
        searchQuery.addQueryParameter(DistOnlineSurveyModel.VERSION, version);
        return getFlexibleSearchService().<DistOnlineSurveyModel> searchUnique(searchQuery);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#findByVersion(java.lang.String, java.util.Date)
     */
    @Override
    public DistOnlineSurveyModel findByVersion(final String version, final Date date) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_SURVEY_BY_VERSION_AND_DATE);
        searchQuery.addQueryParameter(DistOnlineSurveyModel.VERSION, version);
        searchQuery.addQueryParameter("sDate", date);
        searchQuery.addQueryParameter("eDate", date);
        return getFlexibleSearchService().<DistOnlineSurveyModel> searchUnique(searchQuery);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#findActiveOnDate(java.util.Date)
     */
    @Override
    public List<DistOnlineSurveyModel> findActiveOnDate(final Date date) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_ACTIVE_SURVEY_ON_DATE);
        searchQuery.addQueryParameter("sDate", date);
        searchQuery.addQueryParameter("eDate", date);
        return getFlexibleSearchService().<DistOnlineSurveyModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#getAnswers(java.lang.String)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswers(final String version) {
        return getAnswers(findByVersion(version));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#getAnswers(com.namics.distrelec.b2b.core.model.feedback.
     * DistOnlineSurveyModel)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswers(final DistOnlineSurveyModel survey) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_ANSWERS_FOR_SERVEY);
        searchQuery.addQueryParameter(DistOnlineSurveyAnswerModel.SURVEY, survey);
        return getFlexibleSearchService().<DistOnlineSurveyAnswerModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#getAnswers(com.namics.distrelec.b2b.core.model.feedback.
     * DistOnlineSurveyModel, boolean)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswers(final DistOnlineSurveyModel survey, final boolean exported) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_ANSWERS_FOR_SERVEY_EXPORT);
        searchQuery.addQueryParameter(DistOnlineSurveyAnswerModel.SURVEY, survey);
        searchQuery.addQueryParameter(DistOnlineSurveyAnswerModel.EXPORTED, exported ? "1" : "0");

        return getFlexibleSearchService().<DistOnlineSurveyAnswerModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#getSurveyQuestion(java.lang.String)
     */
    @Override
    public DistOnlineSurveyQuestionModel getSurveyQuestion(final String uid) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_SURVEY_QUESTION_BY_UID);
        searchQuery.addQueryParameter(DistOnlineSurveyQuestionModel.UID, uid);
        return getFlexibleSearchService().<DistOnlineSurveyQuestionModel> searchUnique(searchQuery);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#getAnswersForQuestion(java.lang.String)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final String uid) {
        return getAnswersForQuestion(getSurveyQuestion(uid));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#getAnswersForQuestion(com.namics.distrelec.b2b.core.model.feedback
     * .DistOnlineSurveyQuestionModel)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final DistOnlineSurveyQuestionModel question) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_ANSWERS_FOR_QUESTION);
        searchQuery.addQueryParameter(DistOnlineSurveyAnswerModel.QUESTION, question);
        return getFlexibleSearchService().<DistOnlineSurveyAnswerModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#getAnswersForQuestion(java.lang.String, java.lang.String)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final String version, final String uid) {
        return getAnswersForQuestion(version, getSurveyQuestion(uid));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.survey.dao.DistOnlineSurveyDao#getAnswersForQuestion(java.lang.String,
     * com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyQuestionModel)
     */
    @Override
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final String version, final DistOnlineSurveyQuestionModel question) {
        return getAnswersForQuestion(findByVersion(version), question);
    }

    /**
     * Fetch all answers attached to the specified survey and question.
     * 
     * @param survey
     *            the survey
     * @param question
     *            the question
     * @return a list of {@code DistOnlineSurveyAnswerModel}
     */
    public List<DistOnlineSurveyAnswerModel> getAnswersForQuestion(final DistOnlineSurveyModel survey, final DistOnlineSurveyQuestionModel question) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_ANSWERS_FOR_SERVEY_AND_QUESTION);
        searchQuery.addQueryParameter(DistOnlineSurveyAnswerModel.SURVEY, survey);
        searchQuery.addQueryParameter(DistOnlineSurveyAnswerModel.QUESTION, question);
        return getFlexibleSearchService().<DistOnlineSurveyAnswerModel> search(searchQuery).getResult();
    }
}
