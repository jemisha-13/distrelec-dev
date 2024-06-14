package com.namics.distrelec.b2b.core.service.db;

import de.hybris.platform.core.model.ItemModel;

import java.util.Collection;

public interface MassDeleteService {

    public void deleteAll(Collection<? extends ItemModel> items);

}
