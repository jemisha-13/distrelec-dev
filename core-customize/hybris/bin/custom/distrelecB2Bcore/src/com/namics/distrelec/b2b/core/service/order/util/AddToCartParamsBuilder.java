package com.namics.distrelec.b2b.core.service.order.util;

import java.util.Set;

import org.apache.commons.lang.BooleanUtils;

import de.hybris.platform.commercefacades.order.data.AddToCartParams;

public class AddToCartParamsBuilder {

    private String productCode;

    private long quantity;

    private String storeId;

    private Set<Integer> entryGroupNumbers;

    private String searchQuery;

    private Boolean recalculate;

    private String reference;

    private String addedFrom;

    public AddToCartParamsBuilder() {
    }

    public static AddToCartParamsBuilder aAddToCartParamsBuilder() {
        return new AddToCartParamsBuilder();
    }

    public AddToCartParamsBuilder withProductCode(String productCode) {
        this.productCode = productCode;
        return this;
    }

    public AddToCartParamsBuilder withQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public AddToCartParamsBuilder withStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public AddToCartParamsBuilder withEntryGroupNumbers(Set<Integer> entryGroupNumbers) {
        this.entryGroupNumbers = entryGroupNumbers;
        return this;
    }

    public AddToCartParamsBuilder withSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        return this;
    }

    public AddToCartParamsBuilder withRecalculate(Boolean recalculate) {
        this.recalculate = recalculate;
        return this;
    }

    public AddToCartParamsBuilder withReference(String reference) {
        this.reference = reference;
        return this;
    }

    public AddToCartParamsBuilder withAddedFrom(String addedFrom) {
        this.addedFrom = addedFrom;
        return this;
    }

    public AddToCartParams build() {
        AddToCartParams addToCartParams = new AddToCartParams();
        addToCartParams.setProductCode(productCode);
        addToCartParams.setQuantity(quantity);
        addToCartParams.setStoreId(storeId);
        addToCartParams.setEntryGroupNumbers(entryGroupNumbers);
        addToCartParams.setReference(reference);
        addToCartParams.setRecalculate(BooleanUtils.toBoolean(recalculate));
        addToCartParams.setSearchQuery(searchQuery);
        addToCartParams.setAddedFrom(addedFrom);
        return addToCartParams;
    }
}
