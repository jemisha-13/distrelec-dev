/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.survey.data;

import java.util.ArrayList;
import java.util.List;

import com.namics.distrelec.b2b.core.enums.DistOnlineSurveyQuestionType;

/**
 * {@code DistOnlineSurveyQuestionData}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistOnlineSurveyQuestionData {

    private String uid;
    private int position;
    private String name;
    private boolean mandatory;
    private boolean persistentAnswer;
    private DistOnlineSurveyQuestionType type;
    private String value;
    private List<DistPossibleAnswerData> possibleAnswers = new ArrayList<DistPossibleAnswerData>();

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(final int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    public DistOnlineSurveyQuestionType getType() {
        return type;
    }

    public void setType(final DistOnlineSurveyQuestionType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public List<DistPossibleAnswerData> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(final List<DistPossibleAnswerData> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    public boolean isPersistentAnswer() {
        return persistentAnswer;
    }

    public void setPersistentAnswer(final boolean persistentAnswer) {
        this.persistentAnswer = persistentAnswer;
    }
}
