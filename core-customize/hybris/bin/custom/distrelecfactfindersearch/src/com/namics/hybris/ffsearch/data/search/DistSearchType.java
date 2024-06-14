/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.search;

/**
 * Enum representing the type of a search.
 * 
 * - Text (normal text search) <br/>
 * - Category (search with a category code, limited on the category field) <br/>
 * - Manufacturer (search with a manufacturer code, limited on the manufacturer field) <br/>
 * - Outlet (search for outlet products, additional filter with
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.22
 */
public enum DistSearchType {

    TEXT, CATEGORY, CATEGORY_AND_TEXT, MANUFACTURER, OUTLET, NEW

}
