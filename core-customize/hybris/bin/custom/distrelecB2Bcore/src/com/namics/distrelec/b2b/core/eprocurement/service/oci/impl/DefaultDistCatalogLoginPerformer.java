/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.oci.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.eprocurement.data.oci.DefaultDistSAPProduct;
import com.namics.distrelec.b2b.core.eprocurement.data.oci.DefaultDistSAPProductList;
import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService;
import com.namics.distrelec.b2b.core.eprocurement.service.oci.DistCatalogLoginPerformer;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.product.DistPriceService;
import com.namics.distrelec.distrelecoci.constants.DistrelecOciConstants;
import com.namics.distrelec.distrelecoci.data.DistSapProduct;
import com.namics.distrelec.distrelecoci.data.DistSapProductList;
import com.namics.distrelec.distrelecoci.exception.OciException;
import com.namics.distrelec.distrelecoci.utils.OutboundSection;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloConnectException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.util.WebSessionFunctions;

/**
 * Implement for <code>SAPProductList</code> to use models instead of jalo items.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DefaultDistCatalogLoginPerformer implements DistCatalogLoginPerformer {


    @Autowired
    private ModelService modelService;

    @Autowired
    private DistPriceService priceService;

    @Autowired
    @Qualifier("commercePriceService")
    private DistCommercePriceService commercePriceService;

    @Autowired
    private ProductService productService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    @Qualifier("distEProcurementCustomerConfigService")
    private DistEProcurementCustomerConfigService customerConfigService;

    @Override
    public void login(final HttpServletRequest request, final HttpServletResponse response, final OutboundSection outboundsection) throws OciException {
        Map<String, String> loginprops = new HashMap<String, String>();

        loginprops.put("user.principal", outboundsection.getField(DistrelecOciConstants.USERNAME_FIELD));
        loginprops.put("user.credentials", outboundsection.getField(DistrelecOciConstants.PASSWORD_FIELD));
        loginprops.put("user.pk", null);
        try {
            WebSessionFunctions.getSession(request).transfer(loginprops);
        } catch (JaloSecurityException localJaloSecurityException) {
            throw new OciException("Login failed.", 9, localJaloSecurityException);
        } catch (JaloInvalidParameterException localJaloInvalidParameterException) {
            throw new OciException("Wrong parameter in Map", 4, localJaloInvalidParameterException);
        } catch (JaloConnectException localJaloConnectException) {
            throw new OciException("Unable to get jalo session!", -1, localJaloConnectException);
        }
    }

    @Override
    public String getHookURLFieldName() {
        return DistrelecOciConstants.HOOK_URL_FIELD_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistCatalogLoginPerformer#backgroundSearch(java.util.List, boolean)
     */
    @Override
    public DistSapProductList backgroundSearch(final List<ProductModel> backgroundSearchPlist, final boolean ociSession) {
        // final StringBuilder queryString = new StringBuilder();
        // queryString.append("SELECT {p:").append(ProductModel.PK).append("} FROM {").append(ProductModel._TYPECODE).append(" AS p} WHERE {p:")
        // .append(ProductModel.CODE).append("} LIKE ?").append(DistrelecOciConstants.SEARCHSTRING).append(" OR {p:").append(ProductModel.CODEELFA)
        // .append("} LIKE ?").append(DistrelecOciConstants.SEARCHSTRING).append(" OR {p:").append(ProductModel.CODEMOVEX).append("} LIKE ?")
        // .append(DistrelecOciConstants.SEARCHSTRING).append(" OR {p:").append(ProductModel.CODENAVISION).append("} LIKE ?")
        // .append(DistrelecOciConstants.SEARCHSTRING).append(" OR {p:").append(ProductModel.NAME).append(":o} LIKE ?")
        // .append(DistrelecOciConstants.SEARCHSTRING).append(" OR {p:").append(ProductModel.DESCRIPTION).append(":o} LIKE ?")
        // .append(DistrelecOciConstants.SEARCHSTRING);
        //
        // final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
        // query.addQueryParameter(DistrelecOciConstants.SEARCHSTRING, "%" + searchstring + "%");
        // return new DefaultDistSAPProductList(flexibleSearchService.<ProductModel> search(query).getResult(), ociSession, priceService,
        // customerConfigService,
        // modelService, commercePriceService);

        return new DefaultDistSAPProductList(backgroundSearchPlist, ociSession, priceService, customerConfigService, modelService, commercePriceService);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistCatalogLoginPerformer#getProductInfoForValidation(java.lang.String,
     * double, boolean)
     */
    @Override
    public DistSapProduct getProductInfoForValidation(final String productID, final double quantity, final boolean ociSession) {
        final ProductModel product = productService.getProductForCode(productID);
        if (product != null) {
            return new DefaultDistSAPProduct(product, quantity, ociSession, priceService, customerConfigService, commercePriceService);
        }

        return null;
    }


    // Getters & Setters

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DistPriceService getPriceService() {
        return priceService;
    }

    public void setPriceService(final DistPriceService priceService) {
        this.priceService = priceService;
    }

    public DistCommercePriceService getCommercePriceService() {
        return commercePriceService;
    }

    public void setCommercePriceService(final DistCommercePriceService commercePriceService) {
        this.commercePriceService = commercePriceService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DistEProcurementCustomerConfigService getCustomerConfigService() {
        return customerConfigService;
    }

    public void setCustomerConfigService(final DistEProcurementCustomerConfigService customerConfigService) {
        this.customerConfigService = customerConfigService;
    }
}
