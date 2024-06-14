/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.cms2.components.DistrelecCategoryGridComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.components.DistrelecCategoryGridItemComponentModel;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.cms.data.DistrelecCategoryGridItemData;
import com.namics.distrelec.b2b.facades.util.WebpMediaUtil;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.L10NService;

/**
 * {@code DistrelecCategoryGridComponentController}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */

@Controller("DistrelecCategoryGridComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistrelecCategoryGridComponent)
public class DistrelecCategoryGridComponentController extends AbstractDistCMSComponentController<DistrelecCategoryGridComponentModel> {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Autowired
    @Qualifier("categoryModelUrlResolver")
    private UrlResolver<CategoryModel> categoryModelUrlResolver;

    @Autowired
    @Qualifier("imageConverter")
    private Converter<MediaModel, ImageData> imageConverter;

    @Autowired
    @Qualifier("mediaContainerToImageMapConverter")
    private Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter;

    @Autowired
    @Qualifier("distCategoryFacade")
    private DistCategoryFacade categoryFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistrelecCategoryGridComponentModel component) {
        model.addAttribute("gridItems", getItems(component.getItems()));
    }

    /**
     * Convert the collection of {@code DistrelecCategoryGridItemComponentModel} to a list of {@code DistrelecCategoryGridItemData}
     *
     * @param items
     *            the source collection to be converted
     * @return a list of {@code DistrelecCategoryGridItemData}
     */
    protected List<DistrelecCategoryGridItemData> getItems(final Collection<DistrelecCategoryGridItemComponentModel> items) {
        final List<DistrelecCategoryGridItemData> datas = new ArrayList<DistrelecCategoryGridItemData>();

        if (items != null && !items.isEmpty()) {
            for (final DistrelecCategoryGridItemComponentModel carpetModel : items) {
                datas.add(convert(carpetModel));
            }
        }

        return datas;
    }

    /**
     * Convert the source {@code DistrelecCategoryGridItemComponentModel} to a target {@code DistrelecCategoryGridItemData}
     *
     * @param itemModel
     *            the source item to be converted
     * @return an instance of {@code DistrelecCategoryGridItemData}
     */
    protected DistrelecCategoryGridItemData convert(final DistrelecCategoryGridItemComponentModel itemModel) {
        final DistrelecCategoryGridItemData gridItemData = new DistrelecCategoryGridItemData();
        final CategoryModel category = itemModel.getCategory();
        gridItemData.setCategoryCode(category != null ? category.getCode() : null);
        gridItemData.setTitle(StringUtils.isNotBlank(itemModel.getTitle()) ? itemModel.getTitle() : (category != null ? category.getName() : ""));
        gridItemData.setUrl(
                            StringUtils.isNotBlank(itemModel.getUrl()) ? itemModel.getUrl()
                                                                       : (category != null ? categoryModelUrlResolver.resolve(category) : "#"));
        if (itemModel.getIcon() != null) {
            gridItemData.setThumbnail(imageConverter.convert(itemModel.getIcon(), gridItemData.getThumbnail()));
        } else if (category != null && category.getPrimaryImage() != null && CollectionUtils.isNotEmpty(category.getPrimaryImage().getMedias())) {
            MediaModel thumbnail = WebpMediaUtil.getMediaModelByType(DistConstants.MediaFormat.PORTRAIT_SMALL_WEBP, DistConstants.MediaFormat.PORTRAIT_SMALL, category.getPrimaryImage());
            if (thumbnail != null) {
                gridItemData.setThumbnail(imageConverter.convert(thumbnail, gridItemData.getThumbnail()));
            }
        }
        return gridItemData;
    }

}
