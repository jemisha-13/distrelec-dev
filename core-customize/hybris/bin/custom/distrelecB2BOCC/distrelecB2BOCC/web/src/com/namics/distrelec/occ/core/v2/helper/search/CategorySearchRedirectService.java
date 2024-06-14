package com.namics.distrelec.occ.core.v2.helper.search;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.customer.DistPunchoutService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CategorySearchRedirectService implements SearchRedirectService {

    @Autowired
    @Qualifier("categoryModelUrlResolver")
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Autowired
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private DistCategoryService distCategoryService;

    @Autowired
    private DistPunchoutService distPunchoutService;

    @Autowired
    private SearchRedirectRuleFactory searchRedirectRuleFactory;

    @Override
    public SearchRedirectRule shouldRedirect(SearchQueryData searchQuery) {
        String catCode = searchQuery.getCode();

        final CategoryModel successor = distCategoryService.findSuccessor(catCode);
        if (successor != null) {
            final String redirection = categoryModelUrlResolver.resolve(successor);
            if (StringUtils.isNotEmpty(redirection)) {
                return searchRedirectRuleFactory.createRedirectRule(redirection);
            }
        }

        if (distPunchoutService.isCategoryPunchedout(catCode)) {
            return searchRedirectRuleFactory.createStatusRule(SearchRedirectStatus.PUNCHED_OUT);
        }

        CategoryModel category = null;
        try {
            category = distCategoryService.getCategoryForCode(catCode);
        } catch (UnknownIdentifierException e) {
            // expected exception in case category with code does not exist
        }
        if (category == null) {
            return searchRedirectRuleFactory.createStatusRule(SearchRedirectStatus.NOT_FOUND);
        }

        if (distCategoryFacade.isCategoryEmptyForCurrentSite(category)) {
            // Redirect to the parent category.
            final String redirection = categoryModelUrlResolver.resolve(getSuperCategory(category));
            if (StringUtils.isNotEmpty(redirection)) {
                return searchRedirectRuleFactory.createRedirectRule(redirection);
            }
        }

        return null;
    }

    @Override
    public boolean supportsSearchType(DistSearchType searchType) {
        switch (searchType) {
            case CATEGORY:
            case CATEGORY_AND_TEXT:
                return true;
            default:
                return false;
        }
    }

    protected CategoryModel getSuperCategory(final CategoryModel category) {
        for (final CategoryModel superCategory : category.getSupercategories()) {
            if (superCategory != null && !(superCategory instanceof ClassificationClassModel)) {
                if (!distCategoryService.isRoot(superCategory) && superCategory.getLevel().equals(Integer.valueOf(category.getLevel().intValue() - 1))) {
                    return superCategory;
                }
            }
        }

        return category;
    }

    protected void setCategoryModelUrlResolver(DistUrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }

    protected void setDistCategoryFacade(DistCategoryFacade distCategoryFacade) {
        this.distCategoryFacade = distCategoryFacade;
    }

    protected void setDistCategoryService(DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }

    protected void setDistPunchoutService(DistPunchoutService distPunchoutService) {
        this.distPunchoutService = distPunchoutService;
    }

    protected void setSearchRedirectRuleFactory(SearchRedirectRuleFactory searchRedirectRuleFactory) {
        this.searchRedirectRuleFactory = searchRedirectRuleFactory;
    }
}
