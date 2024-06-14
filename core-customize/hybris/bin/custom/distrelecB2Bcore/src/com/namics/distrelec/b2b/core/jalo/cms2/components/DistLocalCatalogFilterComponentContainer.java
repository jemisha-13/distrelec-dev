package com.namics.distrelec.b2b.core.jalo.cms2.components;

import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.jalo.contents.components.SimpleCMSComponent;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DistLocalCatalogFilterComponentContainer extends GeneratedDistLocalCatalogFilterComponentContainer {

    private static final Logger LOG = Logger.getLogger(DistLocalCatalogFilterComponentContainer.class.getName());

    public static final String CONTENT_CATALOG_SUFFIX = "ContentCatalog";
    public static final String CATALOG_VERSIONS_SESSION_KEY = "catalogversions";

    @Override
    protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes) throws JaloBusinessException {
        // business code placed here will be executed before the item is created
        // then create the item
        final Item item = super.createItem(ctx, type, allAttributes);
        // business code placed here will be executed after the item was created
        // and return the item
        return item;
    }

    @Override
    public List<SimpleCMSComponent> getCurrentCMSComponents(SessionContext sessionContext) {

        Collection<CatalogVersionModel> currentCatalogVersions = getCatalogVersionsFromSession();
        List<CatalogVersionModel> catalogVersions = getActiveChildContentCatalogVersions(currentCatalogVersions);

        if (catalogVersions.isEmpty()) {
            //In case there are no active child catalog versions available page is viewed in Staged country catalog in Smartedit and it should display Staged components for that country
            catalogVersions = getInactiveChildContentCatalogVersions(currentCatalogVersions);
        }

        if (catalogVersions.isEmpty()) {
            //In case there are no child catalog versions page is viewed in International country catalog in Smartedit and it should display Online catalog if applicable
            catalogVersions = getActiveContentCatalogVersions(currentCatalogVersions);
        }

        if (catalogVersions.isEmpty()) {
            //In case there are no child catalog versions page and no active catalog versions page is viewed in International country catalog in Staged version in Smartedit and it should display Staged catalog components
            catalogVersions = getInactiveContentCatalogVersions(currentCatalogVersions);
        }

        if (catalogVersions.isEmpty()) {
            LOG.warn("Cannot find content catalogs in session");
            return Collections.emptyList();
        }

        return getApplicableComponentsInCatalogVersions(catalogVersions);
    }

    private List<SimpleCMSComponent> getApplicableComponentsInCatalogVersions(Collection<CatalogVersionModel> catalogVersions) {
        ModelService modelService = Registry.getApplicationContext().getBean(ModelService.class);

        List<CatalogVersion> catalogVersionsJalo = catalogVersions.stream()
                .map(modelService::<CatalogVersion>getSource)
                .collect(Collectors.toList());

        return getApplicableComponents().stream()
                .filter(component -> catalogVersionsJalo.contains(component.getCatalogVersion()))
                .collect(Collectors.toList());
    }

    private List<CatalogVersionModel> getInactiveContentCatalogVersions(Collection<CatalogVersionModel> catalogVersions) {
        return getContentCatalogVersionsStream(catalogVersions)
                .filter(catalogVersion -> !catalogVersion.getActive())
                .collect(Collectors.toList());
    }

    private List<CatalogVersionModel> getInactiveChildContentCatalogVersions(Collection<CatalogVersionModel> catalogVersions) {
        return getChildContentCatalogVersionsStream(catalogVersions)
                .filter(catalogVersion -> !catalogVersion.getActive())
                .collect(Collectors.toList());
    }

    private List<CatalogVersionModel> getActiveContentCatalogVersions(Collection<CatalogVersionModel> catalogVersions) {
        return getContentCatalogVersionsStream(catalogVersions)
                .filter(CatalogVersionModel::getActive)
                .collect(Collectors.toList());
    }

    private List<CatalogVersionModel> getActiveChildContentCatalogVersions(Collection<CatalogVersionModel> catalogVersions) {
        return getChildContentCatalogVersionsStream(catalogVersions)
                .filter(CatalogVersionModel::getActive)
                .collect(Collectors.toList());
    }

    private Stream<CatalogVersionModel> getChildContentCatalogVersionsStream(Collection<CatalogVersionModel> catalogVersions) {
        return getContentCatalogVersionsStream(catalogVersions)
                .filter(this::isChildContentCatalogVersion);
    }

    private Stream<CatalogVersionModel> getContentCatalogVersionsStream(Collection<CatalogVersionModel> catalogVersions) {
        return catalogVersions.stream()
                .filter(this::isContentCatalogVersion);
    }

    private boolean isContentCatalogVersion(CatalogVersionModel catalogVersion) {
        return ContentCatalogModel.class.isAssignableFrom(catalogVersion.getCatalog().getClass());
    }

    private boolean isChildContentCatalogVersion(CatalogVersionModel catalogVersion) {
        return ((ContentCatalogModel)catalogVersion.getCatalog()).getSuperCatalog() != null;
    }

    private Collection<CatalogVersionModel> getCatalogVersionsFromSession() {
        SessionService sessionService = Registry.getApplicationContext().getBean(SessionService.class);
        return sessionService.getAttribute(CATALOG_VERSIONS_SESSION_KEY);
    }
}
