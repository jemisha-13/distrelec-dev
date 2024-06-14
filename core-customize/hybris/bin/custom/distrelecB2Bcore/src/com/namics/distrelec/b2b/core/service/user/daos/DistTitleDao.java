/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.user.daos;

import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.user.daos.TitleDao;

public interface DistTitleDao extends TitleDao {

    /**
     * Finds the title by the given sapCode.
     * 
     * @return <code>null</code> if no {@link TitleModel} by the given <code>sapCode</code> was found.
     * @param sapCode
     *            the SAP code of the title (example dr.)
     * 
     * @throws de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException
     *             if more than one titles were found for the given code.
     */
    TitleModel findTitleBySapCode(String sapCode);

}
