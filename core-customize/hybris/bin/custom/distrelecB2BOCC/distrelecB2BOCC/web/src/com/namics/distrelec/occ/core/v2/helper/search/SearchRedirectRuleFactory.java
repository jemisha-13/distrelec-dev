package com.namics.distrelec.occ.core.v2.helper.search;

interface SearchRedirectRuleFactory {

    SearchRedirectRule createStatusRule(SearchRedirectStatus status);

    SearchRedirectRule createRedirectRule(String redirectUrl);
}
