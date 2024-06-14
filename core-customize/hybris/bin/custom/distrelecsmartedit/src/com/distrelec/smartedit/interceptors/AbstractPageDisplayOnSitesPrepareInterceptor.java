package com.distrelec.smartedit.interceptors;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import static java.util.Collections.emptyList;

public class AbstractPageDisplayOnSitesPrepareInterceptor implements PrepareInterceptor<AbstractPageModel> {

    @Override
    public void onPrepare(AbstractPageModel page, InterceptorContext interceptorContext) throws InterceptorException {
        CatalogModel catalog = page.getCatalogVersion().getCatalog();

        if (catalog instanceof ContentCatalogModel) {
            ContentCatalogModel contentCatalog = (ContentCatalogModel) catalog;
            if (contentCatalog.getSuperCatalog() != null) {
                page.setDisplayOnSites(emptyList());
            }
        }
    }
}
