package com.namics.distrelec.b2b.facades.order.quotation.converters;

import com.namics.distrelec.b2b.core.model.order.SubOrderEntryModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationEntry;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;

public class QuotationEntries2SubOrderEntryModelConverter extends AbstractPopulatingConverter<QuotationEntry, SubOrderEntryModel> {

    @Autowired
    private DistProductService distProductService;

    @Override
    protected SubOrderEntryModel createTarget() {
        return new SubOrderEntryModel();
    }

    @Override
    public void populate(final QuotationEntry source, final SubOrderEntryModel target) {
        try {
            final ProductModel productForCode = getDistProductService().getProductForCode(source.getProduct().getCode());
            if (productForCode != null && productForCode.getGalleryImages() != null && productForCode.getGalleryImages().size() > 0
                    && productForCode.getGalleryImages().get(0) != null) {
                target.setImageUrl(productForCode.getGalleryImages().get(0).getName());
            }

            target.setMaterialName(productForCode.getName());
        } catch (final Exception e) {
            // NOP
        }
        target.setMaterialNumber(source.getProduct().getCode());
        target.setOrderQuantity(source.getQuantity());
        super.populate(source, target);
    }

    public DistProductService getDistProductService() {
        return distProductService;
    }

    public void setDistProductService(final DistProductService distProductService) {
        this.distProductService = distProductService;
    }

}
