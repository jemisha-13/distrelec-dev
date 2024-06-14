/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Populating Converter for {@link FactFinderFacetValueData} which populates the QUERY to STATE.
 *
 * @param <QUERY>
 * @param <STATE>
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class FactFinderFacetValueConverter<QUERY, STATE> extends AbstractPopulatingConverter<FactFinderFacetValueData<QUERY>, FactFinderFacetValueData<STATE>> {

    private static final String PROPERTY_NAME_ARGUMENT_SEPARATOR = "^";

    @Autowired
    private Converter<QUERY, STATE> searchStateConverter;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private UserService userService;

    @Override
    public void populate(final FactFinderFacetValueData<QUERY> source, final FactFinderFacetValueData<STATE> target) {
        target.setName(DistUtils.decodeFfSpecialChars(source.getName()));
        target.setCode(source.getCode());
        target.setPropertyName(getPropertyName(source.getName(), source.getPropertyName()));
        target.setPropertyNameArguments(getPropertyNameArguments(source.getPropertyName()));
        target.setPropertyNameArgumentSeparator(PROPERTY_NAME_ARGUMENT_SEPARATOR);
        target.setCount(source.getCount());
        target.setSelected(source.isSelected());
        target.setAbsoluteMaxValue(source.getAbsoluteMaxValue());
        target.setAbsoluteMinValue(source.getAbsoluteMinValue());
        target.setSelectedMaxValue(source.getSelectedMaxValue());
        target.setSelectedMinValue(source.getSelectedMinValue());
        if (source.getQuery() != null) {
            target.setQuery(searchStateConverter.convert(source.getQuery()));
        }
        target.setQueryFilter(source.getQueryFilter());
        super.populate(source, target);
    }

    private String getPropertyName(String name, String propertyName) {
        if (StringUtils.equals(propertyName, DistrelecfactfindersearchConstants.FILTER_FACET_PRODUCT_STATUS)) {
            return propertyName + DistConstants.Punctuation.DOT + removeSortingPrefixAndFormat(name);
        }
        return propertyName;
    }

    private String removeSortingPrefixAndFormat(String name) {
        return StringUtils.lowerCase(StringUtils.substring(name, 2));
    }

    private String getPropertyNameArguments(final String propertyName) {
        if (userService.getCurrentUser() instanceof EmployeeModel) {
            return StringUtils.EMPTY;
        }
        if (DistrelecfactfindersearchConstants.FILTER_INSTOCK_FAST.equals(propertyName)) {
            return cmsSiteService.getCurrentSite().getFastDeliveryTime();
        } else if (DistrelecfactfindersearchConstants.FILTER_INSTOCK_SLOW.equals(propertyName)) {
            return cmsSiteService.getCurrentSite().getSlowDeliveryTime();
        }
        return null;
    }

    @Override
    protected FactFinderFacetValueData<STATE> createTarget() {
        return new FactFinderFacetValueData<STATE>();
    }
}
