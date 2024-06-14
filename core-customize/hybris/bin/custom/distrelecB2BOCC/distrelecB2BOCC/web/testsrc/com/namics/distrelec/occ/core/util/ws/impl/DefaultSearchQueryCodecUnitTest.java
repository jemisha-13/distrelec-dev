package com.namics.distrelec.occ.core.util.ws.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;

import de.hybris.bootstrap.annotations.UnitTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@UnitTest
public class DefaultSearchQueryCodecUnitTest {

    private DefaultSearchQueryCodec defaultSearchQueryCodec;

    @Before
    public void setUp() {
        defaultSearchQueryCodec = new DefaultSearchQueryCodec();
    }

    @Test
    public void parseSearchQuery() {
        final String query = "easy::" + DistrelecfactfindersearchConstants.FILTER + "key1:value1:key2:value2";
        final SearchQueryData data = defaultSearchQueryCodec.decodeQuery(query);
        assertEquals("easy", data.getFreeTextSearch());
        assertEquals(DistSearchType.TEXT, data.getSearchType());
        assertEquals(1, data.getFilterTerms().size());

        final SearchQueryTermData filter1 = data.getFilterTerms().get(0);

        assertEquals("key1", filter1.getKey());
        assertEquals("value1", filter1.getValue());
    }

    @Test
    public void parseSearchQueryFreeTextOnly() {
        final String query = "easy";
        final SearchQueryData data = defaultSearchQueryCodec.decodeQuery(query);
        assertEquals("easy", data.getFreeTextSearch());
        assertEquals(DistSearchType.TEXT, data.getSearchType());
        assertEquals(0, data.getFilterTerms().size());
    }

    @Test
    public void parseSearchQueryFreeTextAndSort() {
        final String query = "easy:somesort";
        final SearchQueryData data = defaultSearchQueryCodec.decodeQuery(query);
        assertEquals("easy", data.getFreeTextSearch());
        assertEquals("somesort", data.getSort());
        assertEquals(DistSearchType.TEXT, data.getSearchType());
        assertEquals(0, data.getFilterTerms().size());
    }

    @Test
    public void parseSearchQueryWithSort() {
        final String query = "easy:somesort:" + DistrelecfactfindersearchConstants.FILTER + "key1:value1:" + DistrelecfactfindersearchConstants.FILTER + "key2:value2";
        final SearchQueryData data = defaultSearchQueryCodec.decodeQuery(query);
        assertEquals("easy", data.getFreeTextSearch());
        assertEquals("somesort", data.getSort());
        assertEquals(DistSearchType.TEXT, data.getSearchType());
        assertEquals(2, data.getFilterTerms().size());

        final SearchQueryTermData filter1 = data.getFilterTerms().get(0);
        final SearchQueryTermData filter2 = data.getFilterTerms().get(1);

        assertEquals("key1", filter1.getKey());
        assertEquals("value1", filter1.getValue());

        assertEquals("key2", filter2.getKey());
        assertEquals("value2", filter2.getValue());
    }

    @Test
    public void parseSearchQueryWithCategoryFreetextSearch() {
        final String query = "easy:somesort:filter_categoryCodePathROOT:cat-code1";
        final SearchQueryData data = defaultSearchQueryCodec.decodeQuery(query);
        assertEquals("easy", data.getFreeTextSearch());
        assertEquals("somesort", data.getSort());
        assertEquals(DistSearchType.TEXT, data.getSearchType());
        assertFalse(data.getFilterTerms().isEmpty());
    }

    @Test
    public void parseSearchQueryWithCategory() {
        final String query = "*:somesort:filter_categoryCodePathROOT:cat-code1";
        final SearchQueryData data = defaultSearchQueryCodec.decodeQuery(query);
        assertEquals("*", data.getFreeTextSearch());
        assertEquals("somesort", data.getSort());
        assertEquals(DistSearchType.CATEGORY, data.getSearchType());
        assertFalse(data.getFilterTerms().isEmpty());
    }

    @Test
    public void serializeComplete() {
        final SearchQueryData query = new SearchQueryData();
        query.setFreeTextSearch("a");
        query.setSort("somesort");

        final List<SearchQueryTermData> terms = new ArrayList<>();
        final SearchQueryTermData term1 = createSearchQueryTermData("key1", "value1");
        final SearchQueryTermData term2 = createSearchQueryTermData("key2", "value2");

        terms.add(term1);
        terms.add(term2);

        query.setFilterTerms(terms);

        assertEquals("a:somesort:key1:value1:key2:value2", defaultSearchQueryCodec.encodeQuery(query));
    }

    protected SearchQueryTermData createSearchQueryTermData(final String key, final String value) {
        final SearchQueryTermData term = new SearchQueryTermData();
        term.setKey(key);
        term.setValue(value);
        return term;
    }

    @Test
    public void serializeFreeTextAndTermsOnly() {
        final SearchQueryData query = new SearchQueryData();
        query.setFreeTextSearch("a");
        query.setSort("somesort");

        assertEquals("a:somesort", defaultSearchQueryCodec.encodeQuery(query));
    }

    @Test
    public void serializeFreeTextAndSort() {
        final SearchQueryData query = new SearchQueryData();
        query.setFreeTextSearch("a");

        final List<SearchQueryTermData> terms = new ArrayList<>();
        final SearchQueryTermData term1 = createSearchQueryTermData("key1", "value1");
        final SearchQueryTermData term2 = createSearchQueryTermData("key2", "value2");

        terms.add(term1);
        terms.add(term2);

        query.setFilterTerms(terms);

        assertEquals("a::key1:value1:key2:value2", defaultSearchQueryCodec.encodeQuery(query));
    }
}
