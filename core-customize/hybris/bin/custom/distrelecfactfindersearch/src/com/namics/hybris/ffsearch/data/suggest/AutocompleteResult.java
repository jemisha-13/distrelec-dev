/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.suggest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class AutocompleteResult {

    private AutocompleteProductResult prods;
    private AutocompleteCategoryResult cats;
    private AutocompleteManufacturerResult mans;
    private AutocompleteTermResult terms;

    public AutocompleteResult() {
        this.prods = new AutocompleteProductResult();
        this.cats = new AutocompleteCategoryResult();
        this.mans = new AutocompleteManufacturerResult();
        this.terms = new AutocompleteTermResult();
    }

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public AutocompleteProductResult getProds() {
        return prods;
    }

    public void setProds(final AutocompleteProductResult prods) {
        this.prods = prods;
    }

    public AutocompleteCategoryResult getCats() {
        return cats;
    }

    public void setCats(final AutocompleteCategoryResult cats) {
        this.cats = cats;
    }

    public AutocompleteManufacturerResult getMans() {
        return mans;
    }

    public void setMans(final AutocompleteManufacturerResult mans) {
        this.mans = mans;
    }

    public AutocompleteTermResult getTerms() {
        return terms;
    }

    public void setTerms(final AutocompleteTermResult terms) {
        this.terms = terms;
    }

}
