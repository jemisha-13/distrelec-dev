/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import com.namics.distrelec.b2b.core.model.marketing.DistHeroProductsModel;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * {@code DistHeroProductsValidateInterceptor}
 * 
 * 
 * @author <a href="wilhelm-patrick.spalinger@distrelec.com">Wilhelm Spalinger</a>, Distrelec
 * @since Distrelec 4.13
 */
public class DistHeroProductsValidateInterceptor implements ValidateInterceptor {

    private FlexibleSearchService flexibleSearchService;
    private L10NService l10nService;

    /**
     * Create a new instance of {@code DistHeroProductsValidateInterceptor}
     */
    public DistHeroProductsValidateInterceptor() {
    }

    @Override
    public void onValidate(Object obj, InterceptorContext ctx) throws InterceptorException {
        DistHeroProductsModel model = (DistHeroProductsModel) obj;

        // check for master
        if (model.isMaster()) {
            if (model.getSalesOrg() != null) {
                throw new InterceptorException("The master hero products cannot be set for a SalesOrg");
            }
            try {
                final DistHeroProductsModel example = new DistHeroProductsModel();
                example.setMaster(true);
                DistHeroProductsModel master = flexibleSearchService.getModelByExample(example);
                if (!master.getPk().equals(model.getPk())) {
                    throw new InterceptorException("Only one master hero product set can exist");
                }
            } catch (ModelNotFoundException exx) {
                // everything fine, there is no other master item
            }
        }

        if (model.getPOne() == null || model.getPTwo() == null || model.getPThree() == null || model.getPFour() == null) {
            throw new InterceptorException("The first four products are mandatory, the fifth product is optional");
        }
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public L10NService getL10nService() {
        return l10nService;
    }

    public void setL10nService(L10NService l10nService) {
        this.l10nService = l10nService;
    }

}
