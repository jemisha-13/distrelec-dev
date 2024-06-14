/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.wishlist.impl;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.enums.ProductListOrder;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.facades.util.AbstractDistComparator;
import com.namics.distrelec.b2b.facades.wishlist.DistWishlistFacade;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistEntryData;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

public abstract class AbstractDistWishlistFacade extends DefaultNamicsWishlistFacade implements DistWishlistFacade {

    private static final Map<ProductListOrder, AbstractComparator<NamicsWishlistEntryData>> COMPARATORS = new HashMap<>();

    static {
        COMPARATORS.put(ProductListOrder.DATEADDEDASC, DateAddedAscComp.INSTANCE);
        COMPARATORS.put(ProductListOrder.DATEADDEDDESC, DateAddedDescComp.INSTANCE);
        COMPARATORS.put(ProductListOrder.NAMEASC, NameAscComp.INSTANCE);
        COMPARATORS.put(ProductListOrder.NAMEDESC, NameDescComp.INSTANCE);
        COMPARATORS.put(ProductListOrder.ARTNRASC, ArtNrAscComp.INSTANCE);
        COMPARATORS.put(ProductListOrder.ARTNRDESC, ArtNrDescComp.INSTANCE);
        COMPARATORS.put(ProductListOrder.PRICEASC, PriceAscComp.INSTANCE);
        COMPARATORS.put(ProductListOrder.PRICEDESC, PriceDescComp.INSTANCE);
        COMPARATORS.put(ProductListOrder.MANUFACTURERASC, ManufacturerAscComp.INSTANCE);
        COMPARATORS.put(ProductListOrder.MANUFACTURERDESC, ManufacturerDescComp.INSTANCE);
    }

    @Autowired
    private UserService userService;

    @Override
    public NamicsWishlistData getWishlist() {
        final NamicsWishlistData wishlist = super.getWishlist();
        sortList(wishlist, COMPARATORS.get(ProductListOrder.DATEADDEDDESC));
        return wishlist;
    }

    @Override
    public NamicsWishlistData getWishlist(final String uuid) {
        final NamicsWishlistData wishlist = super.getWishlist(uuid);
        sortList(wishlist, COMPARATORS.get(ProductListOrder.DATEADDEDDESC));
        return wishlist;
    }

    @Override
    public NamicsWishlistData getWishlist(final ProductListOrder order) {
        final NamicsWishlistData wishlist = super.getWishlist();
        sortList(wishlist, COMPARATORS.get(order));
        return wishlist;
    }

    @Override
    public NamicsWishlistData getWishlist(final String uuid, final ProductListOrder order) {
        final NamicsWishlistData wishlist = super.getWishlist(uuid);
        sortList(wishlist, COMPARATORS.get(order));
        return wishlist;
    }

    @Override
    protected NamicsWishlistData convertWishlist(final Wishlist2Model wishlistModel) {
        return wishlistModel != null ? getWishlistConverter().convert(wishlistModel) : null;
    }

    protected void sortList(final NamicsWishlistData wishlist, final Comparator comparator) {
        if (wishlist != null) {
            final List<NamicsWishlistEntryData> list = wishlist.getEntries();
            if (CollectionUtils.isNotEmpty(list)) {
                Collections.sort(list, comparator);
            }
            wishlist.setEntries(list);
        }
    }

    public boolean isGuestUser() {
        final UserModel currentUser = userService.getCurrentUser();
        return userService.isAnonymousUser(currentUser);
    }

    @Override
    public int getDefaultListSize() {
        return getWishlistService().getWishlistSize(getType());
    }

    @Override
    public List<String> productsInWishlist(final String[] codes) {
        final Map<String, List<String>> result = getWishlistService().productsInWishlists(Arrays.asList(codes), getType());
        return new ArrayList<>(result.keySet());
    }

    protected static class DateAddedAscComp extends AbstractDistComparator<NamicsWishlistEntryData> {
        protected static final DateAddedAscComp INSTANCE = new DateAddedAscComp();

        @Override
        protected int compareInstances(final NamicsWishlistEntryData entry1, final NamicsWishlistEntryData entry2) {
            return compareNullGreater(entry1.getAddedDate(), entry2.getAddedDate());
        }
    }

    protected static class DateAddedDescComp extends AbstractDistComparator<NamicsWishlistEntryData> {
        protected static final DateAddedDescComp INSTANCE = new DateAddedDescComp();

        @Override
        protected int compareInstances(final NamicsWishlistEntryData entry1, final NamicsWishlistEntryData entry2) {
            return compareNullNotGreater(entry2.getAddedDate(), entry1.getAddedDate());
        }
    }

    protected static class NameAscComp extends AbstractDistComparator<NamicsWishlistEntryData> {
        protected static final NameAscComp INSTANCE = new NameAscComp();

