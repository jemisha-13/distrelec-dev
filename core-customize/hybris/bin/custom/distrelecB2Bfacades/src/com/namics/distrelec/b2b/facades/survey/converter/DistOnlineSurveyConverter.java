/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.survey.converter;

import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveyModel;
import com.namics.distrelec.b2b.core.model.feedback.DistOnlineSurveySectionModel;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveySectionData;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.Comparator;

/**
 * {@code DistOnlineSurveyConverter}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistOnlineSurveyConverter extends AbstractPopulatingConverter<DistOnlineSurveyModel, DistOnlineSurveyData> {

    private Converter<DistOnlineSurveySectionModel, DistOnlineSurveySectionData> onlineSurveySectionConverter;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.commerceservices.converter.impl.AbstractConverter#createTarget()
     */
    @Override
    protected DistOnlineSurveyData createTarget() {
        return new DistOnlineSurveyData();
    }

    @Override
    public void populate(final DistOnlineSurveyModel source, final DistOnlineSurveyData target) {
        target.setUid(source.getUid());
        target.setVersion(source.getVersion());
        target.setTitle(source.getTitle());
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
        target.setResponsible(source.getResponsible());
        target.setThankYouText(source.getThankYouText());
        target.setSuccessButtonText(source.getSuccessButtonText());
        target.setSuccessButtonAction(source.getSuccessButtonAction());
        // Add the sections of the survey
        target.setSections(Converters.convertAll(source.getSections(), getOnlineSurveySectionConverter()));
        // Sorting the sections by their positions
        Collections.sort(target.getSections(), new Comparator<DistOnlineSurveySectionData>() {

            @Override
            public int compare(final DistOnlineSurveySectionData o1, final DistOnlineSurveySectionData o2) {
                return o1.getPosition() - o2.getPosition();
            }
        });
        // Adding the CMS sites
        if (source.getCmsSites() != null && !source.getCmsSites().isEmpty()) {
            for (final CMSSiteModel site : source.getCmsSites()) {
                target.getCmsSites().add(site.getUid());
            }
        }
    }

    public Converter<DistOnlineSurveySectionModel, DistOnlineSurveySectionData> getOnlineSurveySectionConverter() {
        return onlineSurveySectionConverter;
    }

    public void setOnlineSurveySectionConverter(final Converter<DistOnlineSurveySectionModel, DistOnlineSurveySectionData> onlineSurveySectionConverter) {
        this.onlineSurveySectionConverter = onlineSurveySectionConverter;
    }
}
