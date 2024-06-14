/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.model;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.AbstractDynamicLocalizedAttributeHandler;
import com.namics.distrelec.b2b.core.model.DistCodelistErpModel;
import com.namics.distrelec.b2b.core.model.DistCodelistModel;

import de.hybris.platform.servicelayer.i18n.I18NService;

/**
 * Returns the name of the code list. If the code list is an instance of {@link DistCodelistErpModel} and the name of this code list is
 * empty, the name of the code list in the ERP will be returned.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DistCodeListRelevantNameAttributeHandler extends AbstractDynamicLocalizedAttributeHandler<String, DistCodelistModel> {

    @Autowired
    private I18NService i18NService;

    @Override
    public String get(final DistCodelistModel distCodelist) {
        if (distCodelist == null) {
            throw new IllegalArgumentException("Item model is required");
        }

        if (StringUtils.isEmpty(distCodelist.getName()) && distCodelist instanceof DistCodelistErpModel) {
            return ((DistCodelistErpModel) distCodelist).getNameErp();
        }

        return distCodelist.getName();
    }

    @Override
    public String get(final DistCodelistModel distCodelist, final Locale loc) {
        if (distCodelist == null) {
            throw new IllegalArgumentException("Item model is required");
        }

        if (StringUtils.isEmpty(distCodelist.getName(loc)) && distCodelist instanceof DistCodelistErpModel) {
            return ((DistCodelistErpModel) distCodelist).getNameErp(loc);
        }

        return distCodelist.getName(loc);
    }

    public I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(final I18NService i18nService) {
        i18NService = i18nService;
    }
}
