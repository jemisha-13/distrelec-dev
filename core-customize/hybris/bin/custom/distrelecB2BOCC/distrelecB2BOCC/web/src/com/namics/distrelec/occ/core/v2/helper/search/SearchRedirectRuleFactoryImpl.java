package com.namics.distrelec.occ.core.v2.helper.search;

import org.springframework.stereotype.Component;

@Component
class SearchRedirectRuleFactoryImpl implements SearchRedirectRuleFactory {

    @Override
    public SearchRedirectRule createStatusRule(SearchRedirectStatus status) {
        SearchRedirectRule redirectRule = new SearchRedirectRule();
        redirectRule.setStatus(status);
        return redirectRule;
    }

    @Override
    public SearchRedirectRule createRedirectRule(String redirectUrl) {
        SearchRedirectRule redirectRule = new SearchRedirectRule();
        redirectRule.setStatus(SearchRedirectStatus.REDIRECT);
        redirectRule.setRedirectUrl(redirectUrl);
        return redirectRule;
    }
}
