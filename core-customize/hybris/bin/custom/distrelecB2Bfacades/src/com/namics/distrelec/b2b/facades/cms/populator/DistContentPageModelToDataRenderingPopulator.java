package com.namics.distrelec.b2b.facades.cms.populator;

import com.namics.distrelec.occ.cms.data.ContentPageNavigationData;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;

import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.rendering.attributeconverters.NavigationNodeToDataContentConverter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DistContentPageModelToDataRenderingPopulator implements Populator<AbstractPageModel, ContentPageNavigationData>
{
    private NavigationNodeToDataContentConverter cmsRenderingNavigationNodeToDataContentConverter;

    @Override
    public void populate(final AbstractPageModel source, final ContentPageNavigationData target) throws ConversionException
    {
        if (CollectionUtils.isNotEmpty(source.getNavigationNodeList()))
        {

            CMSNavigationNodeModel navNode = source.getNavigationNodeList().iterator().next();
            if(Objects.nonNull(navNode.getParent().getParent())) {
                CMSNavigationNodeModel secondLevelParentNode = navNode.getParent().getParent();
                if (Objects.isNull(secondLevelParentNode.getParent()))
                {
                    target.setNavRootNodes(navNode.getChildren().stream().map(cmsNavigationNode -> getCmsRenderingNavigationNodeToDataContentConverter().convert(cmsNavigationNode)).collect(Collectors.toList()));
                    if(CollectionUtils.isNotEmpty(navNode.getChildren())){
                        target.setPageRootNavNode(getCmsRenderingNavigationNodeToDataContentConverter().convert(navNode.getChildren().iterator().next()));
                    }
                }

                if (Objects.nonNull(secondLevelParentNode.getParent()) && Objects.isNull(secondLevelParentNode.getParent().getParent()))
                {
                    target.setNavRootNodes(navNode.getParent().getChildren().stream().map(cmsNavigationNode -> getCmsRenderingNavigationNodeToDataContentConverter().convert(cmsNavigationNode)).collect(Collectors.toList()));
                    target.setPageRootNavNode(getCmsRenderingNavigationNodeToDataContentConverter().convert(navNode));
                }

                if (Objects.nonNull(secondLevelParentNode.getParent().getParent()) && Objects.isNull(secondLevelParentNode.getParent().getParent().getParent()))
                {
                    target.setNavRootNodes(navNode.getParent().getParent().getChildren().stream().map(cmsNavigationNode -> getCmsRenderingNavigationNodeToDataContentConverter().convert(cmsNavigationNode)).collect(Collectors.toList()));
                    target.setPageRootNavNode(getCmsRenderingNavigationNodeToDataContentConverter().convert(navNode.getParent()));
                }
            }

        }
    }

    public NavigationNodeToDataContentConverter getCmsRenderingNavigationNodeToDataContentConverter() {
        return cmsRenderingNavigationNodeToDataContentConverter;
    }

    public void setCmsRenderingNavigationNodeToDataContentConverter(NavigationNodeToDataContentConverter cmsRenderingNavigationNodeToDataContentConverter) {
        this.cmsRenderingNavigationNodeToDataContentConverter = cmsRenderingNavigationNodeToDataContentConverter;
    }
}
