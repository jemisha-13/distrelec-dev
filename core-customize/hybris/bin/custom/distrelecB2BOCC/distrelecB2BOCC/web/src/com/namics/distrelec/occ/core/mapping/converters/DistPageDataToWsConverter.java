package com.namics.distrelec.occ.core.mapping.converters;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsoccaddon.data.CMSPageWsDTO;
import de.hybris.platform.cmsoccaddon.data.ComponentWsDTO;
import de.hybris.platform.cmsoccaddon.data.ContentSlotListWsDTO;
import de.hybris.platform.cmsoccaddon.data.ContentSlotWsDTO;
import de.hybris.platform.cmsoccaddon.data.NavigationNodeWsDTO;
import de.hybris.platform.cmsoccaddon.mapping.converters.PageDataToWsConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;

import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class DistPageDataToWsConverter extends PageDataToWsConverter {

    private static final String DIST_MAIN_NAVIGATION_COMPONENT = "DistMainNavigationComponent";

    private static final String ROOT_NAVIGATION_NODE = "rootNavigationNode";

    /**
     * Only added clearMainNavNonTopChildren, everything else same as in parent.
     */
    @Override
    public void customize(MapperFactory factory) {
        factory.classMap(AbstractPageData.class, CMSPageWsDTO.class)
               .byDefault()
               .field(LOCALIZED_TITLE, TITLE) //
               .field(LOCALIZED_DESCRIPTION, DESCRIPTION) //
               .customize(new CustomMapper<AbstractPageData, CMSPageWsDTO>() {
                   @Override
                   public void mapAtoB(final AbstractPageData data, final CMSPageWsDTO wsData, final MappingContext mappingContext) {
                       final List<ContentSlotWsDTO> mappedSlots = data.getContentSlots() //
                                                                      .stream() //
                                                                      .map(slot -> (ContentSlotWsDTO) getMapper().map(slot, fields)) //
                                                                      .collect(Collectors.toList());

                       clearMainNavNonTopChildren(mappedSlots);

                       final ContentSlotListWsDTO slotsWsData = new ContentSlotListWsDTO();
                       slotsWsData.setContentSlot(mappedSlots);
                       wsData.setContentSlots(slotsWsData);

                       if (isOtherPropertiesFieldVisible(wsData)) {
                           wsData.setOtherProperties(convertMap(data.getOtherProperties()));
                       }
                   }
               }).register();
    }

    private void clearMainNavNonTopChildren(List<ContentSlotWsDTO> mappedSlots) {
        mappedSlots.stream()
                   .flatMap(contentSlot -> contentSlot.getComponents().getComponent().stream())
                   .filter(component -> DIST_MAIN_NAVIGATION_COMPONENT.equals(component.getTypeCode()))
                   .forEach(this::removeNavigationNodeNonTopChildren);
    }

    private void removeNavigationNodeNonTopChildren(ComponentWsDTO mainNavNode) {
        NavigationNodeWsDTO rootNavigationNode = (NavigationNodeWsDTO) mainNavNode.getOtherProperties().get(ROOT_NAVIGATION_NODE);
        if (rootNavigationNode == null) {
            return;
        }
        emptyIfNull(rootNavigationNode.getChildren())
          .stream()
          .filter(Objects::nonNull)
          .forEach(child -> child.setChildren(emptyList()));
    }

}
