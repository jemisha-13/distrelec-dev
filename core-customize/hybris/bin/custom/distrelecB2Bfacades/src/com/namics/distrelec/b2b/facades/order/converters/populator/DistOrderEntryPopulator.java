/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.converters.populator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.service.environment.RuntimeEnvironmentService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.enums.QuoteModificationType;
import com.namics.distrelec.b2b.core.model.availability.DistErpAvailabilityInfoModel;
import com.namics.distrelec.b2b.core.model.order.SubOrderEntryModel;
import com.namics.distrelec.b2b.facades.order.comparators.DistAvailabilityDataComparator;
import com.namics.distrelec.b2b.facades.order.data.DistAvailabilityData;
import com.namics.distrelec.b2b.facades.order.data.SubOrderEntryData;
import com.namics.distrelec.b2b.facades.order.quotation.utils.QuotationUtil;

import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;

public class DistOrderEntryPopulator extends OrderEntryPopulator {

    @Autowired
    @Qualifier("defaultDistSubOrderEntryDataConverter")
    private Converter<SubOrderEntryModel, SubOrderEntryData> subOrderEntryDataConverter;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private RuntimeEnvironmentService runtimeEnvironmentService;

    Converter<ProductModel, ProductData> occProductConverter;

    @Override
    public void populate(final AbstractOrderEntryModel source, final OrderEntryData target) {
        super.populate(source, target);

        target.setCustomerReference(source.getCustomerReference());
        if (source.getBaseListPrice() != null) {
            target.setBaseListPrice(createPrice(source, source.getBaseListPrice()));
        }
        if (source.getTotalListPrice() != null) {
            target.setTotalListPrice(createPrice(source, source.getTotalListPrice()));
        }
        if (source.getBaseNetPrice() != null) {
            target.setBasePrice(createPrice(source, source.getBaseNetPrice()));
        }
        if (source.getTotalNetPrice() != null) {
            target.setTotalPrice(createPrice(source, source.getTotalPrice()));
        }

        target.setIsBackOrder(BooleanUtils.toBoolean(source.getIsBackOrder()));

        setQuotations(source, target);

        target.setRequestedDeliveryDateItemLevel(source.getRequestedDeliveryDateItemLevel());

        setErpAvailabilityInfos(source, target);

        target.setBom(BooleanUtils.toBoolean(source.getIsBOM()));
        if (BooleanUtils.isTrue(source.getIsBOM())) {
            target.setSubOrderEntryData(Converters.convertAll(source.getSubOrderEntries(), subOrderEntryDataConverter));
        }

        target.setSearchQuery(source.getFactFinderTrackingSearchTerm());
        if (source.getIsBackOrderProfitable() != null) {
            target.setBackOrderProfitable(source.getIsBackOrderProfitable());
        }
        if (source.getBackOrderedQuantity() != null) {
            target.setBackOrderedQuantity(source.getBackOrderedQuantity());
        }
        target.setMview(source.getMview());
        target.setAddedFrom(source.getAddedFrom());
    }

    private void setQuotations(AbstractOrderEntryModel source, OrderEntryData target) {
        if (BooleanUtils.isTrue(source.getQuotation())) {
            target.setIsQuotation(BooleanUtils.toBoolean(source.getQuotation()));
            target.setQuotationId(source.getQuotationId());
            target.setLineNumber(source.getLineNumber());
            target.setQuotationReference(source.getQuotationReference());
            target.setDummyItem(BooleanUtils.isTrue(source.getDummyItem()));
            target.setArticleDescription(source.getArticleDescription());
            // Phase II
            target.setMandatoryItem(BooleanUtils.isTrue(source.getMandatoryItem()));
            target.setQuoteModificationType(source.getQuoteModificationType() == null ? QuoteModificationType.ALL
                                                                                      : QuotationUtil.getFromCode(source.getQuoteModificationType()
                                                                                                                        .getCode()));
        }
    }

    private void setErpAvailabilityInfos(AbstractOrderEntryModel source, OrderEntryData target) {
        if (CollectionUtils.isNotEmpty(source.getErpAvailabilityInfos())) {
            final List<DistAvailabilityData> availabilities = new ArrayList<>();
            for (final DistErpAvailabilityInfoModel availability : source.getErpAvailabilityInfos()) {
                if (availability.getQuantity() == null || availability.getQuantity() < 1) {
                    continue;
                }

                final DistAvailabilityData availabilityData = new DistAvailabilityData();

                final Date estimatedDate = availability.getEstimatedDeliveryDate();
                if (estimatedDate != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", i18NService.getCurrentLocale());
                    String date = dateFormat.format(availability.getEstimatedDeliveryDate());
                    availabilityData.setFormattedEstimatedDate(date);
                    availabilityData.setEstimatedDate(availability.getEstimatedDeliveryDate());
                }
                availabilityData.setQuantity(availability.getQuantity());
                availabilities.add(availabilityData);
            }
            availabilities.sort(new DistAvailabilityDataComparator());
            target.setAvailabilities(availabilities);
        }
    }

    @Override
    protected void addProduct(AbstractOrderEntryModel orderEntry, OrderEntryData entry) {
        if(runtimeEnvironmentService.isHeadless()){
            entry.setProduct(occProductConverter.convert(orderEntry.getProduct()));
        }else {
            super.addProduct(orderEntry, entry);
        }
    }

    public void setOccProductConverter(Converter<ProductModel, ProductData> occProductConverter) {
        this.occProductConverter = occProductConverter;
    }
}
