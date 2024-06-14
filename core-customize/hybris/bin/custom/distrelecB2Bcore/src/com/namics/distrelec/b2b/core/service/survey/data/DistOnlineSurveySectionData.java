/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.survey.data;

import java.util.List;

/**
 * {@code DistOnlineSurveySectionData}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistOnlineSurveySectionData {

    private String uid;
    private String title;
    private int position;
    private List<DistOnlineSurveyQuestionData> questions;

    // Getters and Setters

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public List<DistOnlineSurveyQuestionData> getQuestions() {
        return questions;
    }

    public void setQuestions(final List<DistOnlineSurveyQuestionData> questions) {
        this.questions = questions;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(final int position) {
        this.position = position;
    }
}
