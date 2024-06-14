/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code OnlineSurveyForm}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class OnlineSurveyForm {

    private Map<String, String> questions = new HashMap<String, String>();

    public boolean hasValue(final String key) {
        return StringUtils.isNotBlank(questions.get(key));
    }

    public String getValue(final String key) {
        return hasValue(key) ? questions.get(key) : StringUtils.EMPTY;
    }

    public Map<String, String> getQuestions() {
        return questions;
    }

    public void setQuestions(final Map<String, String> questions) {
        this.questions = questions;
    }
}
