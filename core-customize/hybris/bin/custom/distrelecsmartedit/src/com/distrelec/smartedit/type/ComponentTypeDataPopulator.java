package com.distrelec.smartedit.type;

import de.hybris.platform.cmsfacades.data.ComponentTypeData;

public interface ComponentTypeDataPopulator {

    void populate(ComponentTypeData componentTypeData, DistrelecComponentTypeFacade.ComponentSearchData componentSearchData);

}
