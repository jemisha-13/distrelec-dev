package com.distrelec.smartedit.visitors;

import com.namics.distrelec.b2b.core.model.cms2.components.DistComponentGroupModel;
import de.hybris.platform.cmsfacades.synchronization.itemvisitors.AbstractCMSComponentModelVisitor;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DistComponentGroupModelVisitor extends AbstractCMSComponentModelVisitor<DistComponentGroupModel> {

    @Override
    public List<ItemModel> visit(DistComponentGroupModel source, List<ItemModel> arg1, Map<String, Object> arg2) {
        return Stream.concat(source.getComponents().stream(),
                super.visit(source, arg1, arg2).stream())
                .collect(Collectors.toList());
    }
}
