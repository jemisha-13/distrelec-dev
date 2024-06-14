/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.newsletter.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.newsletter.service.DistSavedValuesService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.enums.SavedValueEntryType;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.hmc.model.SavedValueEntryModel;
import de.hybris.platform.hmc.model.SavedValuesModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

public class DefaultDistSavedValuesService implements DistSavedValuesService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistSavedValuesService.class);

    @Autowired
    private ModelService modelService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private UserService userService;
    
    @Autowired
    FlexibleSearchService flexibleSearchService;

    @Override
    public void logModifiedField(final ItemModel item, String field, boolean newValue, boolean oldValue) {
        if (getModelService().isNew(item)) {
            LOG.debug("Cannot set field " + field + " as modified on item " + item.getClass().getSimpleName() + " because it is not yet persisted");
            return;
        }
        SavedValuesModel savedValues = getModelService().create(SavedValuesModel.class);
        if (item instanceof B2BCustomerModel) {
            try {
                B2BCustomerModel customer = ((B2BCustomerModel) item);
                savedValues.setUser(customer);
            } catch (Exception ex) {
                savedValues.setUser(getUserService().getAnonymousUser());
                LOG.error("Can not get customer model in modified log", ex);
            }
        } else {
            savedValues.setUser(getUserService().getAnonymousUser());
        }

        savedValues.setTimestamp(new Date());
        savedValues.setModifiedItem(item);
        savedValues.setModifiedItemType(getTypeService().getComposedTypeForClass(item.getClass()));
        savedValues.setModificationType(SavedValueEntryType.CHANGED);
        savedValues.setModifiedItemDisplayString(item.getItemtype());

        SavedValueEntryModel savedValueEntry = getModelService().create(SavedValueEntryModel.class);
        savedValueEntry.setModifiedAttribute(field);
        savedValueEntry.setNewValue(newValue);
        savedValueEntry.setOldValue(oldValue);
        savedValueEntry.setOldValueAttributeDescriptor(getTypeService().getAttributeDescriptor(item.getItemtype(), field));

        savedValueEntry.setParent(savedValues);
        getModelService().save(savedValues);
        getModelService().save(savedValueEntry);
    }
    @Override
    public List<SavedValuesModel> getSavedValuesForModifiedItem(final ItemModel item) {
      try {
        String query = "select {sv.pk} from {SavedValues AS sv} where  {sv.modifiedItem}=?itemType order by {sv.modifiedTime} DESC";
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query);        
        flexibleSearchQuery.addQueryParameter("itemType", item);
        SearchResult<SavedValuesModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
        return searchResult.getResult();
      }catch(Exception ex) {
          LOG.error("Error whicle fetching log entries for item:",item.getPk());
      }
      return null;
    }
    @Override
    public List<SavedValuesModel> getSavedValuesForModifiedUser(final ItemModel itemType) {
      try {
        String query = "select {sv.pk} from {SavedValues AS sv} where {sv.user}=?user  order by {sv.modifiedTime} DESC";
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query);    
        flexibleSearchQuery.addQueryParameter("user", itemType);
        SearchResult<SavedValuesModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
        return searchResult.getResult();
      }catch(Exception ex) {
          LOG.error("Error whicle fetching log entries for item:",itemType.getPk());
      }
      return null;
    }
    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public TypeService getTypeService() {
        return typeService;
    }

    public void setTypeService(TypeService typeService) {
        this.typeService = typeService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
