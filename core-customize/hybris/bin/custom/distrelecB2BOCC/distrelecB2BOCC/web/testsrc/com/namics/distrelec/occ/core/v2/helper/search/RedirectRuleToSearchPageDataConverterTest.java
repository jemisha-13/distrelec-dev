package com.namics.distrelec.occ.core.v2.helper.search;

import com.google.common.collect.Lists;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@UnitTest
@RunWith(Parameterized.class)
public class RedirectRuleToSearchPageDataConverterTest {

    final String code = "code";
    final DistSearchType searchType = DistSearchType.CATEGORY;
    final String freeTextSearch = "freeTextSearch";
    final String redirectUrl = "redirectUrl";

    RedirectRuleToSearchPageDataConverterImpl converter;

    @Parameter(0)
    public SearchRedirectStatus redirectStatus;

    @Parameter(1)
    public boolean shouldNotFound;

    @Parameter(2)
    public boolean shouldPunchedOut;

    @Mock
    SearchRedirectRule redirectRule;

    @Mock
    SearchQueryData searchQuery;

    @Parameters
    public static Collection<Object[]> data() {
        return Lists.newArrayList(
                new Object[] {SearchRedirectStatus.REDIRECT, false, false},
                new Object[] {SearchRedirectStatus.NOT_FOUND, true, false},
                new Object[] {SearchRedirectStatus.PUNCHED_OUT, false, true});
    }

    @Before
    public void setUpConverter() {
        MockitoAnnotations.initMocks(this);

        converter = new RedirectRuleToSearchPageDataConverterImpl();

        doReturn(code).when(searchQuery).getCode();
        doReturn(searchType).when(searchQuery).getSearchType();
        doReturn(freeTextSearch).when(searchQuery).getFreeTextSearch();

        doReturn(redirectUrl).when(redirectRule).getRedirectUrl();
        doReturn(redirectStatus).when(redirectRule).getStatus();
    }

    @Test
    public void testConvert() {
        FactFinderProductSearchPageData searchPageData = converter.convert(searchQuery, redirectRule);

        assertEquals(this.code, searchPageData.getCode());
        assertEquals(this.searchType, searchPageData.getSearchType());
        assertEquals(this.freeTextSearch, searchPageData.getFreeTextSearch());
        assertEquals(this.redirectUrl, searchPageData.getKeywordRedirectUrl());
        assertEquals(this.shouldNotFound, searchPageData.isNotFound());
        assertEquals(this.shouldPunchedOut, searchPageData.isPunchedOut());
    }
}
