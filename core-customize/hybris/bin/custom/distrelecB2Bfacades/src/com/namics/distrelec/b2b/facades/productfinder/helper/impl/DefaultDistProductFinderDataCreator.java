/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.helper.impl;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderColumnModel;
import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderConfigurationModel;
import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderGroupModel;
import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderValueModel;
import com.namics.distrelec.b2b.facades.productfinder.data.*;
import com.namics.distrelec.b2b.facades.productfinder.helper.DistProductFinderDataCreator;
import com.namics.distrelec.b2b.facades.productfinder.helper.DistProductFinderHelper;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link DistProductFinderDataCreator} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultDistProductFinderDataCreator implements DistProductFinderDataCreator {

    private Converter<DistProductFinderValueModel, DistProductFinderValueData> valueDelegatingConverter;

    private DistProductFinderHelper distProductFinderHelper;

    private CommonI18NService commonI18NService;

    @Override
    public DistProductFinderData createProductFinderData(final DistProductFinderConfigurationModel configuration,
            final FactFinderProductSearchPageData<?, ?> searchPageData) {

        final DistProductFinderData productFinderData = new DistProductFinderData();
        productFinderData.setCategoryCode(configuration.getCategory().getCode());
        productFinderData.setColumns(createProductFinderColumns(configuration, searchPageData));
        return productFinderData;
    }

    private List<DistProductFinderColumnData> createProductFinderColumns(final DistProductFinderConfigurationModel configuration,
            final FactFinderProductSearchPageData<?, ?> searchPageData) {

        final List<DistProductFinderColumnData> columns = new ArrayList<DistProductFinderColumnData>();
        for (final DistProductFinderColumnModel columnModel : configuration.getColumns()) {
            final DistProductFinderColumnData column = new DistProductFinderColumnData();
            column.setGroups(createProductFinderGroups(searchPageData, columnModel));
            columns.add(column);
        }
        return columns;
    }

    private List<DistProductFinderGroupData> createProductFinderGroups(final FactFinderProductSearchPageData<?, ?> searchPageData,
            final DistProductFinderColumnModel columnModel) {

        final List<DistProductFinderGroupData> groups = new ArrayList<DistProductFinderGroupData>();
        for (final DistProductFinderGroupModel groupModel : columnModel.getGroups()) {
            final DistProductFinderGroupData group = new DistProductFinderGroupData();
            group.setName(groupModel.getName());
            group.setValues(createProductFinderValues(searchPageData, groupModel));
            setGroupType(group);

            if (!group.getValues().isEmpty()) {
                groups.add(group);
            }
        }
        return groups;
    }

    private void setGroupType(final DistProductFinderGroupData group) {
        DistProductFinderGroupType type = DistProductFinderGroupType.MULTI;

        for (final DistProductFinderValueData value : group.getValues()) {
            if (CollectionUtils.isEmpty(value.getFacetValues())) {
                type = DistProductFinderGroupType.SINGLE;
                setCustomValue(group, value.getMinMaxKey());
                break;
            }
        }

        group.setType(type);
    }

    private void setCustomValue(final DistProductFinderGroupData group, final String minMaxKey) {
        final DistProductFinderValueData customValue = new DistProductFinderValueData();
        customValue.setMinMaxKey(minMaxKey);
        group.setCustomValue(customValue);
    }

    private List<DistProductFinderValueData> createProductFinderValues(final FactFinderProductSearchPageData<?, ?> searchPageData,
            final DistProductFinderGroupModel groupModel) {

        final CurrencyModel currency = getCommonI18NService().getCurrentCurrency();

        final List<DistProductFinderValueData> values = new ArrayList<DistProductFinderValueData>();
        for (final DistProductFinderValueModel valueModel : groupModel.getValues()) {
            if (currency == null || valueModel.getCurrencyRestriction() == null || currency.equals(valueModel.getCurrencyRestriction())) {
                addValueIfAvailable(values, valueModel, searchPageData);
            }
        }
        return values;
    }

    private void addValueIfAvailable(final List<DistProductFinderValueData> values, final DistProductFinderValueModel valueModel,
            final FactFinderProductSearchPageData<?, ?> searchPageData) {

        final DistProductFinderValueData value = getValueDelegatingConverter().convert(valueModel);
        if (distProductFinderHelper.isValueAvailable(value, searchPageData)) {
            values.add(value);
        }
    }

    public Converter<DistProductFinderValueModel, DistProductFinderValueData> getValueDelegatingConverter() {
        return valueDelegatingConverter;
    }

    @Required
    public void setValueDelegatingConverter(final Converter<DistProductFinderValueModel, DistProductFinderValueData> valueDelegatingConverter) {
        this.valueDelegatingConverter = valueDelegatingConverter;
    }

    public DistProductFinderHelper getDistProductFinderHelper() {
        return distProductFinderHelper;
    }

    @Required
    public void setDistProductFinderHelper(final DistProductFinderHelper distProductFinderHelper) {
        this.distProductFinderHelper = distProductFinderHelper;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

}
