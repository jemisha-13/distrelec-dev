/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.survey.converter;

import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyQuestionModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveySectionModel;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyQuestionData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveySectionData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.Comparator;

/**
 * {@code DistOnlineSurveySectionConverter}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistOnlineSurveySectionConverter extends AbstractPopulatingConverter<DistOnlineSurveySectionModel, DistOnlineSurveySectionData> {

    private Converter<DistOnlineSurveyQuestionModel, DistOnlineSurveyQuestionData> onlineSurveyQuestionConverter;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.commerceservices.converter.impl.AbstractConverter#createTarget()
     */
    @Override
    protected DistOnlineSurveySectionData createTarget() {
        return new DistOnlineSurveySectionData();
    }

    @Override
    public void populate(final DistOnlineSurveySectionModel source, final DistOnlineSurveySectionData target) {
        target.setUid(source.getUid());
        target.setTitle(source.getTitle());
        target.setPosition(source.getPosition());
        target.setQuestions(Converters.convertAll(source.getQuestions(), getOnlineSurveyQuestionConverter()));
        Collections.sort(target.getQuestions(), new Comparator<DistOnlineSurveyQuestionData>() {

            @Override
            public int compare(final DistOnlineSurveyQuestionData o1, final DistOnlineSurveyQuestionData o2) {
                return o1.getPosition() - o2.getPosition();
            }
        });
    }

    public Converter<DistOnlineSurveyQuestionModel, DistOnlineSurveyQuestionData> getOnlineSurveyQuestionConverter() {
        return onlineSurveyQuestionConverter;
    }

    public void setOnlineSurveyQuestionConverter(final Converter<DistOnlineSurveyQuestionModel, DistOnlineSurveyQuestionData> onlineSurveyQuestionConverter) {
        this.onlineSurveyQuestionConverter = onlineSurveyQuestionConverter;
    }
}
