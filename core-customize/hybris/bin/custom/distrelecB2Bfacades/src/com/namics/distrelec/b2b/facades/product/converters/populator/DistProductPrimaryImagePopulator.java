package com.namics.distrelec.b2b.facades.product.converters.populator;

import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;

public class DistProductPrimaryImagePopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends ProductPrimaryImagePopulator<SOURCE, TARGET> {

    protected MediaContainerModel getPrimaryImageMediaContainer(final SOURCE productModel) {
        final MediaContainerModel picture = (MediaContainerModel) getProductAttribute(productModel, ProductModel.PRIMARYIMAGE);
        return picture;
    }

}
