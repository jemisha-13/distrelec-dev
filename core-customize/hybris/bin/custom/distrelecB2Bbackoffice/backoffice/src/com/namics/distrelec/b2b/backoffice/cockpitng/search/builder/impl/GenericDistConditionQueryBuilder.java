package com.namics.distrelec.b2b.backoffice.cockpitng.search.builder.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hybris.backoffice.cockpitng.search.builder.impl.GenericConditionQueryBuilder;
import com.hybris.backoffice.widgets.advancedsearch.engine.AdvancedSearchQueryData;
import com.hybris.backoffice.widgets.advancedsearch.engine.AdvancedSearchQueryData.Builder;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionDataList;
import com.hybris.cockpitng.core.config.CockpitConfigurationException;
import com.hybris.cockpitng.core.config.CockpitConfigurationService;
import com.hybris.cockpitng.core.config.ConfigContext;
import com.hybris.cockpitng.core.config.impl.DefaultConfigContext;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.Parameter;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.simplesearch.Field;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.simplesearch.SimpleSearch;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.simplesearch.SortField;
import com.hybris.cockpitng.dataaccess.context.impl.DefaultContext;
import com.hybris.cockpitng.dataaccess.facades.search.impl.FieldSearchFacadeStrategyRegistry;
import com.hybris.cockpitng.search.data.SearchAttributeDescriptor;
import com.hybris.cockpitng.search.data.SearchQueryCondition;
import com.hybris.cockpitng.search.data.SearchQueryConditionList;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.SortData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import com.namics.distrelec.b2b.backoffice.cockpitng.dataaccess.facades.search.DefaultDistPlatformFieldSearchFacadeStrategy;
import de.hybris.platform.core.GenericCondition;
import de.hybris.platform.core.GenericQuery;
import de.hybris.platform.core.GenericSearchField;
import de.hybris.platform.core.GenericSelectField;
import de.hybris.platform.core.Operator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.core.model.type.RelationMetaTypeModel;
import de.hybris.platform.genericsearch.GenericSearchQuery;
import de.hybris.platform.util.ItemPropertyValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class GenericDistConditionQueryBuilder extends GenericConditionQueryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(GenericDistConditionQueryBuilder.class);

    public static final String SIMPLE_SEARCH = "simple-search";

    private CockpitConfigurationService cockpitConfigurationService;

    private int pageSize = 50;

    @Override
    protected GenericCondition createRelationCondition(RelationDescriptorModel relationDescriptor, String typeCode,
            String qualifier, ValueComparisonOperator comparisonOperator, Object value) {

        if (BooleanUtils.isTrue(relationDescriptor.getRelationType().getAbstract())) {
            if (BooleanUtils.isTrue(relationDescriptor.getProperty()) && value instanceof String) {
                GenericCondition one2Many = createOne2ManyJoinCondition(relationDescriptor, typeCode, qualifier,
                    comparisonOperator, value);

                if (one2Many != null) {
                    return one2Many;
                }
            }
        }


        return super.createRelationCondition(relationDescriptor, typeCode, qualifier, comparisonOperator, value);
    }

    @Override
    protected GenericCondition createSingleTokenCondition(SearchQueryData searchQueryData,
            SearchAttributeDescriptor searchAttributeDescriptor, Object value, ValueComparisonOperator givenOperator) {
        String qualifier = searchAttributeDescriptor.getAttributeName();
        String typeCode = searchQueryData.getSearchType();
        AttributeDescriptorModel attDescriptorModel = getTypeService().getAttributeDescriptor(typeCode, qualifier);
        if (value instanceof String && attDescriptorModel.getAttributeType() instanceof ComposedTypeModel) {
            ValueComparisonOperator operator = givenOperator != null ? givenOperator : searchQueryData.getValueComparisonOperator(searchAttributeDescriptor);
            GenericCondition manyToOne = createManyToOneJoinCondition(attDescriptorModel, typeCode, qualifier, operator, value);
            return manyToOne;
        } else {
            return super.createSingleTokenCondition(searchQueryData, searchAttributeDescriptor, value, givenOperator);
        }
    }

    /**
     * just copied from superclass because it is private and can not be used by subclasses
     */
    private Operator lookupRelationOperator(ValueComparisonOperator comparisonOperator, boolean many2manyRelation) {
        switch(comparisonOperator) {
            case EQUALS:
            case STARTS_WITH:
            case ENDS_WITH:
            case LIKE:
            case CONTAINS:
            case IN:
                return Operator.IN;
            case DOES_NOT_CONTAIN:
                return Operator.NOT_IN;
            case IS_EMPTY:
                return many2manyRelation ? Operator.NOT_IN : Operator.IS_NULL;
            case IS_NOT_EMPTY:
                return many2manyRelation ? Operator.IN : Operator.IS_NOT_NULL;
            default:
                throw new IllegalStateException("Unsupported operator " + comparisonOperator + " for references");
        }
    }

    private GenericCondition createOne2ManyJoinCondition(RelationDescriptorModel relationDescriptor, String typeCode,
            String qualifier, ValueComparisonOperator comparisonOperator, Object value) {
        boolean isSource = BooleanUtils.isTrue(relationDescriptor.getIsSource());
        RelationMetaTypeModel relationType = relationDescriptor.getRelationType();
        ComposedTypeModel otherOneType = isSource ? relationType.getTargetType() : relationType.getSourceType();
        String otherTypeCode = otherOneType.getCode();

        String searchText = (String) value;

        AdvancedSearchData advancedSearchData = buildQueryData(searchText, otherTypeCode);
        AdvancedSearchQueryData searchQueryData = buildQueryData(advancedSearchData);

        DefaultContext context = new DefaultContext();
        DefaultDistPlatformFieldSearchFacadeStrategy fieldSearchFacadeStrategy =
            (DefaultDistPlatformFieldSearchFacadeStrategy) getFieldSearchFacadeStrategyRegistry().getStrategy(
                searchQueryData.getSearchType(), context);


        SearchQueryData adjustedSearchQueryData = fieldSearchFacadeStrategy.adjustSearchQuery(searchQueryData);
        adjustedSearchQueryData.setSortData(null);
        GenericSearchQuery subSearchQuery = fieldSearchFacadeStrategy.buildQuery(adjustedSearchQueryData);
        GenericQuery subQuery = subSearchQuery.getQuery();
        subQuery.addSelectField(new GenericSelectField(otherTypeCode, ItemModel.PK, ItemPropertyValue.class));

        Operator operator = lookupRelationOperator(comparisonOperator, false);

        return GenericCondition.createSubQueryCondition(new GenericSearchField(typeCode, qualifier), operator, subQuery);
    }

    private GenericCondition createManyToOneJoinCondition(AttributeDescriptorModel attrDescriptor, String typeCode,
            String qualifier, ValueComparisonOperator comparisonOperator, Object value) {
        String otherTypeCode = attrDescriptor.getAttributeType().getCode();
        String searchText = (String) value;

        AdvancedSearchData advancedSearchData = buildQueryData(searchText, otherTypeCode);
        AdvancedSearchQueryData searchQueryData = buildQueryData(advancedSearchData);

        DefaultContext context = new DefaultContext();
        DefaultDistPlatformFieldSearchFacadeStrategy fieldSearchFacadeStrategy =
            (DefaultDistPlatformFieldSearchFacadeStrategy) getFieldSearchFacadeStrategyRegistry().getStrategy(
                searchQueryData.getSearchType(), context);

        SearchQueryData adjustedSearchQueryData = fieldSearchFacadeStrategy.adjustSearchQuery(searchQueryData);
        adjustedSearchQueryData.setSortData(null);
        GenericSearchQuery subSearchQuery = fieldSearchFacadeStrategy.buildQuery(adjustedSearchQueryData);
        GenericQuery subQuery = subSearchQuery.getQuery();
        subQuery.addSelectField(new GenericSelectField(otherTypeCode, ItemModel.PK, ItemPropertyValue.class));

        Operator operator = lookupRelationOperator(comparisonOperator, false);

        return GenericCondition.createSubQueryCondition(new GenericSearchField(typeCode, qualifier), operator, subQuery);
    }

    public CockpitConfigurationService getCockpitConfigurationService() {
        return cockpitConfigurationService;
    }

    // from com.hybris.backoffice.widgets.advancedsearch.engine.AdvancedSearchEngineController

    protected AdvancedSearchQueryData buildQueryData(AdvancedSearchData searchData) {
        List<SearchQueryCondition> entries = new ArrayList();
        List<SearchQueryCondition> fqEntries = new ArrayList();
        searchData.getSearchFields().stream().filter((field) -> {
            return !"_orphanedSearchConditions".equals(field);
        }).forEach((field) -> {
            List<SearchConditionData> conditions = searchData.getConditions(field);
            entries.addAll(convertToQuery(conditions, field));
        });
        searchData.getFilterQueryFields().forEach((fqField) -> {
            List<SearchConditionData> fqConditions = searchData.getFilterQueryRawConditions(fqField);
            fqEntries.addAll(convertToFilterQuery(fqConditions, fqField));
        });
        List<SearchConditionData> orphanConditions = searchData.getConditions("_orphanedSearchConditions");
        Builder builder;
        if (CollectionUtils.isNotEmpty(fqEntries)) {
            builder = prepareQueryBuilder(searchData.getTypeCode(), searchData.getGlobalOperator(), Lists.newArrayList(new SearchQueryCondition[]{prepareFilteringConditionsList(searchData.getGlobalOperator(), fqEntries, entries, convertToQuery(orphanConditions, "_orphanedSearchConditions"))}));
        } else if (CollectionUtils.isNotEmpty(orphanConditions) && CollectionUtils.isNotEmpty(entries)) {
            SearchQueryConditionList entriesList = new SearchQueryConditionList(searchData.getGlobalOperator(), entries);
            SearchQueryConditionList orphansList = new SearchQueryConditionList(searchData.getGlobalOperator(), convertToQuery(orphanConditions, "_orphanedSearchConditions"));
            builder = prepareQueryBuilder(searchData.getTypeCode(), searchData.getGlobalOperator(), Arrays.asList(entriesList, orphansList));
        } else if (CollectionUtils.isNotEmpty(orphanConditions)) {
            builder = prepareQueryBuilder(searchData.getTypeCode(), searchData.getGlobalOperator(), convertToQuery(orphanConditions, "_orphanedSearchConditions"));
        } else {
            builder = prepareQueryBuilder(searchData.getTypeCode(), searchData.getGlobalOperator(), entries);
        }

        builder.pageSize(this.pageSize).sortData(searchData.getSortData());
        builder.includeSubtypes(BooleanUtils.toBoolean(searchData.getIncludeSubtypes())).tokenizable(searchData.isTokenizable());
        builder.searchQueryText(searchData.getSearchQueryText());
        builder.selectedFacets(searchData.getSelectedFacets());
        builder.advancedSearchMode(searchData.getAdvancedSearchMode());
        return builder.build();
    }

    private static SearchQueryConditionList prepareFilteringConditionsList(ValueComparisonOperator globalOperator, List<SearchQueryCondition> filteringConditions, List<SearchQueryCondition> conditions, List<SearchQueryCondition> orphanConditions) {
        if (CollectionUtils.isEmpty(conditions) && CollectionUtils.isEmpty(orphanConditions)) {
            return new SearchQueryConditionList(ValueComparisonOperator.AND, filteringConditions);
        } else {
            SearchQueryConditionList filteringConditionsList = new SearchQueryConditionList(ValueComparisonOperator.AND, new SearchQueryCondition[]{new SearchQueryConditionList(ValueComparisonOperator.AND, filteringConditions)});
            if (!CollectionUtils.isEmpty(conditions)) {
                filteringConditions.add(new SearchQueryConditionList(globalOperator, conditions));
            }

            if (!CollectionUtils.isEmpty(orphanConditions)) {
                filteringConditions.add(new SearchQueryConditionList(globalOperator, orphanConditions));
            }

            return filteringConditionsList;
        }
    }

    private static Builder prepareQueryBuilder(String typeCode, ValueComparisonOperator operator, List<SearchQueryCondition> conditions) {
        Builder builder = new Builder(typeCode);
        builder.conditions(conditions).globalOperator(operator);
        return builder;
    }

    private static List<SearchQueryCondition> convertToQuery(List<SearchConditionData> conditions, String field) {
        List<SearchQueryCondition> entries = new ArrayList();
        if (conditions != null) {
            for(int index = 0; index < conditions.size(); ++index) {
                SearchConditionData searchConditionData = (SearchConditionData)conditions.get(index);
                collectEntries(entries, searchConditionData, field, index);
            }
        }

        return entries;
    }

    private static List<SearchQueryCondition> convertToFilterQuery(List<SearchConditionData> conditions, String field) {
        List<SearchQueryCondition> queryConditions = convertToQuery(conditions, field);
        queryConditions.forEach((condition) -> {
            condition.setFilteringCondition(true);
        });
        return queryConditions;
    }

    private static void collectEntries(List<SearchQueryCondition> entries, SearchConditionData searchConditionData, String field, int index) {
        if (searchConditionData instanceof SearchConditionDataList) {
            SearchConditionDataList list = (SearchConditionDataList)searchConditionData;
            SearchQueryConditionList entryList = new SearchQueryConditionList();
            entryList.setOperator(list.getOperator());
            entryList.setConditions(new LinkedList());
            Iterator var6 = list.getConditions().iterator();

            while(var6.hasNext()) {
                SearchConditionData data = (SearchConditionData)var6.next();
                collectEntries(entryList.getConditions(), data, field, index);
            }

            entries.add(entryList);
        } else {
            getQueryEntry(entries, field, index, searchConditionData);
        }

    }

    private static void getQueryEntry(List<SearchQueryCondition> entries, String field, int index, SearchConditionData data) {
        Object value = data.getValue();
        ValueComparisonOperator operator = data.getOperator();
        if (operator == null || value == null && operator.isRequireValue()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Skipping %s since either value or operator is null.", field));
            }
        } else {
            Map<String, String> editorParameters = (Map)data.getFieldType().getEditorParameter().stream().collect(Collectors.toMap(Parameter::getName, Parameter::getValue));
            SearchQueryCondition entry = new SearchQueryCondition();
            entry.setDescriptor(new SearchAttributeDescriptor(data.getFieldType().getName(), index, editorParameters));
            entry.setOperator(operator);
            entry.setValue(value);
            entries.add(entry);
        }

    }

    // from DistAdvancedSearchController:

    protected AdvancedSearchData buildQueryData(String searchText, String typeCode) {
        AdvancedSearchData queryData = this.createAdvancedSearchDataWithInitContext();
        queryData.setTypeCode(typeCode);
        queryData.setSearchQueryText(searchText);
        if (queryData.getGlobalOperator() == null) {
            queryData.setGlobalOperator(ValueComparisonOperator.OR);
        }

        queryData.setTokenizable(true);
        queryData.setIncludeSubtypes(Boolean.TRUE);
        this.applySimpleSearchConfiguration(searchText, typeCode, queryData);
        return queryData;
    }

    protected AdvancedSearchData createAdvancedSearchDataWithInitContext() {
        return new AdvancedSearchData();
    }

    protected void applySimpleSearchConfiguration(String searchText, String typeCode, AdvancedSearchData queryData) {
        SimpleSearch searchConfiguration = this.loadSimpleConfiguration(typeCode);
        if (searchConfiguration != null) {
            SortField sortField = searchConfiguration.getSortField();
            if (sortField != null) {
                queryData.setSortData(new SortData(sortField.getName(), sortField.isAsc()));
            }

            if (StringUtils.isNotEmpty(searchText)) {
                List<Field> fieldList = searchConfiguration.getField();
                List<SearchConditionData> conditions = Lists.newArrayList();
                Iterator var8 = fieldList.iterator();

                while(var8.hasNext()) {
                    Field field = (Field)var8.next();
                    FieldType searchField = new FieldType();
                    searchField.setName(field.getName());
                    conditions.add(new SearchConditionData(searchField, searchText, ValueComparisonOperator.CONTAINS));
                }

                queryData.addConditionList(ValueComparisonOperator.OR, conditions);
            }

            queryData.setIncludeSubtypes(BooleanUtils.isTrue(searchConfiguration.isIncludeSubtypes()));
        }
    }

    protected SimpleSearch loadSimpleConfiguration(String type) {
        try {
            ConfigContext configContext = new DefaultConfigContext(SIMPLE_SEARCH, StringUtils.trim(type));
            SimpleSearch simpleSearch = getCockpitConfigurationService().loadConfiguration(configContext,
                    SimpleSearch.class);
            return simpleSearch;
        } catch (CockpitConfigurationException e) {
            LOG.warn("Unable to get cofiguration", e);
            return null;
        }
    }

    @Required
    public void setCockpitConfigurationService(CockpitConfigurationService cockpitConfigurationService) {
        this.cockpitConfigurationService = cockpitConfigurationService;
    }

    public FieldSearchFacadeStrategyRegistry getFieldSearchFacadeStrategyRegistry() {
        return null;
    }
}
