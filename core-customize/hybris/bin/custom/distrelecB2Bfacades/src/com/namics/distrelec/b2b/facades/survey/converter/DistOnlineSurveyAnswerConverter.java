/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.survey.converter;

import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyAnswerModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyQuestionModel;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyAnswerData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyQuestionData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * {@code DistOnlineSurveyAnswerConverter}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistOnlineSurveyAnswerConverter extends AbstractPopulatingConverter<DistOnlineSurveyAnswerModel, DistOnlineSurveyAnswerData> {

    private Converter<DistOnlineSurveyQuestionModel, DistOnlineSurveyQuestionData> onlineSurveyQuestionConverter;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.commerceservices.converter.impl.AbstractConverter#createTarget()
     */
    @Override
    protected DistOnlineSurveyAnswerData createTarget() {
        return new DistOnlineSurveyAnswerData();
    }

    @Override
    public void populate(final DistOnlineSurveyAnswerModel source, final DistOnlineSurveyAnswerData target) {
        target.setUid(source.getUid());
        target.setLanguage(source.getLanguage());
        target.setExported(source.isExported());
        target.setValue(source.getValue());
        target.setSequenceID(source.getSequenceID());
        target.setQuestion(getOnlineSurveyQuestionConverter().convert(source.getQuestion()));
        // XXX is it needed ?
        // target.setSurvey(survey);
    }

    public Converter<DistOnlineSurveyQuestionModel, DistOnlineSurveyQuestionData> getOnlineSurveyQuestionConverter() {
        return onlineSurveyQuestionConverter;
    }

    public void setOnlineSurveyQuestionConverter(final Converter<DistOnlineSurveyQuestionModel, DistOnlineSurveyQuestionData> onlineSurveyQuestionConverter) {
        this.onlineSurveyQuestionConverter = onlineSurveyQuestionConverter;
    }
}
