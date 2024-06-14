package com.distrelec.smartedit.synchronization;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cmsfacades.synchronization.itemcollector.BasicContentSlotItemCollector;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.stream.Collectors;

public class DistrelecContentSlotItemCollector extends BasicContentSlotItemCollector {

    @Override
    public List<? extends ItemModel> collect(ContentSlotModel item) {
        return super.collect(item).stream()
                .filter(collectedItem -> {
                    if(collectedItem instanceof CMSItemModel){
                        CMSItemModel cmsItem = (CMSItemModel) collectedItem;
                        return cmsItem.getCatalogVersion().equals(item.getCatalogVersion());
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
