package com.namics.distrelec.b2b.facades.order.converters;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.store.BaseStoreModel;

public class DistBaseStoreConverter extends AbstractPopulatingConverter<BaseStoreModel, BaseStoreData> {

    private DistBaseSiteConverter baseSiteConverter;

    @Override
    public void populate(final BaseStoreModel source, final BaseStoreData target) {
        target.setName(source.getName());
        target.setQuotationsEnabled(BooleanUtils.toBoolean(source.getQuotationsEnabled()));
        target.setOrderApprovalEnabled(BooleanUtils.toBoolean(source.getOrderApprovalEnabled()));
        target.setCmsSites(getBaseSiteData(source));
    }

    private List<BaseSiteData> getBaseSiteData(BaseStoreModel source) {
        return source.getCmsSites()
                     .stream()
                     .filter(Objects::nonNull)
                     .map(site -> baseSiteConverter.convert(site))
                     .collect(Collectors.toList());
    }

    public DistBaseSiteConverter getBaseSiteConverter() {
        return baseSiteConverter;
    }

    public void setBaseSiteConverter(final DistBaseSiteConverter baseSiteConverter) {
        this.baseSiteConverter = baseSiteConverter;
    }

}
