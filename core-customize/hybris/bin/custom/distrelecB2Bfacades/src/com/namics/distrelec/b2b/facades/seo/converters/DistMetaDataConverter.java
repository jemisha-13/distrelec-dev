package com.namics.distrelec.b2b.facades.seo.converters;

import com.namics.distrelec.b2b.core.model.seo.DistMetaDataModel;
import com.namics.distrelec.b2b.facades.seo.data.MetaData;
import com.namics.distrelec.b2b.facades.util.MetaSanitizerUtil;

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * {@code DistMetaDataConverter}
 *
 *
 */
public class DistMetaDataConverter extends AbstractPopulatingConverter<DistMetaDataModel, MetaData> {

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.converters.impl.AbstractPopulatingConverter#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final DistMetaDataModel source, final MetaData target) {
        target.setContentDescription(MetaSanitizerUtil.sanitize(source.getContentDescription()));
        target.setContentTitle(MetaSanitizerUtil.sanitize(source.getContentTitle()));
        target.setDescription(MetaSanitizerUtil.sanitize(source.getMetaDescription()));
        target.setH1(source.getMetaH1());
        target.setKeywords(source.getMetaKeywords());
        target.setTitle(MetaSanitizerUtil.sanitize(source.getMetaTitle()));
        target.setRobots(source.getMetaRobots());

        // create unique caching key
        final StringBuilder keyCode = new StringBuilder();
        if (source.getProduct() != null) {
            keyCode.append(source.getProduct().getCode());
        } else if (source.getCategory() != null) {
            keyCode.append(source.getCategory().getCode());
        }
        target.setCachingKey(keyCode.append("_") //
                .append(source.getCountry().getIsocode()) //
                .append("_")//
                .append(source.getLanguage().getIsocode())//
                .toString() //
        );

        super.populate(source, target);
    }
}
