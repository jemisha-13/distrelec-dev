package com.namics.distrelec.b2b.backoffice.widgets.advancedsearch.util;

import java.util.Iterator;
import java.util.Objects;

import com.hybris.backoffice.widgets.advancedsearch.AdvancedSearchMode;
import com.hybris.backoffice.widgets.advancedsearch.AdvancedSearchOperatorService;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.util.AdvancedSearchDataUtil;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.AdvancedSearch;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.ConnectionOperatorType;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldListType;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.core.util.Validate;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.search.data.SortData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import com.namics.distrelec.b2b.backoffice.widgets.advancedsearch.impl.DistAdvancedSearchData;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zk.ui.select.annotation.WireVariable;

/**
 * overrides {@link #buildAdvancedSearchData(AdvancedSearch, DataType)} to create
 */
public class DistAdvancedSearchDataUtil extends AdvancedSearchDataUtil {

    @WireVariable
    private PermissionFacade permissionFacade;
    @WireVariable
    private AdvancedSearchOperatorService advancedSearchOperatorService;

    @Override
    public AdvancedSearchData buildAdvancedSearchData(AdvancedSearch advancedSearch, DataType dataType) {
        Validate.notNull("Configuration may not be null", new Object[]{advancedSearch});
        Validate.notNull("Data type may not be null", new Object[]{dataType});
        AdvancedSearchData model = new DistAdvancedSearchData(advancedSearch.getFieldList());
        model.setAdvancedSearchMode(AdvancedSearchMode.ADVANCED);
        FieldListType fieldList = advancedSearch.getFieldList();
        if (fieldList != null) {
            Iterator var5 = fieldList.getField().iterator();

            label44:
            while(true) {
                FieldType field;
                do {
                    do {
                        if (!var5.hasNext()) {
                            break label44;
                        }

                        field = (FieldType)var5.next();
                    } while(!permissionFacade.canReadProperty(dataType.getCode(), field.getName()));
                } while(!field.isSelected() && !field.isMandatory());

                DataAttribute dataAttr = dataType.getAttribute(field.getName());
                tryAddCondition(advancedSearchOperatorService, dataType, model, field, dataAttr);
            }
        }

        model.setTypeCode(dataType.getCode());
        if (advancedSearch.getSortField() != null) {
            model.setSortData(new SortData(advancedSearch.getSortField().getName(), advancedSearch.getSortField().isAsc()));
        }

        if (fieldList != null) {
            model.setIncludeSubtypes(fieldList.isIncludeSubtypes());
        }

        if (advancedSearch.getConnectionOperator() != null) {
            model.setGlobalOperator(Objects.equals(advancedSearch.getConnectionOperator(), ConnectionOperatorType.OR) ? ValueComparisonOperator.OR : ValueComparisonOperator.AND);
        } else {
            model.setGlobalOperator(ValueComparisonOperator.OR);
        }

        return model;
    }

    @Required
    public void setPermissionFacade(PermissionFacade permissionFacade) {
        this.permissionFacade = permissionFacade;
        super.setPermissionFacade(permissionFacade);
    }

    @Required
    public void setAdvancedSearchOperatorService(AdvancedSearchOperatorService advancedSearchOperatorService) {
        this.advancedSearchOperatorService = advancedSearchOperatorService;
        super.setAdvancedSearchOperatorService(advancedSearchOperatorService);
    }
}
