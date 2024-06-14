package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexData;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.servicelayer.session.SessionService;

@Controller
@RequestMapping(value = "/**/sitemap")
public class DistrelecSitemapController extends AbstractSearchPageController {
    private static final Logger LOG = LogManager.getLogger(FeedbackPageController.class);
//
    private static final String SITEMAP_CMS_PAGE = "sitemapPageTemplate";
    private static final String FORWARD_SLASH = "/";
    private static final String SITEMAP = "sitemap";
    private static final String CATEGORY_INDEX_LANG = "categoryIndex_lang";
    private static final String CATEGORY_INDEX = "categoryIndex";
    private static final String CATEGORY_NAV_ROOT_NODE = "MainCategoryNavNode";
    private static final String CATEGORIES = "categories";
    
    @Autowired
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    @Qualifier("contentPageUrlResolver")
    private UrlResolver<ContentPageModel> contentPageUrlResolver;
    
    @Autowired
    private DistCategoryFacade distCategoryFacade;
    
    @Autowired
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Autowired
    private CMSNavigationService cmsNavigationService;
    
	@RequestMapping(method = RequestMethod.GET)
    public String get(final Model model, final HttpServletRequest request, final HttpServletResponse response)
            throws CMSItemNotFoundException, UnsupportedEncodingException {

        
		
        final String resolvedUrl = new StringBuilder(FORWARD_SLASH).append(getCurrentLanguage().getIsocode()).append(FORWARD_SLASH + SITEMAP).toString();
        final String redirection = checkRequestUrl(request, response, resolvedUrl);
        if (StringUtils.isNotEmpty(redirection)) {
            return redirection;
        }
        addGlobalModelAttributes(model, request);
        model.addAttribute(CATEGORIES, getCategoryIndex());
        storeCmsPageInModel(model, getContentPageForLabelOrId(SITEMAP_CMS_PAGE));
        return getViewForPage(model);
    }

   
    /**
     * @return the list of category data to be displayed on the Category Index Page.
     */
    private List<DistCategoryIndexData> getCategoryIndex() {

        final String currentLang = getI18nService().getCurrentLocale().getLanguage();
        if (!currentLang.equals(getSessionService().getAttribute(CATEGORY_INDEX_LANG))) {
            getSessionService().removeAttribute(CATEGORY_INDEX);
        }

        final List<DistCategoryIndexData> categoryIndexData = getSessionService().getOrLoadAttribute(CATEGORY_INDEX, createSessionAttributeLoader(currentLang));
        if(categoryIndexData  == null){
            return Collections.EMPTY_LIST;
        } else {
            return categoryIndexData
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(DistCategoryIndexData::hasChildren)
                    .filter(DistCategoryIndexData::urlIsNotNull)
                    .filter(DistCategoryIndexData::isNotASpecialShop)
                    .collect(Collectors.toList());
        }
    }

    private SessionService.SessionAttributeLoader<List<DistCategoryIndexData>> createSessionAttributeLoader(String currentLang){
        return () -> {
            getSessionService().setAttribute(CATEGORY_INDEX_LANG, currentLang);
            try {
                final CMSNavigationNodeModel categoryRootNode = cmsNavigationService.getNavigationNodeForId(CATEGORY_NAV_ROOT_NODE);
                if (categoryRootNode == null) {
                    return Collections.<DistCategoryIndexData> emptyList();
                }

                return convert(categoryRootNode.getChildren());
            } catch (final CMSItemNotFoundException e) {
                return Collections.<DistCategoryIndexData> emptyList();
            }
        };
    }

    /**
     * Convert a category CMS navigation node to a category index data
     * 
     * @param node
     *            the source CMS navigation node
     * @return an instance of {@link DistCategoryIndexData}
     */
    private DistCategoryIndexData convert(final CMSNavigationNodeModel node) {
        if (node == null) {
            return null;
        }

        final DistCategoryIndexData categoryData = new DistCategoryIndexData();
        categoryData.setName(node.getTitle());
        if (CollectionUtils.isNotEmpty(node.getEntries()) && node.getEntries().get(0).getItem() instanceof CategoryModel) {
            final CategoryModel category = (CategoryModel) node.getEntries().get(0).getItem();
            categoryData.setUrl(categoryModelUrlResolver.resolve(category));
        }

        categoryData.setChildren(convert(node.getChildren()));

        return categoryData;
    }

    private List<DistCategoryIndexData> convert(final List<CMSNavigationNodeModel> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.<DistCategoryIndexData> emptyList();
        }

        final List<DistCategoryIndexData> categories = new ArrayList<DistCategoryIndexData>();
        for (final CMSNavigationNodeModel node : nodes) {
            categories.add(convert(node));
        }

        Collections.sort(categories, new Comparator<CategoryData>() {

            @Override
            public int compare(final CategoryData cat1, final CategoryData cat2) {
                return cat1.getName().compareTo(cat2.getName());
            }
        });

        return categories;
    }

    public DistUrlResolver<CategoryModel> getCategoryModelUrlResolver() {
        return categoryModelUrlResolver;
    }

    public void setCategoryModelUrlResolver(final DistUrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }

    public CMSNavigationService getCmsNavigationService() {
        return cmsNavigationService;
    }

    public void setCmsNavigationService(final CMSNavigationService cmsNavigationService) {
        this.cmsNavigationService = cmsNavigationService;
    }

}
