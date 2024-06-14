/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.wishlist.converter;

import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * NamicsWishlistConverter.
 *
 * @author mwegener, Namics AG
 * @since Namics Extensions 1.0
 */
public class NamicsWishlistConverter extends AbstractConverter<Wishlist2Model, NamicsWishlistData> {

    @Autowired
    @Qualifier("customerConverter")
    private Converter<UserModel, CustomerData> customerConverter;

    @Autowired
    @Qualifier("namicsWishlistEntryConverter")
    private Converter<Wishlist2EntryModel, NamicsWishlistEntryData> wishlistEntryConverter;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Override
    public void populate(final Wishlist2Model source, final NamicsWishlistData target) {

        if (source != null) {
            final List<NamicsWishlistEntryData> entries = convertEntries(source, -1);
            fillInTarget(source, target, entries);
        } else {
            createEmptyTarget(target);
        }
    }

    public NamicsWishlistData convertMaxEntries(final Wishlist2Model source, final int maxEntriesToConvert) {
        final NamicsWishlistData target = new NamicsWishlistData();

        if (source != null) {
            final List<NamicsWishlistEntryData> entries = convertEntries(source, maxEntriesToConvert);
            fillInTarget(source, target, entries);
        } else {
            createEmptyTarget(target);
        }

        return target;
    }

    private void fillInTarget(final Wishlist2Model source, final NamicsWishlistData target, final List<NamicsWishlistEntryData> entries) {
        final CustomerData customer = customerConverter.convert(source.getUser());
        target.setCustomer(customer);
        target.setDefaultWishlist(BooleanUtils.isTrue(source.getDefault()));
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setEntries(entries);
        target.setTotalUnitCount(entries.size());
        target.setUniqueId(source.getUniqueId());
        target.setListType(source.getListType().getCode());
        target.setProductCodes(entries.stream()
                                      .map(entry -> entry.getProduct().getCode())
                                      .collect(Collectors.toList()));

        // Add prices
        final String currencyIso = source.getUser().getSessionCurrency().getIsocode();
        target.setTotalPrice(createPriceData(source.getTotalPrice() != null ? source.getTotalPrice() : Double.valueOf(0), currencyIso));
        target.setSubTotal(createPriceData(source.getSubTotal() != null ? source.getSubTotal() : Double.valueOf(0), currencyIso));
        target.setTotalTax(createPriceData(source.getTotalTax() != null ? source.getTotalTax() : Double.valueOf(0), currencyIso));
        target.setCalculated(BooleanUtils.isTrue(source.getCalculated()));
    }

    protected PriceData createPriceData(final Double val, final String currencyIso) {
        return priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(val != null ? val : 0), currencyIso);
    }

    private List<NamicsWishlistEntryData> convertEntries(final Wishlist2Model source, final int maxEntries) {
        final List<NamicsWishlistEntryData> entries = new ArrayList<>();

        if (maxEntries < 0) {
            for (final Wishlist2EntryModel wishlistEntryModel : source.getEntries()) {
                convertEntry(entries, wishlistEntryModel);
            }
        } else {
            final List<Wishlist2EntryModel> entryModels = source.getEntries();
            final int entriesToDisplay = (maxEntries > source.getEntries().size()) ? entryModels.size() : maxEntries;
            for (int i = 1; i <= entriesToDisplay; i++) {
                final Wishlist2EntryModel wishlistEntryModel = entryModels.get(entryModels.size() - i);
                convertEntry(entries, wishlistEntryModel);
            }
        }

        return entries;
    }

    private void convertEntry(final List<NamicsWishlistEntryData> entries, final Wishlist2EntryModel wishlistEntryModel) {
        if (wishlistEntryModel.getProduct() != null) {
            final NamicsWishlistEntryData wishlistEntryData = wishlistEntryConverter.convert(wishlistEntryModel);
            if (wishlistEntryData != null && wishlistEntryData.getProduct() != null) {
                entries.add(wishlistEntryData);
            }
        }
    }

    private void createEmptyTarget(final NamicsWishlistData target) {
        target.setCustomer(new CustomerData());
        target.setDefaultWishlist(false);
        target.setDescription(StringUtils.EMPTY);
        target.setName(StringUtils.EMPTY);
        target.setEntries(new ArrayList<>());
        target.setTotalUnitCount(0);
        target.setUniqueId(StringUtils.EMPTY);
        target.setListType(StringUtils.EMPTY);
    }
}

