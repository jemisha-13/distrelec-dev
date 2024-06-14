/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.model.cms2.components.DistCategoryThumbsComponentModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.customer.DistPunchoutService;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Controller for DistCategoryThumbsComponent
 */
@Controller("DistCategoryThumbsComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistCategoryThumbsComponent)
public class DistCategoryThumbsComponentController extends AbstractDistCMSComponentController<DistCategoryThumbsComponentModel> {

    @Autowired
    @Qualifier("categoryService")
    private DistCategoryService categoryService;

    @Autowired
    @Qualifier("categoryConverter")
    private Converter<CategoryModel, CategoryData> categoryConverter;

    @Autowired
    @Qualifier("distPunchoutService")
    private DistPunchoutService distPunchoutService;

    @Autowired
    @Qualifier("distCategoryFacade")
    private DistCategoryFacade distCategoryFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistCategoryThumbsComponentModel component) {
        final String categoryCode = (String) request.getAttribute("categoryCode");
        if (StringUtils.isNotEmpty(categoryCode)) {
            final CategoryModel category = categoryService.getCategoryForCode(categoryCode);
            final Collection<CategoryModel> categoryModels = category.getCategories();
            if (CollectionUtils.isNotEmpty(categoryModels)) {
                final List<CategoryData> categories = new ArrayList<CategoryData>();
                for (final CategoryModel categoryModel : categoryModels) {
                    if (!(distCategoryFacade.isCategoryEmptyForCurrentSite(categoryModel) || categoryService.hasSuccessor(categoryModel.getCode()))) {
                        categories.add(categoryConverter.convert(categoryModel));
                    }
                }
        		Collections.sort(categories, new Comparator<CategoryData>() {
        			@Override
        			public int compare(final CategoryData o1, final CategoryData o2) {
        				return o1.getName().compareTo(o2.getName());
        			}
        		});
                model.addAttribute("categories", categories);
            }
        }
    }
}
