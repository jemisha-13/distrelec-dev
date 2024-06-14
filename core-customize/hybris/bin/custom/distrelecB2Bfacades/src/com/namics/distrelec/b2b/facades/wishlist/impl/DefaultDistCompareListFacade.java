/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.wishlist.impl;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.wishlist.DistCompareListFacade;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistEntryData;

import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

/**
 * Compare list facade containing some basic functions like looking up
 *
 * @author sivakumaran, Namics AG
 * @since Namics Extensions 1.0
 */
public class DefaultDistCompareListFacade extends AbstractDistWishlistFacade implements DistCompareListFacade {

    private static final Logger LOG = Logger.getLogger(DefaultDistCompareListFacade.class);

    private static final char TOKEN = ':';

    private static final String COMPARELIST_ERROR = "This Operation is not supported for product in compare list.";

    //@formatter:off
    protected static final Collection<ProductOption> PRODUCT_OPTIONS = Arrays.asList(
            ProductOption.MIN_BASIC,
            ProductOption.PRICE,
            ProductOption.DIST_MANUFACTURER,
            ProductOption.CLASSIFICATION,
            ProductOption.DOWNLOADS,
            ProductOption.CATEGORIES,
            ProductOption.BREADCRUMBS,
            ProductOption.CHANNEL_AVAILABILITY);
    //@formatter:on

    @Autowired
    private DistrelecProductFacade productFacade;

    @Override
    public String addToCompareList(final String productCode, final String cookieValue) {
        if (isGuestUser()) {
            return addValue(cookieValue, productCode);
        } else {
            super.addToWishlist(productCode);
            return null;
        }
    }

