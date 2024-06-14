package com.namics.distrelec.occ.core.v2.helper.search;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SearchRedirectRuleFactoryImplTest {

    final String redirectUrl = "redirectUrl";
    final SearchRedirectStatus status = SearchRedirectStatus.PUNCHED_OUT;

    SearchRedirectRuleFactoryImpl factory;

    @Before
    public void setUpFactory() {
        factory =  new SearchRedirectRuleFactoryImpl();
    }

    @Test
    public void testCreateStatusRule() {
        SearchRedirectRule redirectRule = factory.createStatusRule(status);

        assertEquals(this.status, redirectRule.getStatus());
        assertNull(redirectRule.getRedirectUrl());
    }

    @Test
    public void testCreateRedirectRule() {
        SearchRedirectRule redirectRule = factory.createRedirectRule(redirectUrl);

        assertEquals(this.redirectUrl, redirectRule.getRedirectUrl());
        assertEquals(SearchRedirectStatus.REDIRECT, redirectRule.getStatus());
    }
}
