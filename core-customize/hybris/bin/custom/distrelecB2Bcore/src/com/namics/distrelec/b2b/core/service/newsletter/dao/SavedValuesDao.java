/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.newsletter.dao;

import java.util.List;

import de.hybris.platform.core.model.ItemModel;

/**
 * Dao to retrieve items which have been recently modified, and the modification was tracked with a SavedValueEntry.
 * 
 *
 * @author <a href="marco.dellanna@distrelec.com">Marco Dell'Anna</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.7
 */
public interface SavedValuesDao {
    
    /**
     * This method retrieves the items of type {@code typeCode}, which are newer than {@code days} days, or whose attribute defined by {@code fieldname} was modified in the last {@code days} days.
     * @param days
     *        amount of days starting from now to go back in time.
     *        
     * @param typeCode
     *        the typeCode of the items we want to retrieve
     * 
     * @param fieldName
     *        name of the attribute that was modified
     * 
     * @return the list of new or modified items.
     */
    public <T extends ItemModel> List<T> findByLastFieldUpdate(final int days, final String typeCode, final String fieldName);

}
