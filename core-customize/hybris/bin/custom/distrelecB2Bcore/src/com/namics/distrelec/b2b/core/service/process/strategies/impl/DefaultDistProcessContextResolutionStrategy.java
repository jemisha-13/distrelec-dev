/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.process.strategies.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.PaymentNotifyProcessModel;
import com.namics.distrelec.b2b.core.model.process.SystemProcessModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorservices.process.strategies.impl.B2BAcceleratorProcessContextStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.jalo.c2l.LocalizableItem;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Process context resolution strategy for handling special system emails
 *
 * @author rhusi, Distrelec
 * @since Distrelec 2.0
 */
public class DefaultDistProcessContextResolutionStrategy extends B2BAcceleratorProcessContextStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistProcessContextResolutionStrategy.class);

    private String defaultCatalog;
    private String defaultLanguage;
    private List<String> actionsUsingDefaultCatalog = new ArrayList<>();
    private List<String> actionsUsingDefaultLanguage = new ArrayList<>();

    @Override
    public void initializeContext(final BusinessProcessModel businessProcessModel) {
        if (hasActionUsingDefaultLanguage(businessProcessModel)) {
            try {
                getCommonI18NService().setCurrentLanguage(getCommonI18NService().getLanguage(getDefaultLanguage()));
                getSessionService().setAttribute(LocalizableItem.LANGUAGE_FALLBACK_ENABLED, isEnableLanguageFallback());
                getSessionService().setAttribute(AbstractItemModel.LANGUAGE_FALLBACK_ENABLED_SERVICE_LAYER, isEnableLanguageFallback());
            } catch (final Exception e) {
                LOG.error("Failed to initialize session context", e);
            }
        } else {
            super.initializeContext(businessProcessModel);
        }
    }

    @Override
    public CatalogVersionModel getContentCatalogVersion(final BusinessProcessModel businessProcessModel) {
        if (hasActionUsingDefaultLanguage(businessProcessModel)) {
            initializeContext(businessProcessModel);
            return getCatalogVersionService().getCatalogVersion(getDefaultCatalog(), DistConstants.CatalogVersion.ONLINE);
        }

        final BaseSiteModel baseSite = getCmsSite(businessProcessModel);
        if (baseSite instanceof CMSSiteModel) {
            final List<ContentCatalogModel> contentCatalogs = ((CMSSiteModel) baseSite).getContentCatalogs();
            if (CollectionUtils.isNotEmpty(contentCatalogs)) {
                final ContentCatalogModel contentCatalogFiltered = contentCatalogs.stream().filter(catalog -> null != catalog.getSuperCatalog()).findAny().orElse(contentCatalogs.get(0));
                return getCatalogVersionService().getSessionCatalogVersionForCatalog(contentCatalogFiltered.getId()); // Shouldn't be more than one
            }
        }

        return super.getContentCatalogVersion(businessProcessModel);
    }

    @Override
    public BaseSiteModel getCmsSite(BusinessProcessModel businessProcess) {
        BaseSiteModel cmsSite = super.getCmsSite(businessProcess);
        if (cmsSite == null && businessProcess instanceof StoreFrontCustomerProcessModel) {
            StoreFrontCustomerProcessModel storefrontCustomerProcessModel = (StoreFrontCustomerProcessModel) businessProcess;
            return storefrontCustomerProcessModel.getSite();
        } else if (businessProcess instanceof PaymentNotifyProcessModel && businessProcess.getUser() instanceof B2BCustomerModel) {
            final B2BCustomerModel customer = (B2BCustomerModel) businessProcess.getUser();
            return customer.getCustomersBaseSite();
        }
        return cmsSite;
    }

    protected boolean hasActionUsingDefaultLanguage(BusinessProcessModel businessProcessModel) {
        boolean isSystemProcessModel = businessProcessModel instanceof SystemProcessModel;
        if (isSystemProcessModel) {
            return businessProcessModel.getCurrentTasks()
                    .stream()
                    .map(ProcessTaskModel::getAction)
                    .anyMatch(getActionsUsingDefaultLanguage()::contains);
        }
        return false;
    }

    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<String> getActionsUsingDefaultCatalog() {
        return actionsUsingDefaultCatalog;
    }

    public void setActionsUsingDefaultCatalog(List<String> actionsUsingDefaultCatalog) {
        this.actionsUsingDefaultCatalog = actionsUsingDefaultCatalog;
    }

    public List<String> getActionsUsingDefaultLanguage() {
        return actionsUsingDefaultLanguage;
    }

    public void setActionsUsingDefaultLanguage(List<String> actionsUsingDefaultLanguage) {
        this.actionsUsingDefaultLanguage = actionsUsingDefaultLanguage;
    }

}
