package com.namics.distrelec.b2b.facades.seo.impl;

import com.namics.distrelec.b2b.core.model.marketing.DistHeroProductsModel;
import com.namics.distrelec.b2b.core.model.seo.DistMetaDataModel;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.core.service.i18n.dao.DistLanguageDao;
import com.namics.distrelec.b2b.core.service.seo.DistSeoService;
import com.namics.distrelec.b2b.facades.marketing.converters.DistHeroProductsConverter;
import com.namics.distrelec.b2b.facades.marketing.data.DistHeroProductsData;
import com.namics.distrelec.b2b.facades.seo.DistSeoFacade;
import com.namics.distrelec.b2b.facades.seo.converters.DistMetaDataConverter;
import com.namics.distrelec.b2b.facades.seo.data.MetaData;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultDistSeoFacade implements DistSeoFacade {

    @Autowired
    private DistSeoService distSeoService;

    @Autowired
    private DistMetaDataConverter distMetaDataConverter;

    @Autowired
    private NamicsCommonI18NService namicsCommonI18NService;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private DistLanguageDao languageDao;

    @Autowired
    private DistHeroProductsConverter distHeroProductsConverter;

    @Override
    public MetaData getMetaDataForProduct(final ProductModel product) {
        final DistMetaDataModel metaDataModel = distSeoService.getMetaDataForProduct(product, getLanguage(), getCountry());
        if (metaDataModel != null) {
            return distMetaDataConverter.convert(metaDataModel);
        }
        return null;
    }

    @Override
    public MetaData getMetaDataForCategory(final CategoryModel category) {
        final DistMetaDataModel metaDataModel = distSeoService.getMetaDataForCategory(category, getLanguage(), getCountry());
        if (metaDataModel != null) {
            return distMetaDataConverter.convert(metaDataModel);
        }
        return null;
    }

    @Override
    public DistHeroProductsData getActiveHeroProducts() {
        final DistHeroProductsModel heroProductsModel = distSeoService.getActiveHeroProducts();
        if (heroProductsModel != null) {
            final DistHeroProductsData dataObject = distHeroProductsConverter.convert(heroProductsModel);
            return dataObject;
        }
        return null;
    }

    private LanguageModel getLanguage() {
        return languageDao.findLanguagesByCode(storeSessionFacade.getCurrentLanguage().getIsocode()).get(0);
    }

    private CountryModel getCountry() {
        return namicsCommonI18NService.getCurrentCountry();
    }

}
