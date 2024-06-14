package com.namics.distrelec.b2b.core.service.seo.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.marketing.DistHeroProductsModel;
import com.namics.distrelec.b2b.core.model.seo.DistMetaDataModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.seo.DistSeoService;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

public class DefaultDistSeoService implements DistSeoService {

    private static final Logger LOG = Logger.getLogger(DefaultDistSeoService.class);

    @Autowired
    private ModelService modelService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Override
    public DistMetaDataModel getMetaDataForProduct(final ProductModel product, final LanguageModel language, final CountryModel country) {
        DistMetaDataModel example = createDistMetaDataModel(language, country);
        example.setProduct(product);
        return getDistMetaDataByExample(example);
    }

    @Override
    public DistMetaDataModel getMetaDataForCategory(final CategoryModel category, final LanguageModel language, final CountryModel country) {
        DistMetaDataModel example = createDistMetaDataModel(language, country);
        example.setCategory(category);
        return getDistMetaDataByExample(example);
    }

   

    private DistMetaDataModel createDistMetaDataModel(final LanguageModel language, final CountryModel country) {
        DistMetaDataModel example = new DistMetaDataModel();
        example.setLanguage(language);
        example.setCountry(country);
        return example;
    }

    private DistMetaDataModel getDistMetaDataByExample(final DistMetaDataModel example) {
        try {
            return flexibleSearchService.getModelByExample(example);
        } catch (final ModelNotFoundException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(ex.getMessage(), ex);
            }
        } catch (final IllegalArgumentException ex) {
            LOG.error(ex);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.seo.DistSeoService#getActiveHeroProducts()
     */
    @Override
    public DistHeroProductsModel getActiveHeroProducts() {
        try {
            final DistHeroProductsModel example = new DistHeroProductsModel();
            example.setSalesOrg(distSalesOrgService.getCurrentSalesOrg());
            return flexibleSearchService.getModelByExample(example);
        } catch (final ModelNotFoundException ex) {
            try {
                DistHeroProductsModel example = new DistHeroProductsModel();
                example.setMaster(true);
                return flexibleSearchService.getModelByExample(example);
            } catch (final ModelNotFoundException exx) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(exx.getMessage(), exx);
                }
                return null;
            }
        }
    }

}
