/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.misc;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.core.service.wishlist.DistWishlistService;
import com.namics.distrelec.b2b.facades.order.cart.DistCartFacade;
import com.namics.distrelec.b2b.facades.wishlist.DistCompareListFacade;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class WishlistController extends AbstractController {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(WishlistController.class);

    private static final String PRODUCT_CODES = "productCodes[]";

    @Autowired
    private DistCompareListFacade distCompareListFacade;

    @Autowired
    private DistCartFacade cartFacade;

    @Autowired
    private DistWishlistService distWishlistService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/checkToggles", method = RequestMethod.POST)
    public String getProductToggles(@RequestParam(value = PRODUCT_CODES)
    final String[] productCodes, final Model model, final HttpServletRequest request) {
        final long startTime = System.nanoTime();
        Map<String, List<String>> result;

        if (isGuestUser()) {
            result = new HashMap<>();
            for (final String code : distCompareListFacade.productsInCompareList(productCodes, Attributes.COMPARE.getValueFromCookies(request))) {
                addToMap(result, code, NamicsWishlistType.COMPARE.getCode());
            }
        } else {
            result = distWishlistService.productsInWishlists(Arrays.asList(productCodes));
        }

        for (final String code : cartFacade.productsInCart(productCodes)) {
            addToMap(result, code, "CART");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Execution of getProductToggles() took " + (int) ((System.nanoTime() - startTime) / 1e6) + " ms.");
        }
        model.addAttribute("toggles", result);
        return ControllerConstants.Views.Fragments.Wishlist.WishlistTogglesJson;
    }

    protected void addToMap(final Map<String, List<String>> result, final String code, final String type) {
        result.putIfAbsent(code, new ArrayList<>());
        result.get(code).add(type);
    }

    protected boolean isGuestUser() {
        final UserModel currentUser = userService.getCurrentUser();
        // DISTRELEC-6065 B2E, OCI and ARIBA customers does not have a persistent compare list
        return userService.isAnonymousUser(currentUser) || isMemberOfGroup(currentUser, DistConstants.User.EPROCUREMENTGROUP_UID)
                || isMemberOfGroup(currentUser, DistConstants.User.B2BEESHOPGROUP_UID);
    }

    protected boolean isMemberOfGroup(final UserModel currentUser, final String groupName) {
        return userService.isMemberOfGroup(currentUser, userService.getUserGroupForUID(groupName));
    }
}
