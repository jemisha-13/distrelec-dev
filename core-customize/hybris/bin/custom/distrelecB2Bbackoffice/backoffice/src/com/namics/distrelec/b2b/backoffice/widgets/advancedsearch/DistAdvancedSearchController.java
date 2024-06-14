package com.namics.distrelec.b2b.backoffice.widgets.advancedsearch;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.hybris.backoffice.widgets.advancedsearch.AdvancedSearchController;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchInitContext;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.AdvancedSearch;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.namics.distrelec.b2b.backoffice.widgets.advancedsearch.impl.DistAdvancedSearchData;
import de.hybris.platform.core.model.ItemModel;

public class DistAdvancedSearchController extends AdvancedSearchController {

    /**
     * Overrides default implementation to instance DistAdvancedSearchData instead of original AdvancedSearchData.
     */
    @SocketEvent(
        socketId = "initContext"
    )
    @Override
    public void initializeWithContext(AdvancedSearchInitContext initContext) {
        if (initContext != null) {
            AdvancedSearchData searchData = initContext.getAdvancedSearchData();
            this.setValue("initContext", initContext);
            this.setValue("InitializedFromInitCtx", Boolean.TRUE);
            if (searchData != null) {
                AdvancedSearch config = initContext.getAdvancedSearchConfig();
                if (config == null) {
                    config = this.loadAdvancedConfiguration(searchData.getTypeCode());
                }

                DataType dataType = this.loadDataTypeForCode(searchData.getTypeCode());
                if (dataType != null) {
                    this.setActionSlotTypeCode(dataType.getCode());
                    this.resetSimpleSearchTerm();
                    this.resetFacetsOnChangeType();
                    DistAdvancedSearchData coopyOfSearchData = new DistAdvancedSearchData(searchData);
                    this.adjustWidgetModel(config, coopyOfSearchData, true, dataType);
                }

            }
        }
    }

    /**
     * Replaces a default implementation to allow searching on all item type attributes instead of only those defined
     * by backoffice config files.
     */
    @Override
    protected AdvancedSearch loadAdvancedConfiguration(String type) {
        AdvancedSearch advancedSearch = super.loadAdvancedConfiguration(type);

        // get item type
        DataType dataType = loadDataTypeForCode(type);
        Set<String> attrNames = gatherAttributeNames(dataType);

        List<FieldType> fields = advancedSearch.getFieldList().getField();

        Iterator<FieldType> fieldTypeIt = fields.iterator();
        while (fieldTypeIt.hasNext()) {
            FieldType fieldType = fieldTypeIt.next();
            String attrName = fieldType.getName();

            // remove already defined attributes
            attrNames.remove(attrName);

            if (ItemModel.ITEMTYPE.equals(attrName)) {
                // remove item type
                fieldTypeIt.remove();
            }
        }

        // create fields for not defined attributes
        for (String attrName : attrNames) {
            DataAttribute dataAttr = dataType.getAttribute(attrName);
            FieldType fieldType = createFieldType(dataAttr);
            fields.add(fieldType);
        }

        return advancedSearch;
    }

    @Override
    protected void resetFacetsOnChangeType() {
        super.resetFacetsOnChangeType();
        if (!isSimpleSearchActive()) {
            updateOpenStateSClass(true);
        }
    }

    protected Set<String> gatherAttributeNames(DataType dataType) {
        return dataType.getAttributes().stream()
            .map(DataAttribute::getQualifier)
            .collect(Collectors.toSet());
    }

    protected FieldType createFieldType(DataAttribute dataAttribute) {
        FieldType fieldType = new FieldType();
        fieldType.setName(dataAttribute.getQualifier());
        return fieldType;
    }
}
