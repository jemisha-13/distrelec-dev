package com.namics.distrelec.occ.core.populators;

import com.namics.distrelec.b2b.core.model.cms2.components.DistFooterComponentModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;

/**
 *
 */
public class DistFooterComponentModelToDataPopulator implements Populator<CMSItemModel, Map<String, Object>> {

    private Predicate<CMSItemModel> distFooterComponentPredicate;

    @Override
    public void populate(CMSItemModel cmsItemModel, Map<String, Object> stringObjectMap) throws ConversionException {

        if (getDistFooterComponentPredicate().test(cmsItemModel)) {
            DistFooterComponentModel distFooterComponentModel = (DistFooterComponentModel) cmsItemModel;
            if (CollectionUtils.isNotEmpty(distFooterComponentModel.getSocialMedias())) {
                //

            }

        }

    }

    public Predicate<CMSItemModel> getDistFooterComponentPredicate() {
        return distFooterComponentPredicate;
    }

    public void setDistFooterComponentPredicate(Predicate<CMSItemModel> distFooterComponentPredicate) {
        this.distFooterComponentPredicate = distFooterComponentPredicate;
    }

}
