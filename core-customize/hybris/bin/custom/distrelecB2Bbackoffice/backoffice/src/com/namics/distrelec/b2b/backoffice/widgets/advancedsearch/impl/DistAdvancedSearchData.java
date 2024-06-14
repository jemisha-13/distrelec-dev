package com.namics.distrelec.b2b.backoffice.widgets.advancedsearch.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldListType;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;

public class DistAdvancedSearchData extends AdvancedSearchData {

    private SearchConditionData additionalCondition;

    public DistAdvancedSearchData(FieldListType fieldList) {
        super(fieldList);
    }

    public DistAdvancedSearchData(AdvancedSearchData advancedSearchData) {
        super(advancedSearchData);
    }

    public void addAdditionalCondition(FieldType field, ValueComparisonOperator operator, Object value) {
        additionalCondition = new SearchConditionData(field, value, operator);
    }

    public void updateOperatorOfAdditionalCondition(ValueComparisonOperator operator) {
        SearchConditionData additionalCondition = getAdditionalCondition();
        if (additionalCondition != null) {
            additionalCondition.updateOperator(operator);
        }
    }
    public SearchConditionData getAdditionalCondition() {
        return additionalCondition;
    }

    public void clearAdditionalCondition() {
        additionalCondition = null;
    }

    @Override
    public List<SearchConditionData> getConditions(String name) {
        List<SearchConditionData> originalConditions = super.getConditions(name);

        SearchConditionData additionalCondition = getAdditionalCondition();
        if (additionalCondition != null && name.equals(additionalCondition.getFieldType().getName())) {
            List<SearchConditionData> conditions = new ArrayList<>();
            if (originalConditions != null) {
                conditions.addAll(originalConditions);
            }
            conditions.add(additionalCondition);
            return conditions;
        }

        return originalConditions;
    }

    public Set<String> getOriginalSearchFields() {
        return super.getSearchFields();
    }

    @Override
    public Set<String> getSearchFields() {
        SearchConditionData additionalCondition = getAdditionalCondition();
        if (additionalCondition != null) {
            Set<String> searchFields = new LinkedHashSet<>(super.getSearchFields());
            searchFields.add(additionalCondition.getFieldType().getName());
            return Collections.unmodifiableSet(searchFields);
        }

        return super.getSearchFields();
    }

    @Override
    public SearchConditionData getCondition(int index) {
        try {
            return super.getCondition(index);
        } catch (IllegalStateException e) {
            SearchConditionData additionalCondition = getAdditionalCondition();
            if (additionalCondition != null) {
                return additionalCondition;
            } else {
                throw new IllegalStateException("condition is not there");
            }
        }
    }
}
