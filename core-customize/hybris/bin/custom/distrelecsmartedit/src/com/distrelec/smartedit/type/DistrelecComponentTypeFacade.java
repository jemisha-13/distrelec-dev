package com.distrelec.smartedit.type;

import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmsfacades.types.impl.DefaultComponentTypeFacade;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class DistrelecComponentTypeFacade extends DefaultComponentTypeFacade {

    private List<ComponentTypeDataPopulator> componentTypeDataPopulators;

    @Override
    public ComponentTypeData getComponentTypeByCode(String code) throws ComponentTypeNotFoundException {
        ComponentSearchData searchData = getComponentSearchData(code);
        ComponentTypeData componentTypeData = super.getComponentTypeByCode(searchData.getCode());
        componentTypeDataPopulators.stream().forEach(componentTypeDataPopulator -> componentTypeDataPopulator.populate(componentTypeData, searchData));
        return componentTypeData;
    }

    private ComponentSearchData getComponentSearchData(String code) {
        String[] data = code.split("\\|");
        String componentCode = data[0];
        String catalogId = null;
        if (data.length > 1) {
            catalogId = data[1];
        }
        return new ComponentSearchData(componentCode, catalogId);
    }

    public static class ComponentSearchData {
        private final String code;
        private final String catalogId;

        public ComponentSearchData(String code, String catalogId) {
            this.code = code;
            this.catalogId = catalogId;
        }

        public String getCode() {
            return code;
        }

        public String getCatalogId() {
            return catalogId;
        }
    }

    public List<ComponentTypeDataPopulator> getComponentTypeDataPopulators() {
        return componentTypeDataPopulators;
    }

    @Required
    public void setComponentTypeDataPopulators(List<ComponentTypeDataPopulator> componentTypeDataPopulators) {
        this.componentTypeDataPopulators = componentTypeDataPopulators;
    }
}
