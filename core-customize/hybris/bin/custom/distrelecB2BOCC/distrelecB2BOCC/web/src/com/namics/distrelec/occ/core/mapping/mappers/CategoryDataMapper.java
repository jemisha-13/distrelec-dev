/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.mapping.mappers;

import com.namics.distrelec.occ.core.mapping.resolvers.DistHeadlessResolver;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercewebservicescommons.dto.product.CategoryWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import ma.glasnost.orika.MappingContext;

public class CategoryDataMapper extends AbstractCustomMapper<CategoryData, CategoryWsDTO> {

    private DistHeadlessResolver<String, String> headlessURLResolver;

    @Override
    public void mapAtoB(final CategoryData a, final CategoryWsDTO b, final MappingContext context) {
        // other fields are mapped automatically
        context.beginMappingField("url", getAType(), a, "url", getBType(), b);
        try {
            if (shouldMap(a, b, context) && a.getUrl() != null) {
                b.setUrl(headlessURLResolver.resolve(a.getUrl()));
            }
        } finally {
            context.endMappingField();
        }
    }

    public void setHeadlessURLResolver(DistHeadlessResolver<String, String> headlessURLResolver) {
        this.headlessURLResolver = headlessURLResolver;
    }
}