    @Override
    public String addCookieProductsToCompareList(final String cookieValue) {
        if (!isGuestUser() && cookieValue != null) {
            final String[] codes = StringUtils.split(cookieValue, TOKEN);
            for (final String code : codes) {
                try {
                    super.addToWishlist(code);
                } catch (final UnknownIdentifierException e) {
                    // catch exception in case the user modified the cookie with some random code.
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(getProductNotFoundMessage(code), e);
                    }
                }
            }
            return null;
        }
        return "";
    }

    @Override
    public List<ProductData> getCompareList(final String cookieValue) {
        return getCompareList(cookieValue, 0);
    }

    @Override
    public List<ProductData> getCompareList(final String cookieValue, final int limit) {
        if (isGuestUser()) {
            if (cookieValue != null) {
                final String[] codes = StringUtils.split(cookieValue, TOKEN);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Guest user compare list size [" + codes.length + "].");
                }
                return getProducts(codes, limit);
            } else {
                return Collections.EMPTY_LIST;
            }
        } else {
            final NamicsWishlistData wishlist = super.getWishlist();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Registered user compare list size [" + wishlist.getEntries().size() + "].");
            }
            return getProducts(wishlist, limit);
        }
    }

    @Override
    public boolean hasCompareProducts(final String cookieValue) {
        return getCompareListSize(cookieValue) > 0;
    }

    /**
     * Gets the wishlist of the session user and wishlist type
     *
     * @return the Wishlist2Model
     */
    @Override
    public Wishlist2Model getWishlistModel() {
        return getWishlistService().getOrCreateWishlist(getType());
    }

    @Override
    public String removeFromCompareList(final String productCode, final String cookieValue) {
        if (isGuestUser()) {
            return removeValue(cookieValue, productCode);
        } else {
            super.removeFromWishlist(productCode);
            return null;
        }
    }

    @Override
    public String removeAllFromCompareList(final String cookieValue) {
        if (isGuestUser()) {
            return removeValue(cookieValue, null);
        } else {
            super.removeAllFromWishlist();
            return null;
        }
    }

    @Override
    public boolean isProductInWishlist(final String code, final String cookieValue) {
        if (isGuestUser()) {
            if (cookieValue != null) {
                final String[] productCodes = StringUtils.split(cookieValue, TOKEN);
                return ArrayUtils.contains(productCodes, code);
            } else {
                return false;
            }
        } else {
            return isEntryInWishlist(code, getType());
        }
    }

    @Override
    public List<String> productsInCompareList(final String[] codes, final String cookieValue) {
        if (codes == null || codes.length == 0) {
            return Collections.EMPTY_LIST;
        }

        if (isGuestUser()) {
            if (StringUtils.isBlank(cookieValue)) {
                return Collections.EMPTY_LIST;
            }
            final String[] productCodes = StringUtils.split(cookieValue, TOKEN);
            final List<String> result = new ArrayList<>();
            for (final String code : codes) {
                if (ArrayUtils.contains(productCodes, code)) {
                    result.add(code);
                }
            }
            return result;
        } else {
            return productsInWishlist(codes);
        }
    }

    @Override
    public List<String> productsInWishlist(final String[] codes) {
        return isGuestUser() ? Collections.EMPTY_LIST : super.productsInWishlist(codes);
    }

    @Override
    public boolean isProductInWishlist(final String code) {
        throw new UnsupportedOperationException(COMPARELIST_ERROR);
    }

    @Override
    public NamicsWishlistType getType() {
        return NamicsWishlistType.COMPARE;
    }

    @Override
    public boolean isGuestUser() {
        // DISTRELEC-6065 B2E, OCI and ARIBA customers does not have a persistent compare list
        return super.isGuestUser() || isMemberOfGroup(DistConstants.User.EPROCUREMENTGROUP_UID) || isMemberOfGroup(DistConstants.User.B2BEESHOPGROUP_UID);
    }

    protected boolean isMemberOfGroup(final String groupName) {
        return getUserService().isMemberOfGroup(getUserService().getCurrentUser(), getUserService().getUserGroupForUID(groupName));
    }

    public List<ProductData> getProducts(final NamicsWishlistData wishlist, final int listSize) {

        final List<ProductData> products = new ArrayList<>();
        if (wishlist != null && CollectionUtils.isNotEmpty(wishlist.getEntries())) {
            int count = 1;
            for (final NamicsWishlistEntryData entry : wishlist.getEntries()) {
                if (listSize > 0 && count > listSize) {
                    break;
                }
                products.add(entry.getProduct());
                count++;
            }
        }
        return products;
    }

    protected List<ProductData> getProducts(final String[] codes, final int listSize) {
        final List<ProductData> products = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(codes)) {
            int count = 1;
            for (final String code : codes) {
                if (listSize > 0 && count > listSize) {
                    break;
                }
                try {
                    products.add(productFacade.getProductForCodeAndOptions(code, PRODUCT_OPTIONS));
                    count++;
                } catch (final UnknownIdentifierException e) {
                    // catch exception in case the user modified the cookie with some random code.
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(getProductNotFoundMessage(code), e);
                    }
                }
            }
        }
        return products;
    }

    @Override
    public int getCompareListSize(final String cookieValue) {
        if (isGuestUser()) {
            return cookieValue != null ? StringUtils.split(cookieValue, TOKEN).length : 0;
        } else {
            return getWishlistService().getWishlistSize(getType());
        }
    }

    public static String addValue(final String values, final String value) {
        if (values != null) {
            String[] valueList = StringUtils.split(values, TOKEN);
            if (!ArrayUtils.contains(valueList, value)) {
                valueList = (String[]) ArrayUtils.add(valueList, 0, value);
            }
            return StringUtils.join(valueList, TOKEN);
        }
        return value;
    }

    public static String removeValue(final String values, final String value) {
        if (values != null && value != null) {
            final String[] valueList = (String[]) ArrayUtils.removeElement(StringUtils.split(values, TOKEN), value);
            return StringUtils.join(valueList, TOKEN);
        }

        return null;
    }

    protected String getProductNotFoundMessage(final String code) {
        return "Product with code " + code + " from cookie not found.";
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }
}
