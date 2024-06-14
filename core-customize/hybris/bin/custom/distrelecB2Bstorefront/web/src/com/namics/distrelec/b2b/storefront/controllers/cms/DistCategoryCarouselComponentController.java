package com.namics.distrelec.b2b.storefront.controllers.cms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.namics.distrelec.b2b.core.model.cms2.components.DistCategoryCarouselComponentModel;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.converters.populator.CategoryPopulator;
import de.hybris.platform.commercefacades.product.data.CategoryData;

@Controller("DistCategoryCarouselComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistCategoryCarouselComponent)
public class DistCategoryCarouselComponentController extends AbstractDistCMSComponentController<DistCategoryCarouselComponentModel> {
    private static final String AUTOPLAY = "autoplay";

    @Autowired
    private CategoryPopulator categoryPopulator;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistCategoryCarouselComponentModel component) {
        final List<CategoryData> categories = new ArrayList<>();
        for (final CategoryModel category : component.getCategories()) {
            final CategoryData categoryData = new CategoryData();
            categoryPopulator.populate(category, categoryData);
            categories.add(categoryData);
        }
        if (component.getAutoplayTimeout() == null) {
            model.addAttribute(AUTOPLAY, Boolean.FALSE);
        } else {
            model.addAttribute(AUTOPLAY, Boolean.TRUE);
        }
        model.addAttribute("categoryCarouselData", categories);
    }

    public CategoryPopulator getCategoryPopulator() {
        return categoryPopulator;
    }

    public void setCategoryPopulator(final CategoryPopulator categoryConverter) {
        this.categoryPopulator = categoryConverter;
    }
}
