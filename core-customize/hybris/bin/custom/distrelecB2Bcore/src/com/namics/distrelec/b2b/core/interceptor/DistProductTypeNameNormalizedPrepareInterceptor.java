package com.namics.distrelec.b2b.core.interceptor;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DistProductTypeNameNormalizedPrepareInterceptor implements PrepareInterceptor<ProductModel> {

    private final String invalidCharsRegex = "[^a-zA-Z0-9]";

    @Override
    public void onPrepare(ProductModel productModel, InterceptorContext interceptorContext) throws InterceptorException {
        Map<String, Set<Locale>> dirtyAttrs = interceptorContext.getDirtyAttributes(productModel);
        if (dirtyAttrs.containsKey(ProductModel.TYPENAME)) {
            String typeName = productModel.getTypeName();
            String typeNameNormalized = normalizeTypeName(typeName);
            productModel.setTypeNameNormalized(typeNameNormalized);
        }
    }

    protected String normalizeTypeName(String typeName) {
        String typeNameNormalized;
        if (typeName != null) {
            typeNameNormalized = typeName.replaceAll(invalidCharsRegex, StringUtils.EMPTY);
        } else {
            typeNameNormalized = null;
        }
        return typeNameNormalized;
    }
}
