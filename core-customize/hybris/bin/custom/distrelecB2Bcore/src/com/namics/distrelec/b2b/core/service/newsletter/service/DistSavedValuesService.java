/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.newsletter.service;

import java.util.List;
import java.util.Set;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.hmc.model.SavedValueEntryModel;
import de.hybris.platform.hmc.model.SavedValuesModel;

/**
 * {@code DistSavedValuesService}
 * 
 * <p>
 * This class handles creation of {@link de.hybris.platform.hmc.model.SavedValuesModel} when the modification time of a field on an
 * {@code ItemModel} has to be tracked.
 * </p>
 * 
 * <a href="marco.dellanna@distrelec.com">Marco Dell'Anna</a>, Sly GmbH
 * 
 * @since Distrelec 6.7
 *
 */
public interface DistSavedValuesService {

    /**
     * Creates a {@link de.hybris.platform.hmc.model.SavedValuesModel} and its inner
     * {@link de.hybris.platform.hmc.model.SavedValueEntryModel} when the {@code field} attribute of {@code item} item was changed
     * 
     * @param item
     *        the {@link ItemModel} that was modified
     * 
     * @param field
     *        the attribute that was modified on {@code item}
     * 
     * @param newValue
     *        the new value that is being set
     * 
     * @param oldValue
     *        the old value that is being overwritten
     */
    public void logModifiedField(final ItemModel item, String field, boolean newValue, boolean oldValue);
    /**
     * fetch modification log for item type for item type
     * @param item
     * @return Set<SavedValueEntryModel>
     */
    public List<SavedValuesModel> getSavedValuesForModifiedItem(final ItemModel item) ;
    /**
     * fetch modification log for item type for search type
     * @param item
     * @param changedItemType
     * @return Set<SavedValueEntryModel>
     */
    public List<SavedValuesModel> getSavedValuesForModifiedUser(final ItemModel user) ;
}
