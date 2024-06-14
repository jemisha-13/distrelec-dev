package com.namics.distrelec.mapping.converters;

import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsoccaddon.data.ComponentWsDTO;
import de.hybris.platform.cmsoccaddon.data.ContentSlotWsDTO;
import de.hybris.platform.cmsoccaddon.data.NavigationNodeWsDTO;
import de.hybris.platform.cmsoccaddon.mapping.converters.ComponentDataToWsConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;

public class DistComponentDataToWsConverter extends ComponentDataToWsConverter {

    private static final String DIST_MAIN_NAVIGATION_COMPONENT = "DistMainNavigationComponent";

    private static final String ROOT_NAVIGATION_NODE = "rootNavigationNode";

    @Override
    public Map<String, Object> convertMap(Map<String, Object> map) {
        Map<String, Object> cleanedMap = removeEmptyKeysFromMap(map);

        return super.convertMap(cleanedMap);
    }

    protected Map<String, Object> removeEmptyKeysFromMap(Map<String, Object> map) {
        Map<String, Object> cleanedMap = map.entrySet().stream()
                .filter(entry -> isNotBlank(entry.getKey()) && entry.getValue() != null)
                .collect(Collectors.toMap(entry -> trim(entry.getKey()), Map.Entry::getValue));

        return cleanedMap;
    }

    @Override
    public void customize(final MapperFactory factory)
    {
        factory.classMap(AbstractCMSComponentData.class, ComponentWsDTO.class).byDefault()
               .customize(new CustomMapper<AbstractCMSComponentData, ComponentWsDTO>()
               {
                   @Override
                   public void mapAtoB(final AbstractCMSComponentData data, final ComponentWsDTO wsData, final MappingContext mappingContext)
                   {
                       // this field will not be null if it's allowed through fields
                       if (wsData.getOtherProperties() != null)
                       {
                           wsData.setOtherProperties(convertMap(data.getOtherProperties()));
                       }

                       if (isMainNavigationComponent(wsData)) {
                           removeNavigationNodeNonTopChildren(wsData);
                       }
                   }
               }).register();
    }


    private boolean isMainNavigationComponent(ComponentWsDTO wsData) {
        return DIST_MAIN_NAVIGATION_COMPONENT.equals(wsData.getTypeCode());
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
