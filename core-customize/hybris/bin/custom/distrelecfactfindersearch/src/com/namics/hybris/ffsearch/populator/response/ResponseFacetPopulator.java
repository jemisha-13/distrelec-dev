package com.namics.hybris.ffsearch.populator.response;

import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.FACTFINDER_UNIT_PREFIX;
import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.MIN_MAX_FILTERS_REGEX;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.EMPTY;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_PATTERN;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.MANUFACTURER;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Charsets;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetType;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.populator.common.PriceFilterTranslator;
import com.namics.hybris.ffsearch.populator.common.SortCodeTranslator;
import com.namics.hybris.ffsearch.populator.response.helper.SearchQueryTransformer;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfGroupElement;
import de.factfinder.webservice.ws71.FFsearch.Filter;
import de.factfinder.webservice.ws71.FFsearch.FilterStyle;
import de.factfinder.webservice.ws71.FFsearch.Group;
import de.factfinder.webservice.ws71.FFsearch.GroupElement;
import de.factfinder.webservice.ws71.FFsearch.SliderGroupElement;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class ResponseFacetPopulator {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseFacetPopulator.class);

    private double minSliderRange;

    private SearchQueryTransformer queryTransformer;

    private CommonI18NService commonI18NService;

    public void populate(final long totalResultCount,
                         final List<Group> sourceFacets,
                         final SearchQueryData queryData,
                         final List<FactFinderFacetData<SearchQueryData>> target) {
        for (final Group facet : sourceFacets) {
            final FactFinderFacetData<SearchQueryData> facetData = createFacetData();
            facetData.setCode(urlDecode(getFacetCode(facet)));
            facetData.setIsViable(isFacetVisible(facet));
            facetData.setName(getFacetName(facet.getName()));
            facetData.setType(FactFinderFacetType.fromFacet(facet));
            facetData.setHasSelectedElements(hasSelectedElements(facet));
            facetData.setUnit(getUnit(facet, facetData));
            buildFacetValues(facet, queryData, facetData, totalResultCount);
            facetData.setHasMinMaxFilters(hasMinMaxFilters(facetData.getValues()));
            if (isNotEmpty(facetData.getValues())) {
                target.add(facetData);
            }
        }
    }

    private String getUnit(Group facet, FactFinderFacetData<SearchQueryData> facetData) {
        return facetData.getCode().equalsIgnoreCase("price")
                                                             ? getCommonI18NService().getCurrentCurrency().getIsocode()
                                                             : facet.getUnit();
    }

    private String getFacetName(String facetName) {
        String facetNameValue = CATEGORY_CODE_PATTERN.matcher(facetName).matches() ? CategoryModel._TYPECODE : facetName;
        facetNameValue = facetNameValue.replace("+", "&#43;");
        return urlDecode(facetNameValue);
    }

    private Boolean isFacetVisible(Group facet) {
        Boolean visible = facet.getElements() != null
                && CollectionUtils.isNotEmpty(facet.getElements().getGroupElement())
                && facet.getElements()
                        .getGroupElement()
                        .get(0)
                        .getAssociatedFieldName()
                        .equalsIgnoreCase(DistrelecfactfindersearchConstants.CATEGORY_CODE_ROOT_PATH_PREFIX);
        return BooleanUtils.negate(visible);
    }

    private String urlDecode(final String value) {
        try {
            return URLDecoder.decode(value, Charsets.UTF_8.name());
        } catch (final IllegalArgumentException | UnsupportedEncodingException ilae) {
            return value;
        }
    }

    protected String getFacetCode(final Group facet) {
        final StringBuilder codeWithUnit = new StringBuilder();

        // Retrieve facet group code from first facet value if available
        if (facet.getElements() != null && CollectionUtils.isNotEmpty(facet.getElements().getGroupElement())
                && StringUtils.isNotBlank(facet.getElements().getGroupElement().get(0).getAssociatedFieldName())) {

            final String firstAssociatedFieldName = facet.getElements().getGroupElement().get(0).getAssociatedFieldName();
            codeWithUnit.append(StringUtils.substringBefore(firstAssociatedFieldName, FACTFINDER_UNIT_PREFIX));
        } else {
            codeWithUnit.append(facet.getName());
        }

        // Retrieve common facet group unit (can be different than the unit of the first value)
        if (StringUtils.isNotEmpty(facet.getUnit())) {
            codeWithUnit.append(FACTFINDER_UNIT_PREFIX).append(facet.getUnit());
        }

        return PriceFilterTranslator.getPriceSensitiveFacetName(codeWithUnit.toString());
    }

    protected void buildFacetValues(final Group sourceFacet, final SearchQueryData queryData, final FactFinderFacetData<SearchQueryData> target,
                                    final long totalResultCount) {
        final List<GroupElement> facetValues = sourceFacet.getElements().getGroupElement();
        final List<FactFinderFacetValueData<SearchQueryData>> allFacetValues = new ArrayList<>();

        List<GroupElement> selectedFacetValues = new ArrayList<>();
        // Do not show the selected elements for the category facet
        if (!CATEGORY_CODE_PATTERN.matcher(getAssociatedFieldName(sourceFacet)).matches()) {
            // DISTRELEC-3212: set the selected facets at the beginning
            // with the current response there is no possibility to remain the order of the facets
            selectedFacetValues = sourceFacet.getSelectedElements().getGroupElement();
            for (final GroupElement facetValue : selectedFacetValues) {
                if (convertFacetValue(queryData, facetValue, facetValues, selectedFacetValues)) {
                    final FactFinderFacetValueData<SearchQueryData> facetValueData = buildFacetValue(facetValue, queryData, target, totalResultCount);
                    if (facetValueData != null) {
                        allFacetValues.add(facetValueData);
                    }
                }
            }
        }

        for (final GroupElement facetValue : facetValues) {
            if (convertFacetValue(queryData, facetValue, facetValues, selectedFacetValues)) {
                final FactFinderFacetValueData<SearchQueryData> facetValueData = buildFacetValue(facetValue, queryData, target, totalResultCount);
                if (facetValueData != null) {
                    allFacetValues.add(facetValueData);
                }
            }
        }

        target.setValues(allFacetValues);
    }

    private boolean convertFacetValue(final SearchQueryData queryData, final GroupElement facetValue, final List<GroupElement> facetValues,
                                      final List<GroupElement> selectedFacetValues) {
        if (DistSearchType.MANUFACTURER.equals(queryData.getSearchType())) {
            if (MANUFACTURER.getValue().equals(facetValue.getAssociatedFieldName())) {
                return getFacetValueCount(facetValues, selectedFacetValues) >= 2;
            }
        }
        return true;
    }

    private static String urlEncode(final String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Could not encode text [" + text + "]", e);
            return text;
        }
    }

    protected FactFinderFacetValueData<SearchQueryData> buildFacetValue(final GroupElement sourceFacet, final SearchQueryData queryData,
                                                                        final FactFinderFacetData<SearchQueryData> facetKeyData, final long totalResultCount) {
        final String facetName = buildTypeSensitiveName(sourceFacet, facetKeyData);
        if (isBlank(facetName)) {
            return null;
        }
        final FactFinderFacetValueData<SearchQueryData> target = createFacetValueData();
        target.setName(facetName);
        // target.setRealFilterString(sourceFacet.getAssociatedFieldName() + "=" + sourceFacet.getName());
        if (sourceFacet.getAssociatedFieldName().startsWith("categoryCodePathROOT")) {
            final Iterator<Filter> filteIterator = sourceFacet.getSearchParams().getFilters().getFilter().iterator();
            final StringBuilder queryFilte = new StringBuilder();
            while (filteIterator.hasNext()) {
                final Filter filter = filteIterator.next();
                queryFilte.append("filter_");
                queryFilte.append(filter.getName());
                queryFilte.append("=");
                queryFilte.append(filter.getValueList().getFilterValue().get(0).getValue());
                queryFilte.append("&");
            }
            if (queryFilte.lastIndexOf("&") != -1)
                queryFilte.deleteCharAt(queryFilte.lastIndexOf("&"));
            target.setQueryFilter(queryFilte.toString());
        } else {
            target.setQueryFilter("filter_" + urlEncode(sourceFacet.getAssociatedFieldName()) + "=" + urlEncode(sourceFacet.getName()));
        }
        target.setPropertyName(buildPropertyNameForLocalization(sourceFacet, facetKeyData));
        target.setCode(sourceFacet.getName());
        // DISTRELEC-10321 : SRP Categories Redesign - Part II
        if (sourceFacet.getAssociatedFieldName().startsWith(DistrelecfactfindersearchConstants.CATEGORY_CODE_ROOT_PATH_PREFIX))
            target.setCode(sourceFacet.getAssociatedFieldName());
        target.setCount(getRecordCount(sourceFacet, totalResultCount));
        target.setSelected(isFacetSelected(sourceFacet, queryData));
        if (FactFinderFacetType.SLIDER.equals(facetKeyData.getType()) && (sourceFacet instanceof SliderGroupElement)) {
            final SliderGroupElement sliderFacetValue = (SliderGroupElement) sourceFacet;
            target.setAbsoluteMaxValue(Math.ceil(sliderFacetValue.getAbsoluteMaxValue()));
            target.setAbsoluteMinValue(Math.floor(sliderFacetValue.getAbsoluteMinValue()));
            target.setSelectedMaxValue(Math.ceil(sliderFacetValue.getSelectedMaxValue()));
            target.setSelectedMinValue(sliderFacetValue.getSelectedMinValue());
            if (target.getAbsoluteMaxValue() - target.getAbsoluteMinValue() < getMinSliderRange()) {
                // Do not display slider if difference between min. and max. value is smaller than the configured "minSliderRange"
                LOG.debug("Do not display slider facet because the difference between min. and max. value is smaller than required");
                return null;
            }
        }
        final String facetKey = PriceFilterTranslator.getPriceSensitiveFacetName(sourceFacet.getAssociatedFieldName());
        final String facetValue = buildTypeSensitiveValues(facetKeyData.getType(), target);
        if (target.isSelected()) {
            // Query to remove, rather than add facet
            target.setQuery(getQueryTransformer().refineQueryRemoveFacet(queryData, facetKey, facetValue));
        } else {
            // Query to add the facet
            target.setQuery(getQueryTransformer().refineQueryAddFacet(queryData, facetKey, facetValue));
        }
        return target;
    }

    protected String buildTypeSensitiveName(final GroupElement sourceFacet, final FactFinderFacetData<SearchQueryData> facetKeyData) {
        final String name = SortCodeTranslator.getSortName(sourceFacet.getName());

        if (DistrelecfactfindersearchConstants.FILTER_INSTOCK.equalsIgnoreCase(sourceFacet.getAssociatedFieldName())
                || DistrelecfactfindersearchConstants.FILTER_AVAILABLEINPICKUP.equalsIgnoreCase(sourceFacet.getAssociatedFieldName())) {
            if ("0".equalsIgnoreCase(name)) {
                return StringUtils.EMPTY;
            }
        }

        if (!FactFinderFacetType.BOOLEAN_CHECKBOX.equals(facetKeyData.getType())) {
            final String unit = extractUnit(sourceFacet.getAssociatedFieldName());
            if (StringUtils.isNotBlank(unit) && name != null && !name.contains(unit)) {
                return name + " " + unit;
            }
        }
        return name;
    }

    protected String extractUnit(final String associatedFieldName) {
        if (StringUtils.isNotBlank(associatedFieldName)) {
            final String[] nameParts = associatedFieldName.split(FACTFINDER_UNIT_PREFIX);
            if (nameParts.length == 2) {
                return nameParts[1];
            }
        }
        return null;
    }

    protected String buildPropertyNameForLocalization(final GroupElement sourceFacet, final FactFinderFacetData<SearchQueryData> facetKeyData) {
        if (FactFinderFacetType.BOOLEAN_CHECKBOX.equals(facetKeyData.getType())) {
            return StringUtils.equalsIgnoreCase(sourceFacet.getName(), "< 1") ? DistrelecfactfindersearchConstants.FILTER_BOOLEAN_FALSE
                                                                              : DistrelecfactfindersearchConstants.FILTER_BOOLEAN_TRUE;
        }

        if (DistrelecfactfindersearchConstants.FILTER_INSTOCK.equalsIgnoreCase(sourceFacet.getAssociatedFieldName())) {
            if ("1".equalsIgnoreCase(sourceFacet.getName())) {
                return DistrelecfactfindersearchConstants.FILTER_INSTOCK_SLOW;
            } else if ("2".equalsIgnoreCase(sourceFacet.getName())) {
                return DistrelecfactfindersearchConstants.FILTER_INSTOCK_FAST;
            }
        }

        if (DistrelecfactfindersearchConstants.FILTER_PICKUP.equalsIgnoreCase(sourceFacet.getAssociatedFieldName())) {
            if ("1".equalsIgnoreCase(sourceFacet.getName())) {
                return DistrelecfactfindersearchConstants.FILTER_BOOLEAN_TRUE;
            }
        }

        if (DistrelecfactfindersearchConstants.FILTER_PRODUCT_STATUS.equalsIgnoreCase(sourceFacet.getAssociatedFieldName())) {
            return DistrelecfactfindersearchConstants.FILTER_FACET_PRODUCT_STATUS;
        }

        return null;
    }

    protected String buildTypeSensitiveValues(final FactFinderFacetType facetType, final FactFinderFacetValueData<SearchQueryData> facetValue) {
        final StringBuilder value = new StringBuilder();
        if (FactFinderFacetType.SLIDER.equals(facetType)) {
            value.append(facetValue.getSelectedMinValue()).append(" - ").append(facetValue.getSelectedMaxValue());
        } else if (facetValue.getCode().startsWith("categoryCodePathROOT")) {
            value.append(facetValue.getName());
        } else {
            value.append(facetValue.getCode());
        }
        return value.toString();
    }

    protected boolean isFacetSelected(final GroupElement facetValue, final SearchQueryData queryData) {
        for (final SearchQueryTermData termData : queryData.getFilterTerms()) {
            if (termData.getKey().equals(PriceFilterTranslator.getPriceSensitiveFacetName(facetValue.getAssociatedFieldName()))
                    && termData.getValue().equals(facetValue.getName())) {
                return true;
            }
        }
        return false;
    }

    protected int getFacetValueCount(final List<GroupElement> facetValues, final List<GroupElement> selectedFacetValues) {
        int facetValueCount = 0;
        if (CollectionUtils.isNotEmpty(facetValues)) {
            facetValueCount = facetValues.size();
        }
        if (CollectionUtils.isNotEmpty(selectedFacetValues)) {
            facetValueCount += selectedFacetValues.size();
        }
        return facetValueCount;
    }

    protected FactFinderFacetData<SearchQueryData> createFacetData() {
        return new FactFinderFacetData<>();
    }

    protected FactFinderFacetValueData<SearchQueryData> createFacetValueData() {
        return new FactFinderFacetValueData<>();
    }

    protected Boolean hasSelectedElements(final Group sourceFacets) {
        if (sourceFacets != null) {
            final ArrayOfGroupElement elements = sourceFacets.getElements();

            if (FilterStyle.SLIDER.equals(sourceFacets.getFilterStyle())) {
                return isSliderSelected(sourceFacets);
            }

            if (elements != null && CollectionUtils.isNotEmpty(elements.getGroupElement())) {
                for (GroupElement groupElement : elements.getGroupElement()) {
                    if (groupElement.isSelected()) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    private boolean hasMinMaxFilters(List<FactFinderFacetValueData<SearchQueryData>> values) {
        return emptyIfNull(values).stream()
                                  .findFirst().stream()
                                  .allMatch(value -> isBlank(value.getPropertyName())
                                          && splittedFacetValueName(value.getName()).matches(MIN_MAX_FILTERS_REGEX));
    }

    private String splittedFacetValueName(String name) {
        String[] splittedName = StringUtils.split(name, " ");
        return splittedName != null && splittedName.length > 0 ? splittedName[0] : EMPTY;
    }

    /**
     * checks if a slider facet has changed values and is therefore selected.
     * 
     * @param sourceFacets
     *            sourceFacet (make sure it is a Slider facet!)
     * @return TRUE, if the values have changed
     */
    @SuppressWarnings("boxing")
    protected Boolean isSliderSelected(final Group sourceFacets) {
        if (sourceFacets != null) {
            final ArrayOfGroupElement elements = sourceFacets.getElements();
            if (elements != null) {
                final List<GroupElement> groupElements = elements.getGroupElement();
                if (CollectionUtils.isNotEmpty(groupElements)) {

                    try {
                        final SliderGroupElement sliderElement = (SliderGroupElement) groupElements.get(0); // there's only one slider element

                        final Double max = sliderElement.getAbsoluteMaxValue();
                        final Double min = sliderElement.getAbsoluteMinValue();
                        final Double selectedMax = sliderElement.getSelectedMaxValue();
                        final Double selectedMin = sliderElement.getSelectedMinValue();

                        if ((!max.equals(selectedMax)) || (!min.equals(selectedMin))) {
                            return Boolean.TRUE;
                        }

                    } catch (final ClassCastException ex) {
                        LOG.warn("There's a slider facet without a slider group element!");
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     * gets the associated field name from the first unselected value or if this is not possible of the first selected value. <br>
     * returns an empty string if the associated field name could not be retrieved.
     * 
     * @param group
     *            group to check
     * @return the associated field name or an empty String
     */
    protected String getAssociatedFieldName(final Group group) {

        String associatedFieldName = "";

        if (group != null) {
            // check first unselected element
            final ArrayOfGroupElement elements = group.getElements();
            if (elements != null && CollectionUtils.isNotEmpty(elements.getGroupElement()) && elements.getGroupElement().get(0) != null) {
                associatedFieldName = elements.getGroupElement().get(0).getAssociatedFieldName();
            }

            if (isBlank(associatedFieldName)) {
                // check first selected value
                final ArrayOfGroupElement selectedElements = group.getSelectedElements();
                if (selectedElements != null && CollectionUtils.isNotEmpty(selectedElements.getGroupElement())
                        && selectedElements.getGroupElement().get(0) != null) {
                    associatedFieldName = selectedElements.getGroupElement().get(0).getAssociatedFieldName();
                }
            }
        }
        return associatedFieldName;
    }

    private long getRecordCount(final GroupElement sourceFacet, final long totalResultCount) {
        long recordCount = sourceFacet.getRecordCount().longValue();
        if (recordCount == 0) {
            recordCount = totalResultCount;
        }
        return recordCount;
    }

    // BEGIN GENERATED CODE
    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public SearchQueryTransformer getQueryTransformer() {
        return queryTransformer;
    }

    @Required
    public void setQueryTransformer(final SearchQueryTransformer queryTransformer) {
        this.queryTransformer = queryTransformer;
    }

    public double getMinSliderRange() {
        return minSliderRange;
    }

    @Required
    public void setMinSliderRange(final double minSliderRange) {
        this.minSliderRange = minSliderRange;
    }
}
