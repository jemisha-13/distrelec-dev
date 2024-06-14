/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.survey.data;

/**
 * {@code DistOnlineSurveyAnswerData}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistOnlineSurveyAnswerData {

    private String uid;
    private String language;
    private boolean exported;
    private String value;
    private long sequenceID;
    private DistOnlineSurveyData survey;
    private DistOnlineSurveyQuestionData question;

    // Getters & Setters

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public boolean isExported() {
        return exported;
    }

    public void setExported(final boolean exported) {
        this.exported = exported;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public DistOnlineSurveyData getSurvey() {
        return survey;
    }

    public void setSurvey(final DistOnlineSurveyData survey) {
        this.survey = survey;
    }

    public DistOnlineSurveyQuestionData getQuestion() {
        return question;
    }

    public void setQuestion(final DistOnlineSurveyQuestionData question) {
        this.question = question;
    }

    public long getSequenceID() {
        return sequenceID;
    }

    public void setSequenceID(final long sequenceID) {
        this.sequenceID = sequenceID;
    }
}
