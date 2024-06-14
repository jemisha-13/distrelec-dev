package com.namics.distrelec.b2b.facades.order.warehouse.impl;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.namics.distrelec.b2b.core.service.i18n.impl.DistCommerceCommonI18NService;
import com.namics.distrelec.b2b.facades.order.warehouse.WarehouseFacade;
import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultWarehouseFacade implements WarehouseFacade {

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private DistrelecProductFacade distrelecProductFacade;

    @Autowired
    private DistCommerceCommonI18NService i18NService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Converter<WarehouseModel, WarehouseData> warehouseConverter;

    @Override
    public List<WarehouseData> getCheckoutPickupWarehousesForSite(final CMSSiteModel cmsSite) {
        if (cmsSite != null && CollectionUtils.isNotEmpty(cmsSite.getCheckoutPickupWarehouses())) {
            return Converters.convertAll(cmsSite.getCheckoutPickupWarehouses(), warehouseConverter);
        }
        return Collections.emptyList();
    }

    @Override
    public List<WarehouseData> getCheckoutPickupWarehousesForCurrSite() {
        return getCheckoutPickupWarehousesForSite(cmsSiteService.getCurrentSite());
    }

    @Override
    public List<WarehouseData> getPickupWarehousesAndCalculatePickupDate(CartData cartData) {
        Collection<OrderEntryData> mergedOrderEntries = mergeOrderEntries(cartData);
        Map<String, Integer> productQuantityMap = mergedOrderEntries
                                                                    .stream()
                                                                    .collect(Collectors.toMap(orderEntryData -> orderEntryData.getProduct().getCode(),
                                                                                              orderEntryData -> orderEntryData.getQuantity().intValue()));
        return getCheckoutPickupWarehousesForCurrSite()
                                                       .stream()
                                                       .filter(warehouseData -> isNotBlank(warehouseData.getCode()))
                                                       .map(warehouseData -> {
                                                           Pair<Boolean, Date> pickupDate = distrelecProductFacade.getPickupAvailabilityDate(productQuantityMap,
                                                                                                                                             warehouseData.getCode());
                                                           warehouseData.setAvailableForImmediatePickup(pickupDate.getLeft());
                                                           warehouseData.setPickupDate(formatDate(getDataFormatForCurrentCmsSite(), pickupDate.getRight()));
                                                           return warehouseData;
                                                       })
                                                       .collect(Collectors.toList());
    }

    private Collection<OrderEntryData> mergeOrderEntries(CartData cartData) {
        return cartData.getEntries()
                       .stream()
                       .collect(Collectors.toMap(orderEntry -> orderEntry.getProduct().getCode(),
                                                 Function.identity(), (left, right) -> {
                                                     left.setQuantity(left.getQuantity() + (right.getQuantity()));
                                                     return left;
                                                 }))
                       .values();
    }

    private String getDataFormatForCurrentCmsSite() {
        final Locale locale = new Locale(i18NService.getCurrentLocale().getLanguage(), cmsSiteService.getCurrentSite().getCountry().getIsocode());
        return messageSource.getMessage("text.store.dateformat.datepicker.selection", null, "MM/dd/yyyy", locale);
    }

    private String formatDate(String dateFormatForLanguage, Date deliveryDate) {
        return new SimpleDateFormat(dateFormatForLanguage).format(deliveryDate);
    }

}
