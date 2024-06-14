/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.i18n.dao;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.daos.LanguageDao;

/**
 * Interface for language DAO.
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public interface DistLanguageDao extends LanguageDao {
    /**
     * find a language model for a 3 character isocode pim
     * 
     * @param isocodePim
     * @return the language model
     */
    LanguageModel findLanguageByIsocodePim(final String isocodePim);
}
