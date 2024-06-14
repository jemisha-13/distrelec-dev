package com.namics.distrelec.b2b.facades.order.converters;

import com.namics.distrelec.b2b.core.model.order.SubOrderEntryModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.order.data.SubOrderEntryData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;

public class SubOrderEntryDataConverter extends AbstractPopulatingConverter<SubOrderEntryModel, SubOrderEntryData> {

    @Autowired
    private DistProductService distProductService;

    @Autowired
    @Qualifier("defaultProductService")
    private ProductService defaultDistProductService;

    @Autowired
    @Qualifier("productFacade")
    protected DistrelecProductFacade productFacade;

    protected static final Logger LOG = Logger.getLogger(SubOrderEntryDataConverter.class);

    @Override
    public void populate(final SubOrderEntryModel source, final SubOrderEntryData target) {
        target.setImageUrl(source.getImageUrl());
        target.setMaterialName(source.getMaterialName());
        target.setMaterialNumber(source.getMaterialNumber());
        if (source.getOrderQuantity() != null) {
            target.setOrderQuantity(source.getOrderQuantity().intValue());
        }

        try {
            final ProductModel product = getDistProductService().getProductForCode(source.getMaterialNumber());
            if (product != null) {
                target.setProduct(getProductFacade().getProductForCodeAndOptions(product.getCode(), Arrays.asList(ProductOption.BASIC, ProductOption.SUMMARY,
                        ProductOption.DESCRIPTION, ProductOption.PROMOTION_LABELS, ProductOption.DIST_MANUFACTURER)));
                if (target.getProduct().getManufacturer() == null && product.getManufacturer() != null) {
                    target.getProduct().setManufacturer(product.getManufacturer().getName());
                }
            }
        } catch (final Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        super.populate(source, target);
    }

    public DistProductService getDistProductService() {
        return distProductService;
    }

    public void setDistProductService(final DistProductService distProductService) {
        this.distProductService = distProductService;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

}
