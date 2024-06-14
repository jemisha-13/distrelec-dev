package com.namics.distrelec.cms.populators;

import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistrelecNavigationNodeModelToDataRenderingPopulator implements Populator<CMSNavigationNodeModel, NavigationNodeData> {

    private Populator<CMSNavigationNodeModel, NavigationNodeData> cmsNavigationNodeModelToDataRenderingPopulator;

    @Override
    public void populate(CMSNavigationNodeModel cmsNavigationNodeModel, NavigationNodeData navigationNodeData) throws ConversionException {
        cmsNavigationNodeModelToDataRenderingPopulator.populate(cmsNavigationNodeModel, navigationNodeData);
        navigationNodeData.setSortingNumber(cmsNavigationNodeModel.getSortingNumber());
    }

    public void setCmsNavigationNodeModelToDataRenderingPopulator(
      Populator<CMSNavigationNodeModel, NavigationNodeData> cmsNavigationNodeModelToDataRenderingPopulator) {
        this.cmsNavigationNodeModelToDataRenderingPopulator = cmsNavigationNodeModelToDataRenderingPopulator;
    }
}
