package com.namics.distrelec.b2b.facades.product.converters.populator;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.platform.b2b.enums.B2BPermissionTypeEnum;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistProductBulkPermissionPopulator<SOURCE extends ProductModel, TARGET extends ProductData> implements Populator<SOURCE, TARGET> {

    @Autowired
    private DistrelecProductFacade productFacade;

    @Autowired
    private DistCustomerFacade customerFacade;

    @Override
    public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException {
        productData.setAllowBulk(isAllowBulk(productModel));
    }

    private boolean isAllowBulk(SOURCE productModel) {
        return productFacade.isProductBuyable(productModel.getCode()) && hasB2BQuotationRequestPermission();
    }

    private boolean hasB2BQuotationRequestPermission() {
        final CustomerData customer = customerFacade.getCurrentCustomer();
        List<B2BPermissionData> permissions = ofNullable(customer.getPermissions())
                                                                                   .orElse(emptyList());
        return permissions.stream()
                          .anyMatch(this::isQuoteRequestPermission);
    }

    private boolean isQuoteRequestPermission(B2BPermissionData permission) {
        return permission.isActive() && permission.getB2BPermissionTypeData() != null
                && B2BPermissionTypeEnum.DISTB2BREQUESTQUOTATIONPERMISSION.getCode().equals(permission.getB2BPermissionTypeData().getCode());
    }

}
