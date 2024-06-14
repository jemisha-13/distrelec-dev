/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.cms2.components.DistExtCarpetComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistExtCarpetItemModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * {@code DistExtCarpetComponentValidateInterceptor}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistExtCarpetComponentValidateInterceptor implements ValidateInterceptor {

    private static final Integer MIN_PRODUCTS = Integer.valueOf(4);

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    /*
     * (non-Javadoc)
     * @see de.hybris.platform.servicelayer.interceptor.ValidateInterceptor#onValidate(java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onValidate(Object model, InterceptorContext ctx) throws InterceptorException {
        if (!(model instanceof DistExtCarpetComponentModel)) {
            return;
        }
        final DistExtCarpetComponentModel component = (DistExtCarpetComponentModel) model;
        // Check if the customer use only one option
        checkSelectedOption(component);

        if (!StringUtils.isBlank(component.getSearchQuery())) {
            // Query
            checkSearchQueryResults(component);
        } else {
            // Check the sizes of the different columns
            checkColumnsLength(component);
            // Check if an item or a product is used many times in the carpet
            checkUniqueItem(component);
        }
    }

    /**
     * Check the size of the different columns.
     * 
     * @param component
     * @throws InterceptorException
     */
    private void checkColumnsLength(final DistExtCarpetComponentModel component) throws InterceptorException {
        final int sizeColumn1 = component.getCarpetColumn1Items().size();

        // Compare the sizes of different columns
        if (sizeColumn1 != component.getCarpetColumn2Items().size() || sizeColumn1 != component.getCarpetColumn3Items().size()
                || sizeColumn1 != component.getCarpetColumn4Items().size()) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.colomnlengthnotequal"));
        }
    }

    /**
     * Check the selected options
     * 
     * @param component
     * @throws InterceptorException
     */
    private void checkSelectedOption(final DistExtCarpetComponentModel component) throws InterceptorException {
        // One option must be selected
        if (StringUtils.isBlank(component.getSearchQuery())) {
            if (CollectionUtils.isEmpty(component.getCarpetColumn1Items()) || CollectionUtils.isEmpty(component.getCarpetColumn2Items())
                    || CollectionUtils.isEmpty(component.getCarpetColumn3Items()) || CollectionUtils.isEmpty(component.getCarpetColumn4Items())) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.nooptionselected"));
            }
        } else if (CollectionUtils.isNotEmpty(component.getCarpetColumn1Items()) || CollectionUtils.isNotEmpty(component.getCarpetColumn2Items())
                || CollectionUtils.isNotEmpty(component.getCarpetColumn3Items()) || CollectionUtils.isNotEmpty(component.getCarpetColumn4Items())) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.multipleoptionsselected"));
        }
    }

    private void checkSearchQueryResults(final DistExtCarpetComponentModel component) throws InterceptorException {
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

    /**
     * Check that the selected products are unique
     * 
     * @param component
     * @throws InterceptorException
     */
    private void checkUniqueItem(final DistExtCarpetComponentModel component) throws InterceptorException {
        final List<DistExtCarpetItemModel> items = new ArrayList<DistExtCarpetItemModel>();
        final List<String> productCodes = new ArrayList<String>();

        // Add all items
        items.addAll(component.getCarpetColumn1Items());
        items.addAll(component.getCarpetColumn2Items());
        items.addAll(component.getCarpetColumn3Items());
        items.addAll(component.getCarpetColumn4Items());

        for (final DistExtCarpetItemModel item : items) {
            if (item.getProduct() != null) {
                if (productCodes.contains(item.getProduct().getCode())) {
                    throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.duplicateitem"));
                } else {
                    productCodes.add(item.getProduct().getCode());
                }
            }
        }
    }
}
