package com.namics.distrelec.b2b.core.service.db.impl;

import com.namics.distrelec.b2b.core.service.db.MassDeleteService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.directpersistence.CacheInvalidator;
import de.hybris.platform.directpersistence.CrudEnum;
import de.hybris.platform.directpersistence.PersistResult;
import de.hybris.platform.directpersistence.impl.DefaultPersistResult;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MassDeleteServiceImpl implements MassDeleteService {

    @Autowired
    private ModelService modelService;

    @Autowired
    private CacheInvalidator cacheInvalidator;

    @Autowired
    private TypeService typeService;

    @Override
    public void deleteAll(Collection<? extends ItemModel> items) {
        Assert.notNull(items, "Item list must not be null");
        Assert.isTrue(items.size() <= 1000, "Maximum of 1000 elements allowed");

        if (!items.isEmpty()) {
            try (Connection connection = Registry.getCurrentTenant().getDataSource().getConnection();
                 PreparedStatement statement = prepareDbStatement(items, connection)) {
                statement.executeUpdate();
                removeFromCache(items);
            } catch (Exception e) {
                throw new RuntimeException("Mass delete failed. Error: " + e.getMessage());
            }
        }
    }

    private void removeFromCache(Collection<? extends ItemModel> items) {
        List<PersistResult> persistResults = items.stream().map(item -> {
            Item jaloItem = modelService.toPersistenceLayer(item);
            return new DefaultPersistResult(CrudEnum.DELETE, item.getPk(), jaloItem.getPersistenceVersion(), modelService.getModelType(item));
        }).collect(Collectors.toList());
        cacheInvalidator.invalidate(persistResults);
    }

    private PreparedStatement prepareDbStatement(Collection<? extends ItemModel> items, Connection connection) throws SQLException {
        String query = getDeleteQuery(items);
        PreparedStatement statement = connection.prepareStatement(query);
        int index = 1;
        for (ItemModel item : items) {
            statement.setLong(index, item.getPk().getLong());
            index++;
        }
        return statement;
    }

    private String getDeleteQuery(Collection<? extends ItemModel> items) {
        String databaseTable = findTableName(items);
        String beginQueryText = "DELETE FROM " + databaseTable + " WHERE pk IN (";
        String midQueryText = StringUtils.repeat("?", ",", items.size());
        String endQueryText = ")";
        return beginQueryText + midQueryText + endQueryText;
    }

    private String findTableName(Collection<? extends ItemModel> items) {
        ItemModel firstItem = items.iterator().next();
        ComposedTypeModel composedType = typeService.getComposedTypeForClass(firstItem.getClass());
        return composedType.getTable();
    }
}
