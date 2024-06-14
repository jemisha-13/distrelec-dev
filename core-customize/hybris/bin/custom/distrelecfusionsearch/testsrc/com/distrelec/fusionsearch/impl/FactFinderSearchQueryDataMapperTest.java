package com.distrelec.fusionsearch.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;

import de.hybris.bootstrap.annotations.UnitTest;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@UnitTest
public class FactFinderSearchQueryDataMapperTest {

    private static final String FF_BUYABLE_PARAM = "Buyable";

    private static final String FF_MANUFACTURER_PARAM = "Manufacturer";

    private static final String FUSION_MANUFACTURER_PARAM = "manufacturerName";

    private static final String MANUFACTURER_PARAM_VALUE = "Panasonic";

    @InjectMocks
    private FactFinderSearchQueryDataMapper factFinderSearchQueryDataMapper;

    private SearchQueryData searchQueryData;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        searchQueryData = new SearchQueryData();

        ReflectionTestUtils.setField(factFinderSearchQueryDataMapper,
                                     "factFinderFusionFiltersIgnored",
                                     List.of(FF_BUYABLE_PARAM));
        ReflectionTestUtils.setField(factFinderSearchQueryDataMapper,
                                     "factFinderFusionFilterMapping",
                                     Map.of(FF_MANUFACTURER_PARAM, FUSION_MANUFACTURER_PARAM));
    }

    @Test
    public void testRemoveUnnecesarryFFParams() {
        List<SearchQueryTermData> filterTerms = new ArrayList<>(List.of(createTermData(FF_BUYABLE_PARAM, "1")));
        searchQueryData.setFilterTerms(filterTerms);

        factFinderSearchQueryDataMapper.updateFFParamsToFusionFormat(searchQueryData);

        Optional<SearchQueryTermData> buyableTermData = findTermData(searchQueryData, FF_BUYABLE_PARAM);
        assertThat(buyableTermData.isPresent(), is(FALSE));
    }

    @Test
    public void testMapFFParamsToFusionFormat() {
        List<SearchQueryTermData> filterTerms = new ArrayList<>(List.of(createTermData(FF_MANUFACTURER_PARAM, MANUFACTURER_PARAM_VALUE)));
        searchQueryData.setFilterTerms(filterTerms);

        factFinderSearchQueryDataMapper.updateFFParamsToFusionFormat(searchQueryData);

        Optional<SearchQueryTermData> ffManufacturerTermData = findTermData(searchQueryData, FF_MANUFACTURER_PARAM);
        assertThat(ffManufacturerTermData.isPresent(), is(FALSE));
        Optional<SearchQueryTermData> fusionManufacturerTermData = findTermData(searchQueryData, FUSION_MANUFACTURER_PARAM);
        assertThat(fusionManufacturerTermData.isPresent(), is(TRUE));
        assertThat(fusionManufacturerTermData.get().getValue(), is(MANUFACTURER_PARAM_VALUE));
    }

    private SearchQueryTermData createTermData(String key, String value) {
        SearchQueryTermData newTermData = new SearchQueryTermData();
        newTermData.setKey(key);
        newTermData.setValue(value);
        return newTermData;
    }

    private Optional<SearchQueryTermData> findTermData(SearchQueryData searchQueryData, String termDataKey) {
        return searchQueryData.getFilterTerms()
                              .stream()
                              .filter(termData -> termDataKey.equals(termData.getKey()))
                              .findFirst();
    }
}
