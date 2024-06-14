package com.distrelec.smartedit.webservices.facades;

import com.distrelec.smartedit.webservices.strategy.DistrelecBaseLocaleStrategy;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmssmarteditwebservices.data.CategoryData;
import de.hybris.platform.cmssmarteditwebservices.data.ProductData;
import de.hybris.platform.cmssmarteditwebservices.products.facade.impl.DefaultProductSearchFacade;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public class DistrelecDefaultProductSearchFacade extends DefaultProductSearchFacade {

    @Autowired
    private I18NService i18NService;

    @Autowired
    private DistrelecBaseLocaleStrategy distrelecBaseLocaleStrategy;

    @Override
    public SearchResult<ProductData> findProducts(String text, PageableData pageableData) {
        Locale initial = distrelecBaseLocaleStrategy.setLocaleToBase(i18NService.getCurrentLocale());
        SearchResult<ProductData> results =  super.findProducts(text, pageableData);
        distrelecBaseLocaleStrategy.revertLocale(initial);
        return results;
    }

    @Override
    public SearchResult<CategoryData> findProductCategories(String text, PageableData pageableData) {
        Locale initial = distrelecBaseLocaleStrategy.setLocaleToBase(i18NService.getCurrentLocale());
        SearchResult<CategoryData> results = super.findProductCategories(text, pageableData);
        distrelecBaseLocaleStrategy.revertLocale(initial);
        return results;
    }
}
