/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import com.namics.distrelec.b2b.core.model.DistDocumentTypeModel;
import com.namics.distrelec.b2b.facades.product.data.DistDownloadSectionData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

public class DistDocumentTypeConverter extends AbstractPopulatingConverter<DistDocumentTypeModel, DistDownloadSectionData> {

    @Override
    protected DistDownloadSectionData createTarget() {
        return new DistDownloadSectionData();
    }

    @Override
    public void populate(final DistDocumentTypeModel source, final DistDownloadSectionData target) {
        target.setCode(source.getCode());
        target.setTitle(source.getName());
        target.setDescription(source.getDescription());
        target.setRank(source.getRank());

        super.populate(source, target);
    }

}
