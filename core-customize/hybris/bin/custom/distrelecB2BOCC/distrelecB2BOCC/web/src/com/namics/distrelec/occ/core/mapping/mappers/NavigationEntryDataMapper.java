/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.mapping.mappers;

import com.namics.distrelec.occ.core.mapping.resolvers.DistHeadlessResolver;

import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.cmsoccaddon.data.NavigationEntryWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import ma.glasnost.orika.MappingContext;

public class NavigationEntryDataMapper extends AbstractCustomMapper<NavigationEntryData, NavigationEntryWsDTO> {

    private DistHeadlessResolver<String, String> headlessURLResolver;

    @Override
    public void mapAtoB(final NavigationEntryData a, final NavigationEntryWsDTO b, final MappingContext context) {
        // other fields are mapped automatically
        context.beginMappingField("localizedUrl", getAType(), a, "localizedUrl", getBType(), b);
        try {
            if (shouldMap(a, b, context) && a.getLocalizedUrl() != null) {
                b.setLocalizedUrl(headlessURLResolver.resolve(a.getLocalizedUrl()));
            }
        } finally {
            context.endMappingField();
        }
    }

    public void setHeadlessURLResolver(DistHeadlessResolver<String, String> headlessURLResolver) {
        this.headlessURLResolver = headlessURLResolver;
    }
}
