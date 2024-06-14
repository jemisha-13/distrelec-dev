/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.enums.DistCarpetItemSize;
import com.namics.distrelec.b2b.core.model.cms2.components.DistCarpetComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetItemModel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * Check if manual or search products are defined, not manual and search products are defined and at least 5 products are manual added.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistCarpetComponentValidateInterceptor implements ValidateInterceptor {

    private static final Integer MIN_PRODUCTS = Integer.valueOf(5);

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    private void checkSelectedOption(final DistCarpetComponentModel component) throws InterceptorException {
        // One option must be selected
        if (StringUtils.isBlank(component.getSearchQuery())
                && ((component.getCarpetColumn1Items() == null || component.getCarpetColumn1Items().isEmpty())
                        || (component.getCarpetColumn2Items() == null || component.getCarpetColumn2Items().isEmpty()) || (component.getCarpetColumn3Items() == null || component
                        .getCarpetColumn3Items().isEmpty()))) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.nooptionselected"));
        }

        // Only one option must be selected
        if (((component.getCarpetColumn1Items() != null && !component.getCarpetColumn1Items().isEmpty())
                || (component.getCarpetColumn2Items() != null && !component.getCarpetColumn2Items().isEmpty()) || (component.getCarpetColumn3Items() != null && !component
                .getCarpetColumn3Items().isEmpty())) && (StringUtils.isNotBlank(component.getSearchQuery()))) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.multipleoptionsselected"));

        }
    }

    private void checkColumnsLength(final DistCarpetComponentModel component) throws InterceptorException {
        if (component.getCarpetColumn1Items().size() + component.getCarpetColumn2Items().size() + component.getCarpetColumn3Items().size() != 0) {
            int sizeColumn1 = 0;
            int sizeColumn2 = 0;
            int sizeColumn3 = 0;

            for (DistCarpetItemModel item : component.getCarpetColumn1Items()) {
                if (item.getSize() == DistCarpetItemSize.SMALL) {
                    sizeColumn1 += 1;
                } else { // DistCarpetItemSize.LARGE
                    sizeColumn1 += 2;
                }
            }
            for (DistCarpetItemModel item : component.getCarpetColumn2Items()) {
                if (item.getSize() == DistCarpetItemSize.SMALL) {
                    sizeColumn2 += 1;
                } else { // DistCarpetItemSize.LARGE
                    sizeColumn2 += 2;
                }
            }
            // Compare col 1 vs col2
            if (sizeColumn1 != sizeColumn2) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.columnslengthnotequal"));
            }

            for (DistCarpetItemModel item : component.getCarpetColumn3Items()) {
                if (item.getSize() == DistCarpetItemSize.SMALL) {
                    sizeColumn3 += 1;
                } else { // DistCarpetItemSize.LARGE
                    sizeColumn3 += 2;
                }
            }
            // Compare col1 vs vol3 & col2 vs col3
            if (sizeColumn1 != sizeColumn3 || sizeColumn2 != sizeColumn3) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.columnslengthnotequal"));
            }
        }
    }

    private void checkUniqueItem(final DistCarpetComponentModel component) throws InterceptorException {
        final LinkedHashSet<DistCarpetItemModel> items = new LinkedHashSet<DistCarpetItemModel>();

        // Add all items
        items.addAll(component.getCarpetColumn1Items());
        items.addAll(component.getCarpetColumn2Items());
        items.addAll(component.getCarpetColumn3Items());

        // Check the size (LinkedHashSet will not duplicate the items)
        if (items.size() != component.getCarpetColumn1Items().size() + component.getCarpetColumn2Items().size() + component.getCarpetColumn3Items().size()) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.duplicateitem"));
        }

        final List<ProductModel> products = new ArrayList<ProductModel>();

        for (DistCarpetItemModel item : component.getCarpetColumn1Items()) {
            if (item.getProduct() != null && products.contains(item.getProduct())) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.duplicateproduct"));
            }
            products.add(item.getProduct());
        }

        for (DistCarpetItemModel item : component.getCarpetColumn2Items()) {
            if (item.getProduct() != null && products.contains(item.getProduct())) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.duplicateproduct"));
            }
            products.add(item.getProduct());
        }

        for (DistCarpetItemModel item : component.getCarpetColumn3Items()) {
            if (item.getProduct() != null && products.contains(item.getProduct())) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.duplicateproduct"));
            }
            products.add(item.getProduct());
        }
    }

    private void checkSearchQueryResults(final DistCarpetComponentModel component) throws InterceptorException {
        if (StringUtils.isNotBlank(component.getSearchQuery())) {
            // Check the max value
            if (component.getMaxSearchResults() == null) {
                component.setMaxSearchResults(MIN_PRODUCTS);
            }

            // Check the minimum values
            if (component.getMaxSearchResults().compareTo(MIN_PRODUCTS) < 0) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.lessminsearchresults", new Object[] { MIN_PRODUCTS }));
            }
        }
    }

    public void afterSave(final Object model) throws InterceptorException {
        final DistCarpetComponentModel component = (DistCarpetComponentModel) model;
        // Check if the customer use only one option
        checkSelectedOption(component);

        if (!StringUtils.isBlank(component.getSearchQuery())) {
            // Query
            checkSearchQueryResults(component);
        } else {
            // Check the content
            // Columns size
            // checkColumnsLength(component);
            // Check if an item or a product is used many times in the carpet
            checkUniqueItem(component);
        }

    }

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        final DistCarpetComponentModel component = (DistCarpetComponentModel) model;
        // Check if the customer use only one option
        checkSelectedOption(component);

        if (!StringUtils.isBlank(component.getSearchQuery())) {
            // Query
            checkSearchQueryResults(component);
        } else {
            // Check the content
            // Columns size
            // checkColumnsLength(component);
            // Check if an item or a product is used many times in the carpet
            checkUniqueItem(component);
        }
        // nothing else to check for the moment
    }
}
