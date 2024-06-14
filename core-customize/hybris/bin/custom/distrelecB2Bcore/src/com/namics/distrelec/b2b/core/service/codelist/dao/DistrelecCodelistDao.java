/*
 * Copyright 2000-2009 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.dao;

import java.util.List;

import com.namics.distrelec.b2b.core.model.DistCodelistModel;
import com.namics.distrelec.b2b.core.service.exception.NotFoundException;

import de.hybris.platform.servicelayer.internal.dao.Dao;

/**
 * Interface for Distrelec Codelist DAO.
 * 
 * @author daehusir, Distrelec
 */
public interface DistrelecCodelistDao extends Dao {

    /**
     * Returns <code>DistCodelistModel</code> for the given code and for the given DistCodelistModel type.
     * 
     * @param code
     *            code
     * @param codeListType
     *            type of the code list
     * @throws NotFoundException
     *             if no DistCodelistModel with the specified code and class type is found or more than one DistCodelistModel with the
     *             specified code and class type is found
     * @return a model instance of the given list type, a sub type of DistCodelistModel
     */
    DistCodelistModel getDistrelecCodelistEntry(final String code, final String codeListType) throws NotFoundException;

    /**
     * Returns all entries of a <code>DistCodelistModel</code> for the given DistCodelistModel type.
     * 
     * @param codeListType
     *            type of the code list
     * @return a list of DistCodelistModel for the given DistCodelistModel type
     */
    List<? extends DistCodelistModel> getAllCodelistEntries(final String codeListType);

    /**
     * Inserts or updates the given code list entry.
     * 
     * @param codelistEntry
     *            code list entry
     */
    void insertOrUpdateDistrelecCodelistEntry(final DistCodelistModel codelistEntry);

    /**
     * Inserts or updates the given list of code list entries.
     * 
     * @param codelistEntryList
     *            al list of code list enries
     */
    void insertOrUpdateDistrelecCodelistEntry(final List<? extends DistCodelistModel> codelistEntryList);
}
