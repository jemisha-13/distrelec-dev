package com.namics.distrelec.b2b.backoffice.cockpitng.editor.commonreferenceeditor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hybris.cockpitng.search.data.SearchAttributeDescriptor;
import com.hybris.cockpitng.search.data.SearchQueryCondition;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.SimpleSearchQueryData;
import com.hybris.cockpitng.search.data.SortData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;

/**
 * SimpleSearchQueryData has enabled search query tokenisation. This class wraps it to disable it, because it is
 * more suitable for us.
 */
public class NotTokenizableSearchQueryDataWrapper implements SearchQueryData {

    private final SimpleSearchQueryData original;

    NotTokenizableSearchQueryDataWrapper(final SimpleSearchQueryData original) {
        this.original = original;
    }

    @Override
    public Set<SearchAttributeDescriptor> getAttributes() {
        return original.getAttributes();
    }

    @Override
    public String getSearchType() {
        return original.getSearchType();
    }

    @Override
    public boolean isIncludeSubtypes() {
        return original.isIncludeSubtypes();
    }

    @Override
    public int getPageSize() {
        return original.getPageSize();
    }

    @Override
    public SortData getSortData() {
        return original.getSortData();
    }

    @Override
    public void setSortData(SortData sortData) {
        original.setSortData(sortData);
    }

    @Override
    public ValueComparisonOperator getValueComparisonOperator(SearchAttributeDescriptor searchAttributeDescriptor) {
        return original.getValueComparisonOperator(searchAttributeDescriptor);
    }

    @Override
    public Object getAttributeValue(SearchAttributeDescriptor searchAttributeDescriptor) {
        return original.getAttributeValue(searchAttributeDescriptor);
    }

    @Override
    public ValueComparisonOperator getGlobalComparisonOperator() {
        return original.getGlobalComparisonOperator();
    }

    @Override
    public boolean isTokenizable() {
        // returns false, while original class returns true
        return false;
    }

    @Override
    public List<? extends SearchQueryCondition> getConditions() {
        return original.getConditions();
    }

    @Override
    public String getSearchQueryText() {
        return original.getSearchQueryText();
    }

    @Override
    public Map<String, Set<String>> getSelectedFacets() {
        return original.getSelectedFacets();
    }
}
