/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.cms2.components.DistProductReferencesCarouselComponentModel;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.ProductDataHelper;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller("DistProductReferencesCarouselComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistProductReferencesCarouselComponent)
public class DistProductReferencesCarouselComponentController extends AbstractDistCMSComponentController<DistProductReferencesCarouselComponentModel> {
    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.PROMOTION_LABELS,
            ProductOption.DIST_MANUFACTURER);
    private static final String AUTOPLAY = "autoplay";

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistProductReferencesCarouselComponentModel component) {
        final List<ProductReferenceData> productReferences = productFacade.getProductReferencesForCode(ProductDataHelper.getCurrentProduct(request),
                Collections.singletonList(component.getReferenceType()), PRODUCT_OPTIONS, null);

        final List<ProductData> products = new ArrayList<ProductData>();
        for (final ProductReferenceData referenceData : productReferences) {
            products.add(referenceData.getTarget());
        }

        if (component.getAutoplayTimeout() == null) {
            model.addAttribute(AUTOPLAY, Boolean.FALSE);
        } else {
            model.addAttribute(AUTOPLAY, Boolean.TRUE);
        }
        model.addAttribute("productReferencesCarouselData", products);

        distWebtrekkFacade.addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }
}
