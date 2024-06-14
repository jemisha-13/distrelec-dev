package com.namics.distrelec.occ.core.v2.helper.search;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.facades.category.DistProductFamilyFacade;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commercefacades.product.data.CategoryData;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.PRODUCT_FAMILY_CODE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class ProductFamilySearchRedirectService implements SearchRedirectService {

    @Autowired
    private DistProductFamilyFacade distProductFamilyFacade;

    @Autowired
    private DistCategoryService categoryService;

    @Autowired
    private SearchRedirectRuleFactory searchRedirectRuleFactory;

    @Override
    public SearchRedirectRule shouldRedirect(SearchQueryData searchQuery) {
        if (searchQuery.getFilterTerms().size() == 1 && searchQuery.getFilterTerms().get(0).getKey().equals(PRODUCT_FAMILY_CODE)) {
            String productFamilyCode = searchQuery.getFilterTerms().get(0).getValue();
            Optional<CategoryData> productFamilyCategory = distProductFamilyFacade.findProductFamily(productFamilyCode);

            if (productFamilyCategory.isEmpty() || isBlank(productFamilyCategory.get().getFamilyCategoryCode())) {
                return searchRedirectRuleFactory.createStatusRule(SearchRedirectStatus.NOT_FOUND);
            }
        }
        return null;
    }

    @Override
    public boolean supportsSearchType(DistSearchType searchType) {
        return DistSearchType.TEXT.equals(searchType);
    }
}
