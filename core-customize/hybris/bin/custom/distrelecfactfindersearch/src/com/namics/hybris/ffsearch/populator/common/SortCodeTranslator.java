/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.common;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.GROSS;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.MIN;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.NET;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRICE;

import org.apache.commons.lang.StringUtils;

import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.factfinder.webservice.ws71.FFsearch.SortItem;

/**
 * Translator for {@link SortItem} names to their correct code value and vice versa.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class SortCodeTranslator {

    private static final String[] PRICE_SUFFIXES = { GROSS + ";" + MIN, NET + ";" + MIN };

    /**
     * Sorting of Prices needs to be done using the special code of the priceFilter attribute used when making the search request e.g.
     * "EUR;Gross;Min". </br>
     * 
     * For all other sortItems (e.g. Title or ProductNumber), the sort code is identical to their name.
     * 
     * @param sortItemName
     *            name of the sortItem to translate
     * @param source
     *            the search request made to obtain the sortItemName
     * @return a valid sort code with which the given sortItemName attribute can be sorted with FactFinder.
     */
    public static String getSortCode(final String sortItemName, final SearchRequest source) {
        return PriceFilterTranslator.getPriceSensitiveFacetCode(sortItemName, source);
    }

    /**
     * Sorting of Prices needs to be done using the special code of the priceFilter attribute used when making the search request e.g.
     * "EUR;Gross;Min". </br> This method translates such a code back to its name.
     * 
     * For all other sortItems (e.g. Title or ProductNumber), the sort name is identical to their name.
     * 
     * @param sortItemCode
     *            code of the sortItem to translate back to its name
     * @return the name of the sort attribute.
     */
    public static String getSortName(final String sortItemCode) {
        return isPriceSorting(sortItemCode) ? PRICE.getValue() : sortItemCode;
    }

    /**
     * Does the given sort item code conform to a price sorting value?
     * 
     * @param sortItemCode
     *            sort item code
     * @return <code>true</code> if the sortItem code ends with "Gross;Min" or "Net;Min", <code>false</code> otherwise.
     */
    public static boolean isPriceSorting(final String sortItemCode) {
        return StringUtils.endsWithAny(sortItemCode, PRICE_SUFFIXES);
    }

    /**
     * FactFinder is not able to return a selected and working price sorting option as selected -->
     * https://jira.namics.com/browse/DISTRELEC-883
     * 
     * @param resultSortItem
     *            The sorting option to check whether it is selected
     * @param searchRequest
     *            The search request with the requested sorting options.
     * @return True, if it was requested to sort by price and the resultSortItem is a price sort item. Otherwise: The value of the correctly
     *         returned selected item.
     */
    public static boolean isSelectedItem(final SortItem resultSortItem, final SearchRequest searchRequest) {
        if (searchRequest.getSearchParams().getSortsList() == null || searchRequest.getSearchParams().getSortsList().getSortItem().isEmpty()) {
            return resultSortItem.isSelected().booleanValue();
        }
        final SortItem requestSortItem = searchRequest.getSearchParams().getSortsList().getSortItem().get(0);
        if (isPriceSorting(requestSortItem.getName())) {
            final String requestSortName = getSortName(requestSortItem.getName());
            final String resultSortName = resultSortItem.getName();
            if (StringUtils.equals(requestSortName, resultSortName) && requestSortItem.getOrder().equals(resultSortItem.getOrder())) {
                return true;
            }
            // when a price sorting was requested, then the relevance sort wrongly gets returned as selected. fix that be returning false
            // manually.
            if (resultSortItem.isRelevanceSortItem().booleanValue()) {
                return false;
            }
        }
        return resultSortItem.isSelected().booleanValue();
    }

}
