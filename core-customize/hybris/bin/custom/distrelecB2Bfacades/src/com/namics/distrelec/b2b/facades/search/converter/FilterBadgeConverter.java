/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

/**
 * Converter for {@link FilterBadgeData} POJOs.
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 *
 * @param <QUERY>
 * @param <STATE>
 */
public class FilterBadgeConverter<QUERY, STATE> extends AbstractPopulatingConverter<FilterBadgeData<QUERY>, FilterBadgeData<STATE>> {

    private final static String PROPERTY_NAME_ARGUMENT_SEPARATOR = "^";

    private Converter<QUERY, STATE> searchStateConverter;
    private CMSSiteService cmsSiteService;

    @Override
    public void populate(final FilterBadgeData<QUERY> source, final FilterBadgeData<STATE> target) {
        target.setFacetCode(source.getFacetCode());
        target.setFacetName(source.getFacetName());
        target.setFacetValueCode(source.getFacetValueCode());
        target.setFacetValueName(source.getFacetValueName());
        target.setFacetValuePropertyName(source.getFacetValuePropertyName());
        target.setFacetValuePropertyNameArguments(getPropertyNameArguments(source.getFacetValuePropertyName()));
        target.setFacetValuePropertyNameArgumentSeparator(PROPERTY_NAME_ARGUMENT_SEPARATOR);
        target.setType(source.getType());
        target.setCategoryFilter(source.isCategoryFilter());

        if (source.getRemoveQuery() != null) {
            target.setRemoveQuery(getSearchStateConverter().convert(source.getRemoveQuery()));
        }
        if (source.getTruncateQuery() != null) {
            target.setTruncateQuery(getSearchStateConverter().convert(source.getTruncateQuery()));
        }
        super.populate(source, target);
    }

    private String getPropertyNameArguments(final String propertyName) {
        if (DistrelecfactfindersearchConstants.FILTER_INSTOCK_FAST.equals(propertyName)) {
            return getCmsSiteService().getCurrentSite().getFastDeliveryTime();
        } else if (DistrelecfactfindersearchConstants.FILTER_INSTOCK_SLOW.equals(propertyName)) {
            return getCmsSiteService().getCurrentSite().getSlowDeliveryTime();
        }
        return null;
    }

    @Override
    protected FilterBadgeData<STATE> createTarget() {
        return new FilterBadgeData<STATE>();
    }

    protected Converter<QUERY, STATE> getSearchStateConverter() {
        return searchStateConverter;
    }

    @Required
    public void setSearchStateConverter(final Converter<QUERY, STATE> searchStateConverter) {
        this.searchStateConverter = searchStateConverter;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    @Required
    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

}
