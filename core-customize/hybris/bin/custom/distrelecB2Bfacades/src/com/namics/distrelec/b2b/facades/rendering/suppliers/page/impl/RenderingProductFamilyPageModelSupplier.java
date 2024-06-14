package com.namics.distrelec.b2b.facades.rendering.suppliers.page.impl;

import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.facades.category.DistProductFamilyFacade;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cmsfacades.rendering.suppliers.page.RenderingPageModelSupplier;

public class RenderingProductFamilyPageModelSupplier implements RenderingPageModelSupplier {

    private static final Logger LOG = Logger.getLogger(RenderingProductFamilyPageModelSupplier.class.getName());

    private Predicate<String> constrainedBy;

    private DistProductFamilyFacade distProductFamilyFacade;

    private DistCategoryService distCategoryService;

    private CMSDataFactory cmsDataFactory;

    @Override
    public Predicate<String> getConstrainedBy() {
        return constrainedBy;
    }

    @Override
    public Optional<AbstractPageModel> getPageModel(String qualifier) {
        Optional<ProductFamilyPageModel> productFamilyPageOptional = distProductFamilyFacade.findPageForProductFamily(qualifier);

        if (productFamilyPageOptional.isPresent()) {
            return Optional.of(productFamilyPageOptional.get());
        } else {
            LOG.warn("Unable to find page for product family: " + qualifier);
            return Optional.empty();
        }
    }

    @Override
    public Optional<RestrictionData> getRestrictionData(String qualifier) {
        Optional<CategoryModel> productFamilyCategory = getProductFamilyCategory(qualifier);
        if (productFamilyCategory.isPresent()) {
            return Optional.ofNullable(cmsDataFactory.createRestrictionData(productFamilyCategory.get()));
        }
        return Optional.empty();
    }

    protected Optional<CategoryModel> getProductFamilyCategory(String code) {
        if (StringUtils.isBlank(code)) {
            return Optional.empty();
        }
        try {
            return distCategoryService.findProductFamily(code);
        } catch (Exception ex) {
            LOG.info("Could not find product family category with code <" + code + ">", ex);
        }
        return Optional.empty();
    }

    public void setConstrainedBy(Predicate<String> constrainedBy) {
        this.constrainedBy = constrainedBy;
    }

    public void setDistProductFamilyFacade(DistProductFamilyFacade distProductFamilyFacade) {
        this.distProductFamilyFacade = distProductFamilyFacade;
    }

    public void setDistCategoryService(DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }

    public void setCmsDataFactory(CMSDataFactory cmsDataFactory) {
        this.cmsDataFactory = cmsDataFactory;
    }
}
