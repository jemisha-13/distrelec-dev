package com.namics.distrelec.b2b.core.service.product.model;

import com.namics.distrelec.b2b.core.model.AbstractDynamicLocalizedAttributeHandler;
import de.hybris.platform.core.model.product.ProductModel;
import org.apache.commons.collections.CollectionUtils;

public class ProductDownloadsAvailableAttributeHandler extends AbstractDynamicLocalizedAttributeHandler<Boolean, ProductModel> {

    @Override
    public Boolean get(ProductModel model) {
        return CollectionUtils.isNotEmpty(model.getDownloadMedias());
    }
}
