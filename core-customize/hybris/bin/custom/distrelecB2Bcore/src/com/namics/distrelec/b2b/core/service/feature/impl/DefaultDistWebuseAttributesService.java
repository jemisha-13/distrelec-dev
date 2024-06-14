/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.feature.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.feature.DistWebuseAttributesService;
import com.namics.distrelec.b2b.core.service.feature.dao.DistWebuseAttributesDao;

import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * Default implementatino of {@link DistWebuseAttributesService}.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.20 (DISTRELEC-4581)
 * 
 */
public class DefaultDistWebuseAttributesService implements DistWebuseAttributesService {

    private DistWebuseAttributesDao distWebuseAttributesDao;

    /**
     * which visibilites should be included in the result
     */
    static final List<String> VALID_VISIBILITY_CODES = new ArrayList<String>(3);
    {
        VALID_VISIBILITY_CODES.add(ClassificationAttributeVisibilityEnum.A_VISIBILITY.getCode());
        VALID_VISIBILITY_CODES.add(ClassificationAttributeVisibilityEnum.B_VISIBILITY.getCode());
        VALID_VISIBILITY_CODES.add(ClassificationAttributeVisibilityEnum.C_VISIBILITY.getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getWebuseAttributes(ProductModel product, LanguageModel language) {
        return getDistWebuseAttributesDao().getWebuseAttributes(product, language, VALID_VISIBILITY_CODES);
    }

    // BEGIN GENERATED CODE

    public DistWebuseAttributesDao getDistWebuseAttributesDao() {
        return distWebuseAttributesDao;
    }

    @Autowired
    public void setDistWebuseAttributesDao(DistWebuseAttributesDao distWebuseAttributesDao) {
        this.distWebuseAttributesDao = distWebuseAttributesDao;
    }

    // END GENERATED CODE

}
