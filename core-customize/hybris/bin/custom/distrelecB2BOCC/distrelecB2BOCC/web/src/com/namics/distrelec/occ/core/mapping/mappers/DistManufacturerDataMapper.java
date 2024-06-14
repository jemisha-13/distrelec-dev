/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.mapping.mappers;

import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.occ.core.mapping.resolvers.DistHeadlessResolver;

import de.hybris.platform.commercewebservicescommons.dto.manufacturer.DistManufacturerWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import ma.glasnost.orika.MappingContext;

public class DistManufacturerDataMapper extends AbstractCustomMapper<DistManufacturerData, DistManufacturerWsDTO> {

    private DistHeadlessResolver<String, String> headlessURLResolver;

    @Override
    public void mapAtoB(final DistManufacturerData a, final DistManufacturerWsDTO b, final MappingContext context) {
        // other fields are mapped automatically
        context.beginMappingField("urlId", getAType(), a, "urlId", getBType(), b);
        try {
            if (shouldMap(a, b, context) && a.getUrlId() != null) {
                b.setUrlId(headlessURLResolver.resolve(a.getUrlId()));
            }
        } finally {
            context.endMappingField();
        }
    }

    public void setHeadlessURLResolver(DistHeadlessResolver<String, String> headlessURLResolver) {
        this.headlessURLResolver = headlessURLResolver;
    }
}
