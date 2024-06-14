package com.namics.distrelec.b2b.facades.cms.populator;

import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.cmsfacades.navigations.populator.model.NavigationEntryModelToDataPopulator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

public class DistNavigationEntryModelToDataPopulator extends NavigationEntryModelToDataPopulator
{

    @Autowired
    @Qualifier("contentPageUrlResolver")
    private DistUrlResolver<ContentPageModel> contentPageUrlResolver;

    @Autowired
    @Qualifier("defaultDistCategoryModelUrlResolver")
    private DistUrlResolver<CategoryModel> categoryUrlResolver;


    @Override
    public void populate(final CMSNavigationEntryModel source, final NavigationEntryData target) throws ConversionException
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");
        super.populate(source, target);

        if(null != source.getItem()){
            if (source.getItem() instanceof CategoryModel){
                target.setLocalizedUrl(getCategoryUrlResolver().resolve((CategoryModel) source.getItem()));
            }
            if (source.getItem() instanceof ContentPageModel ){
                target.setLocalizedUrl(getContentPageUrlResolver().resolve((ContentPageModel) source.getItem()));
            }
            if (source.getItem() instanceof CMSLinkComponentModel){
                target.setLocalizedUrl(((CMSLinkComponentModel)source.getItem()).getLocalizedUrl());
            }
        }
    }

    public DistUrlResolver<ContentPageModel> getContentPageUrlResolver()
    {
        return contentPageUrlResolver;
    }

    public void setContentPageUrlResolver(final DistUrlResolver<ContentPageModel> contentPageUrlResolver)
    {
        this.contentPageUrlResolver = contentPageUrlResolver;
    }

    public DistUrlResolver<CategoryModel> getCategoryUrlResolver()
    {
        return categoryUrlResolver;
    }

    public void setCategoryUrlResolver(final DistUrlResolver<CategoryModel> categoryUrlResolver)
    {
        this.categoryUrlResolver = categoryUrlResolver;
    }
}
