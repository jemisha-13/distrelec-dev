package com.namics.distrelec.b2b.core.service.product.model;

import java.util.Optional;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

public class CalibrationProductAttributeHandler extends AbstractDynamicAttributeHandler<ProductModel, ProductModel> {

    private final Logger LOG = LoggerFactory.getLogger(CalibrationProductAttributeHandler.class);

    /**
     * Returns not-calibrated or calibrated version of the product.
     */
    @Override
    public ProductModel get(ProductModel productModel) {
        if (isTrue(productModel.getCalibrationService())) {

            Optional<ProductReferenceModel> productRef = productModel.getProductReferences().stream()
                .filter(ProductReferenceModel::getActive)
                .filter(pr -> pr.getReferenceType().equals(ProductReferenceTypeEnum.DIS_ALTERNATIVE_CALIBRATED))
                .findAny();
            if (productRef.isPresent()) {
                ProductReferenceModel productReference = productRef.get();
                return productReference.getTarget();
            } else {
                LOG.warn("Alternative calibration product is not found for product: " + productModel.getCode());
            }
        }

        return null;
    }
}
