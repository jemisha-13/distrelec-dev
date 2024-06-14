package com.namics.distrelec.occ.core.v2.helper.search;

import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManufacturerSearchRedirectService implements SearchRedirectService {

    @Autowired
    private DistManufacturerFacade distManufacturerFacade;

    @Autowired
    private DistrelecProductFacade productFacade;

    @Autowired
    private SearchRedirectRuleFactory searchRedirectRuleFactory;

    @Override
    public SearchRedirectRule shouldRedirect(SearchQueryData searchQuery) {
        String manufacturerCode = searchQuery.getCode();
        if (distManufacturerFacade.isManufacturerExcluded(manufacturerCode) && productFacade.enablePunchoutFilterLogic()) {
            return searchRedirectRuleFactory.createStatusRule(SearchRedirectStatus.PUNCHED_OUT);
        }
        return null;
    }

    @Override
    public boolean supportsSearchType(DistSearchType searchType) {
        return DistSearchType.MANUFACTURER.equals(searchType);
    }

    protected void setDistManufacturerFacade(DistManufacturerFacade distManufacturerFacade) {
        this.distManufacturerFacade = distManufacturerFacade;
    }

    protected void setProductFacade(DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    protected void setSearchRedirectRuleFactory(SearchRedirectRuleFactory searchRedirectRuleFactory) {
        this.searchRedirectRuleFactory = searchRedirectRuleFactory;
    }
}