        @Override
        protected int compareInstances(final NamicsWishlistEntryData entry1, final NamicsWishlistEntryData entry2) {
            final String nameOne = entry1.getProduct().getName();
            final String nameTwo = entry2.getProduct().getName();
            return compareNullGreater(StringUtils.isBlank(nameOne) ? nameOne : nameOne.toLowerCase(),
                                      StringUtils.isBlank(nameTwo) ? nameTwo : nameTwo.toLowerCase());
        }
    }

    protected static class NameDescComp extends AbstractDistComparator<NamicsWishlistEntryData> {
        protected static final NameDescComp INSTANCE = new NameDescComp();

        @Override
        protected int compareInstances(final NamicsWishlistEntryData entry1, final NamicsWishlistEntryData entry2) {
            final String nameOne = entry1.getProduct().getName();
            final String nameTwo = entry2.getProduct().getName();
            return compareNullNotGreater(StringUtils.isBlank(nameTwo) ? nameTwo : nameTwo.toLowerCase(),
                                         StringUtils.isBlank(nameOne) ? nameOne : nameOne.toLowerCase());
        }
    }

    protected static class ArtNrAscComp extends AbstractDistComparator<NamicsWishlistEntryData> {
        protected static final ArtNrAscComp INSTANCE = new ArtNrAscComp();

        @Override
        protected int compareInstances(final NamicsWishlistEntryData entry1, final NamicsWishlistEntryData entry2) {
            return compareNullGreater(entry1.getProduct().getCodeErpRelevant(), entry2.getProduct().getCodeErpRelevant());
        }
    }

    protected static class ArtNrDescComp extends AbstractDistComparator<NamicsWishlistEntryData> {
        protected static final ArtNrDescComp INSTANCE = new ArtNrDescComp();

        @Override
        protected int compareInstances(final NamicsWishlistEntryData entry1, final NamicsWishlistEntryData entry2) {
            return compareNullGreater(entry2.getProduct().getCodeErpRelevant(), entry1.getProduct().getCodeErpRelevant());
        }
    }

    protected static class PriceAscComp extends AbstractDistComparator<NamicsWishlistEntryData> {
        protected static final PriceAscComp INSTANCE = new PriceAscComp();

        @Override
        protected int compareInstances(final NamicsWishlistEntryData entry1, final NamicsWishlistEntryData entry2) {
            final PriceData price1 = entry1.getProduct().getPrice();
            final PriceData price2 = entry2.getProduct().getPrice();
            if (price1 != null && price2 != null) {
                return compareNullGreater(price1.getValue(), price2.getValue());
            }
            return 0;
        }
    }

    protected static class PriceDescComp extends AbstractDistComparator<NamicsWishlistEntryData> {
        protected static final PriceDescComp INSTANCE = new PriceDescComp();

        @Override
        protected int compareInstances(final NamicsWishlistEntryData entry1, final NamicsWishlistEntryData entry2) {
            final PriceData price1 = entry1.getProduct().getPrice();
            final PriceData price2 = entry2.getProduct().getPrice();
            if (price1 != null && price2 != null) {
                return compareNullNotGreater(price2.getValue(), price1.getValue());
            }
            return 0;
        }
    }

    protected static class ManufacturerAscComp extends AbstractDistComparator<NamicsWishlistEntryData> {
        protected static final ManufacturerAscComp INSTANCE = new ManufacturerAscComp();

        @Override
        protected int compareInstances(final NamicsWishlistEntryData entry1, final NamicsWishlistEntryData entry2) {
            final ProductData p1 = entry1.getProduct();
            final ProductData p2 = entry2.getProduct();
            final DistManufacturerData m1 = p1.getDistManufacturer();
            final DistManufacturerData m2 = p2.getDistManufacturer();
            if (m1 != null && m2 != null) {
                final String nameOne = m1.getName();
                final String nameTwo = m2.getName();
                return compareNullGreater(StringUtils.isBlank(nameOne) ? nameOne : nameOne.toLowerCase(),
                                          StringUtils.isBlank(nameTwo) ? nameTwo : nameTwo.toLowerCase());
            }
            return 0;
        }
    }

    protected static class ManufacturerDescComp extends AbstractDistComparator<NamicsWishlistEntryData> {
        protected static final ManufacturerDescComp INSTANCE = new ManufacturerDescComp();

        @Override
        protected int compareInstances(final NamicsWishlistEntryData entry1, final NamicsWishlistEntryData entry2) {
            final ProductData p1 = entry1.getProduct();
            final ProductData p2 = entry2.getProduct();
            final DistManufacturerData m1 = p1.getDistManufacturer();
            final DistManufacturerData m2 = p2.getDistManufacturer();
            if (m1 != null && m2 != null) {
                final String nameOne = m1.getName();
                final String nameTwo = m2.getName();
                return compareNullNotGreater(StringUtils.isBlank(nameTwo) ? nameTwo : nameTwo.toLowerCase(),
                                             StringUtils.isBlank(nameOne) ? nameOne : nameOne.toLowerCase());
            }
            return 0;
        }
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

}
