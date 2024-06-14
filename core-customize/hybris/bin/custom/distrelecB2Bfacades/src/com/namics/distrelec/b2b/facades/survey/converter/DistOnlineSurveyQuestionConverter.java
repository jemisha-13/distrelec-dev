/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.survey.converter;

import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyPossibleAnswerModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyQuestionModel;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyQuestionData;
import com.namics.distrelec.b2b.core.service.survey.data.DistPossibleAnswerData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * {@code DistOnlineSurveyQuestionConverter}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistOnlineSurveyQuestionConverter extends AbstractPopulatingConverter<DistOnlineSurveyQuestionModel, DistOnlineSurveyQuestionData> {

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.commerceservices.converter.impl.AbstractConverter#createTarget()
     */
    @Override
    protected DistOnlineSurveyQuestionData createTarget() {
        return new DistOnlineSurveyQuestionData();
    }

    @Override
    public void populate(final DistOnlineSurveyQuestionModel source, final DistOnlineSurveyQuestionData target) {
        target.setUid(source.getUid());
        target.setPosition(source.getPosition());
        target.setName(source.getName());
        target.setMandatory(source.isMandatory());
        target.setType(source.getType());
        target.setValue(source.getValue());
        target.setPersistentAnswer(source.isPersistentAnswer());
        if (CollectionUtils.isNotEmpty(source.getPossibleAnswers())) {
            for (final DistOnlineSurveyPossibleAnswerModel ospa : source.getPossibleAnswers()) {
                if (StringUtils.isNotBlank(ospa.getValue())) {
                    target.getPossibleAnswers().add(new DistPossibleAnswerData(ospa.getValue(), ospa.getValue()));
                }
            }
        }
    }
}
