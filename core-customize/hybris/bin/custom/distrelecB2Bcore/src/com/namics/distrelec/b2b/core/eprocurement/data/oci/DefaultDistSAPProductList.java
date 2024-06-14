/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.data.oci;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.BooleanUtils;

import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService;
import com.namics.distrelec.b2b.core.inout.erp.OrderCalculationService;
import com.namics.distrelec.b2b.core.model.eprocurement.OCICustomerConfigModel;
import com.namics.distrelec.b2b.core.model.eprocurement.OciShippingConfigModel;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.product.DistPriceService;
import com.namics.distrelec.distrelecoci.data.DistSapProduct;
import com.namics.distrelec.distrelecoci.data.DistSapProductList;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Implement for {@link DistSapProductList} to use models instead of jalo items
 * 
 * @author pbueschi, Namics AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DefaultDistSAPProductList implements DistSapProductList {

    private final AbstractOrderModel cart;
    private final List<DistSapProduct> entries;
    private final Map<String, String> headers;

    /**
     * Create a new instance of {@code DefaultDistSAPProductList}
     * 
     * @param products
     * @param ociSession
     * @param priceService
     * @param customerConfigService
     * @param modelService
     * @param commercePriceService
     */
    public DefaultDistSAPProductList(final List<ProductModel> products, final boolean ociSession, final DistPriceService priceService,
            final DistEProcurementCustomerConfigService customerConfigService, final ModelService modelService,
            final DistCommercePriceService commercePriceService) {
        this.cart = null;
        this.headers = customerConfigService.getHeaderFields();
        this.entries = products.stream()
                .map(product -> new DefaultDistSAPProduct(product, ociSession, priceService, customerConfigService, commercePriceService))
                .collect(Collectors.toList());

        addShippingProductForCustomerConfig(ociSession, customerConfigService, modelService, commercePriceService);
    }

    /**
     * Create a new instance of {@code DefaultDistSAPProductList}
     * 
     * @param cart
     * @param ociSession
     * @param priceService
     * @param orderCalculationService
     * @param customerConfigService
     * @param modelService
     * @param commercePriceService
     * @throws CalculationException
     */
    public DefaultDistSAPProductList(final AbstractOrderModel cart, final boolean ociSession, final DistPriceService priceService,
            final OrderCalculationService orderCalculationService, final DistEProcurementCustomerConfigService customerConfigService,
            final ModelService modelService, final DistCommercePriceService commercePriceService) throws CalculationException {
        this.cart = cart;
        if (!BooleanUtils.isTrue(this.cart.getCalculated())) {
            orderCalculationService.calculate(this.cart, true);
        }

        this.headers = customerConfigService.getHeaderFields();
        this.entries = this.cart.getEntries().stream()
                .map(cartEntry -> new DefaultDistSAPProduct(cartEntry, ociSession, priceService, customerConfigService, commercePriceService))
                .collect(Collectors.toList());

        addShippingProductForCustomerConfig(ociSession, customerConfigService, modelService, commercePriceService);
    }

    /**
     * Add additional shipping product if defined in customer config for the current customer.
     * 
     * @param customerConfigService
     */
    private void addShippingProductForCustomerConfig(final boolean ociSession, final DistEProcurementCustomerConfigService customerConfigService,
            final ModelService modelService, final DistCommercePriceService commercePriceService) {
        if (customerConfigService.hasShippingProduct()) {
            final OCICustomerConfigModel ociCustomerConfig = (OCICustomerConfigModel) customerConfigService.getCustomerConfig();
            final OciShippingConfigModel shippingConfig = ociCustomerConfig.getShippingConfig();
            final ProductModel shippingProduct = (ProductModel) modelService.create(ProductModel.class);
            shippingProduct.setCode(shippingConfig.getArticleNumber());
            shippingProduct.setName(shippingConfig.getText());
            entries.add(new DefaultDistSAPProduct(shippingProduct, ociSession, null, customerConfigService, commercePriceService));
        }
    }

    @Override
    public DistSapProduct getProduct(final int index) {
        return this.entries.get(index);
    }

    @Override
    public int size() {
        return this.entries.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.distrelecoci.data.DistSapProductList#getHeaders()
     */
    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
