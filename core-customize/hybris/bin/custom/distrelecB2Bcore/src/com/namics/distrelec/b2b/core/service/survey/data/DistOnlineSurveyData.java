/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.survey.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * {@code DistOnlineSurveyData}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistOnlineSurveyData {

    private String uid;
    private String version;
    private String title;
    private Date startDate;
    private Date endDate;
    private String responsible;
    private String thankYouText;
    private String successButtonText;
    private String successButtonAction;
    private List<DistOnlineSurveyAnswerData> answers;
    private List<DistOnlineSurveySectionData> sections;
    private List<String> cmsSites = new ArrayList<String>();

    // Getters & Setters

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(final String responsible) {
        this.responsible = responsible;
    }

    public List<DistOnlineSurveyAnswerData> getAnswers() {
        return answers;
    }

    public void setAnswers(final List<DistOnlineSurveyAnswerData> answers) {
        this.answers = answers;
    }

    public List<DistOnlineSurveySectionData> getSections() {
        return sections;
    }

    public void setSections(final List<DistOnlineSurveySectionData> sections) {
        this.sections = sections;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public List<String> getCmsSites() {
        return cmsSites;
    }

    public void setCmsSites(final List<String> cmsSites) {
        this.cmsSites = cmsSites;
    }

    public String getThankYouText() {
        return thankYouText;
    }

    public void setThankYouText(final String thankYouText) {
        this.thankYouText = thankYouText;
    }

    public String getSuccessButtonText() {
        return successButtonText;
    }

    public void setSuccessButtonText(final String successButtonText) {
        this.successButtonText = successButtonText;
    }

    public String getSuccessButtonAction() {
        return successButtonAction;
    }

    public void setSuccessButtonAction(final String successButtonAction) {
        this.successButtonAction = successButtonAction;
    }
}
