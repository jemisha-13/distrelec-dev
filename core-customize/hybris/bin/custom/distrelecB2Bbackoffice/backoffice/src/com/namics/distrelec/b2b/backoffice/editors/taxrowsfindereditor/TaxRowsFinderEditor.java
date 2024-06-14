package com.namics.distrelec.b2b.backoffice.editors.taxrowsfindereditor;

import java.util.stream.Collectors;

import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchInitContext;
import com.hybris.cockpitng.core.config.CockpitConfigurationException;
import com.hybris.cockpitng.core.config.impl.DefaultConfigContext;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.AdvancedSearch;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.ConnectionOperatorType;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldListType;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.Parameter;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.SortFieldType;
import com.hybris.cockpitng.editors.CockpitEditorRenderer;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.europe1.enums.ProductTaxGroup;
import de.hybris.platform.europe1.enums.UserTaxGroup;
import de.hybris.platform.europe1.model.TaxRowModel;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;

public class TaxRowsFinderEditor extends AbstractComponentWidgetAdapterAware implements CockpitEditorRenderer<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(TaxRowsFinderEditor.class);

    @Override
    public void render(Component parent, EditorContext<Object> editorContext, EditorListener<Object> editorListener) {
        Div cnt = new Div();
        Button button = this.createFinderButton();
        button.addEventListener("onClick", (event) -> {
            AdvancedSearchData searchData = new AdvancedSearchData();
            searchData.setTypeCode(TaxRowModel._TYPECODE);
            WidgetInstanceManager wim = (WidgetInstanceManager)editorContext.getParameter("wim");
            searchData.setGlobalOperator(ValueComparisonOperator.AND);
            AdvancedSearchInitContext initContext = this.createSearchContext(searchData, wim, editorContext);
            this.sendOutput("finderOutput", initContext);
        });
        parent.appendChild(cnt);
        cnt.appendChild(button);
    }

    protected Button createFinderButton() {
        return new Button(Labels.getLabel("hmc.findtaxrowssfortaxclass"));
    }

    protected AdvancedSearchInitContext createSearchContext(AdvancedSearchData searchData, WidgetInstanceManager wim, EditorContext<Object> editorContext) {
        AdvancedSearch originalConfig = this.loadAdvancedConfiguration(wim, "advanced-search");
        if (originalConfig == null) {
            return new AdvancedSearchInitContext(searchData, null);
        } else {
            AdvancedSearch config = new AdvancedSearchWrapper(originalConfig, Boolean.TRUE);

            Object parentObject = editorContext.getParameter("parentObject");
            for (FieldType field : config.getFieldList().getField()) {
                if (matchField(field, parentObject, ProductTaxGroup.class, TaxRowModel.PG)
                        || matchField(field, parentObject, UserTaxGroup.class, TaxRowModel.UG)) {
                    addCondition(searchData, field, parentObject);
                }
            }

            return new AdvancedSearchInitContext(searchData, config);
        }
    }

    protected boolean matchField(FieldType field, Object parentObject, Class<?> objectClass, String fieldName) {
        boolean isFieldNameMatched = fieldName.equals(field.getName());

        if (isFieldNameMatched) {
            if (parentObject instanceof EnumerationValueModel) {
                EnumerationValueModel enumValue = (EnumerationValueModel) parentObject;
                return enumValue.getItemtype().equals(objectClass.getSimpleName());
            }
        }
        return false;
    }

    protected void addCondition(AdvancedSearchData searchData, FieldType field, Object parentObject) {
        FieldType groupsFieldClone = new FieldType();
        groupsFieldClone.setDisabled(Boolean.TRUE);
        groupsFieldClone.setEditor(field.getEditor());
        groupsFieldClone.setMandatory(field.isMandatory());
        groupsFieldClone.setMergeMode(field.getMergeMode());
        groupsFieldClone.setName(field.getName());
        groupsFieldClone.setOperator(field.getOperator());
        groupsFieldClone.setPosition(field.getPosition());
        groupsFieldClone.setSelected(field.isSelected());
        groupsFieldClone.setSortable(field.isSortable());
        if (CollectionUtils.isNotEmpty(field.getEditorParameter())) {
            groupsFieldClone.getEditorParameter().addAll(field.getEditorParameter().stream().map((param) -> {
                Parameter paramClone = new Parameter();
                paramClone.setName(param.getName());
                paramClone.setValue(param.getValue());
                return paramClone;
            }).collect(Collectors.toList()));
        }

        searchData.addCondition(groupsFieldClone, ValueComparisonOperator.EQUALS, parentObject);
        searchData.setIncludeSubtypes(Boolean.TRUE);
    }

    protected AdvancedSearch loadAdvancedConfiguration(WidgetInstanceManager wim, String name) {
        DefaultConfigContext context = new DefaultConfigContext(name, TaxRowModel._TYPECODE);

        try {
            return wim.loadConfiguration(context, AdvancedSearch.class);
        } catch (CockpitConfigurationException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Could not find Advanced Search configuration.", ex);
            }

            return null;
        }
    }

    /** @deprecated */
    @Deprecated
    private class AdvancedSearchWrapper extends AdvancedSearch {
        private final AdvancedSearch parent;
        private Boolean disableSimpleSearchOverride;

        AdvancedSearchWrapper(AdvancedSearch parent, Boolean disableSimpleSearchOverride) {
            this.parent = parent;
            this.disableSimpleSearchOverride = disableSimpleSearchOverride;
        }

        public FieldListType getFieldList() {
            return this.parent.getFieldList();
        }

        public void setFieldList(FieldListType value) {
            this.parent.setFieldList(value);
        }

        public SortFieldType getSortField() {
            return this.parent.getSortField();
        }

        public void setSortField(SortFieldType value) {
            this.parent.setSortField(value);
        }

        public ConnectionOperatorType getConnectionOperator() {
            return this.parent.getConnectionOperator();
        }

        public void setConnectionOperator(ConnectionOperatorType value) {
            this.parent.setConnectionOperator(value);
        }

        public boolean isDisableAutoSearch() {
            return this.parent.isDisableAutoSearch();
        }

        public void setDisableAutoSearch(Boolean value) {
            this.disableSimpleSearch = value;
        }

        public boolean isDisableSimpleSearch() {
            return this.disableSimpleSearchOverride != null && this.disableSimpleSearchOverride;
        }

        public void setDisableSimpleSearch(Boolean value) {
            this.disableSimpleSearchOverride = value;
        }
    }
}
