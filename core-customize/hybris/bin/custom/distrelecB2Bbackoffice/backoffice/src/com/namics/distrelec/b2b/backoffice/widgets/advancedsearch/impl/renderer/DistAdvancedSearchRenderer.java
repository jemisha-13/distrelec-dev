package com.namics.distrelec.b2b.backoffice.widgets.advancedsearch.impl.renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hybris.backoffice.widgets.advancedsearch.AdvancedSearchOperatorService;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchDataConditionEvaluator;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionDataList;
import com.hybris.backoffice.widgets.advancedsearch.impl.renderer.AdvancedSearchRenderer;
import com.hybris.cockpitng.components.Actions;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.AdvancedSearch;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.search.data.SearchAttributeDescriptor;
import com.hybris.cockpitng.search.data.SortData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import com.hybris.cockpitng.testing.annotation.InextensibleMethod;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.util.YTestTools;
import com.namics.distrelec.b2b.backoffice.widgets.advancedsearch.impl.DistAdvancedSearchData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

public class DistAdvancedSearchRenderer extends AdvancedSearchRenderer {

    private static final SearchConditionData GRID_ADD_LINE_HOLDER = new SearchConditionData(null, null, null);

    private final AdvancedSearchOperatorService advancedSearchOperatorService;
    private final LabelService labelService;
    private final PermissionFacade permissionFacade;

    public DistAdvancedSearchRenderer(TypeFacade typeFacade, LabelService labelService,
            AdvancedSearchOperatorService advancedSearchOperatorService, PermissionFacade permissionFacade,
            CockpitLocaleService cockpitLocaleService) {
        super(typeFacade, labelService, advancedSearchOperatorService, permissionFacade, cockpitLocaleService);
        this.advancedSearchOperatorService = advancedSearchOperatorService;
        this.labelService = labelService;
        this.permissionFacade = permissionFacade;
    }

    public DistAdvancedSearchRenderer(TypeFacade typeFacade, LabelService labelService,
            WidgetInstanceManager widgetInstanceManager, AdvancedSearchOperatorService advancedSearchOperatorService,
            PermissionFacade permissionFacade, CockpitLocaleService cockpitLocaleService) {
        super(typeFacade, labelService, widgetInstanceManager, advancedSearchOperatorService, permissionFacade, cockpitLocaleService);
        this.advancedSearchOperatorService = advancedSearchOperatorService;
        this.labelService = labelService;
        this.permissionFacade = permissionFacade;
    }

    public DistAdvancedSearchRenderer(TypeFacade typeFacade, LabelService labelService,
            WidgetInstanceManager widgetInstanceManager, AdvancedSearchOperatorService advancedSearchOperatorService,
            PermissionFacade permissionFacade, CockpitLocaleService cockpitLocaleService,
            Consumer<Event> editorsEventConsumer) {
        super(typeFacade, labelService, widgetInstanceManager, advancedSearchOperatorService, permissionFacade, cockpitLocaleService, editorsEventConsumer);
        this.advancedSearchOperatorService = advancedSearchOperatorService;
        this.labelService = labelService;
        this.permissionFacade = permissionFacade;
    }

    @Override
    public void renderVisible(Grid container, Radiogroup sortControlCnt, Actions actionSlot, AdvancedSearch configuration, DataType dataType) {
        final AdvancedSearchData advancedSearchData = getSearchDataFromModel();
        getFieldEditors().clear();
        final Column sortOrderColumn = getSortOrderColumn(container);
        container.setModel(prepareGridModel());
        adjustSortOrderColumnVisibility(sortOrderColumn, advancedSearchData);
        container.setRowRenderer(new RowRenderer<SearchConditionData>() {
            public void render(Row row, SearchConditionData data, int rowIndex) {
                if (Objects.equals(GRID_ADD_LINE_HOLDER, data)) {
                    renderAddAttributeRow(container, row, rowIndex, data, configuration, dataType);
                } else {
                    addCellLabel(row, data);
                    addCellOperator(row, data, rowIndex);
                    addCellEditor(row, data, rowIndex, sortOrderColumn);
                    addCellSort(row, data);
                    addCellRemove(row, data, rowIndex);
                    mergeCellsForLabelAndSortButtonsIfNecessary(data, rowIndex);
                    applyRowClassIfItIsLastRowForGivenCondition(data, row);
                }

            }

            private void addCellLabel(Row row, SearchConditionData data) {
                String qualifier = data.getFieldType().getName();
                int conditionIndexForGivenQualifier = advancedSearchData.getConditions(qualifier).indexOf(data);
                if (conditionIndexForGivenQualifier == 0) {
                    Cell cellLabel = new Cell();
                    Label label = new Label(getAttributeLabel(qualifier));
                    UITools.modifySClass(label, "yw-advanced-search-cnd-disabled", data.getFieldType().isDisabled());
                    cellLabel.appendChild(label);
                    row.appendChild(cellLabel);
                }
            }

            private void addCellOperator(Row row, SearchConditionData data, int rowIndex) {
                Component operatorComponent = createOperatorComponent(data.getFieldType(), data.getOperator(), createSearchOperatorSelectionListener(data, rowIndex), rowIndex);
                Cell cellOperator = new Cell();
                UITools.modifySClass(cellOperator, "yw-advancedsearch-line", true);
                YTestTools.modifyYTestId(cellOperator, "operators-container-" + data.getFieldType().getName());
                cellOperator.appendChild(operatorComponent);
                row.appendChild(cellOperator);
            }

            private void addCellEditor(Row row, SearchConditionData data, int rowIndex, Column sortOrderColumnx) {
                Cell cellEditor = new Cell();
                Editor editor = createEditor(data.getFieldType(), rowIndex, data);
                SearchAttributeDescriptor searchAttributeDescriptor = new SearchAttributeDescriptor(data.getFieldType().getName(), rowIndex);
                assignEditorToField(searchAttributeDescriptor, editor);
                adjustEditor(searchAttributeDescriptor, data.getOperator(), data.getFieldType());
                adjustSearchDataModel(searchAttributeDescriptor, data.getOperator());
                UITools.modifySClass(cellEditor, "yw-advancedsearch-line", true);
                YTestTools.modifyYTestId(editor, "editor-" + data.getFieldType().getName() + "-" + rowIndex);
                YTestTools.modifyYTestId(cellEditor, "values-container-" + data.getFieldType().getName());
                cellEditor.appendChild(editor);
                row.appendChild(cellEditor);
                sortOrderColumnx.setVisible(atLeastOneFieldSortable(advancedSearchData));
                markIfMandatoryAndEmpty(editor, data);
            }

            private void markIfMandatoryAndEmpty(Editor editor, SearchConditionData data) {
                AdvancedSearch config = getWidgetInstanceManager().getModel().getValue("advancedSearchConfiguration", AdvancedSearch.class);
                String fieldName = data.getFieldType().getName();
                if (AdvancedSearchDataConditionEvaluator.isMandatory(config, fieldName) && !AdvancedSearchDataConditionEvaluator.atLeastOneConditionProvided(advancedSearchData, fieldName)) {
                    markEmptyMandatoryField(editor);
                }

            }

            private void addCellSort(Row row, SearchConditionData data) {
                String qualifier = data.getFieldType().getName();
                int conditionIndexForGivenQualifier = advancedSearchData.getConditions(qualifier).indexOf(data);
                if (conditionIndexForGivenQualifier == 0) {
                    Cell cellSort = new Cell();
                    SortData sortData = getSortData();
                    Div sortControls = createSortControls(sortControlCnt, data, sortData);
                    cellSort.appendChild(sortControls);
                    row.appendChild(cellSort);
                }

            }

            private SortData getSortData() {
                SortData sortDataFromModel = advancedSearchData.getSortData();
                SortData sortDataFromConfig = extractSortData(configuration);
                return sortDataFromModel != null ? sortDataFromModel : sortDataFromConfig;
            }

            private void addCellRemove(Row row, SearchConditionData data, int rowIndex) {
                Cell cellRemove = new Cell();
                UITools.modifySClass(cellRemove, "yw-advancedsearch-line", true);
                YTestTools.modifyYTestId(cellRemove, "removes-container-" + data.getFieldType().getName());
                if (isAddAttributeRowVisible() && !data.getFieldType().isDisabled()) {
                    cellRemove.appendChild(createRemoveLine(rowIndex, container, advancedSearchData, data));
                }

                row.appendChild(cellRemove);
            }

            private void mergeCellsForLabelAndSortButtonsIfNecessary(SearchConditionData data, int rowIndex) {
                String qualifier = data.getFieldType().getName();
                int conditionIndexForGivenQualifier = advancedSearchData.getConditions(qualifier).indexOf(data);
                if (conditionIndexForGivenQualifier > 0) {
                    Cell firstCellLabelForGivenQualifier = (Cell)container.getCell(rowIndex - conditionIndexForGivenQualifier, 0);
                    firstCellLabelForGivenQualifier.setRowspan(conditionIndexForGivenQualifier + 1);
                    firstCellLabelForGivenQualifier.setSclass("yw-advanced-search-rowspan");
                    Cell firstCellWithSortButtonsForGivenQualifier = (Cell)container.getCell(rowIndex - conditionIndexForGivenQualifier, 3);
                    firstCellWithSortButtonsForGivenQualifier.setRowspan(conditionIndexForGivenQualifier + 1);
                    firstCellWithSortButtonsForGivenQualifier.setSclass("yw-advanced-search-rowspan");
                }

            }

            private void applyRowClassIfItIsLastRowForGivenCondition(SearchConditionData data, Row row) {
                String qualifier = data.getFieldType().getName();
                int numberOfConditions = advancedSearchData.getConditions(qualifier).size();
                int conditionIndexForGivenQualifier = advancedSearchData.getConditions(qualifier).indexOf(data);
                if (conditionIndexForGivenQualifier == numberOfConditions - 1) {
                    UITools.modifySClass(row, "yw-advancedsearch-last-row-for-condition", true);
                }

            }

            private EventListener<SelectEvent> createSearchOperatorSelectionListener(SearchConditionData data, int tmpIndex) {
                return (event) -> {
                    Set selectedObjects = event.getSelectedObjects();
                    if (selectedObjects.size() == 1) {
                        ValueComparisonOperator valueComparisonOperator = (ValueComparisonOperator)selectedObjects.iterator().next();
                        data.updateOperator(valueComparisonOperator);
                        SearchAttributeDescriptor searchAttributeDescriptor = new SearchAttributeDescriptor(data.getFieldType().getName(), tmpIndex);
                        adjustEditor(searchAttributeDescriptor, valueComparisonOperator, data.getFieldType());
                        adjustSearchDataModel(searchAttributeDescriptor, valueComparisonOperator);
                    }

                };
            }

            private Component createOperatorComponent(FieldType field, ValueComparisonOperator selectedOperator, EventListener<SelectEvent> selectionListener, int index) {
                DataAttribute attribute = getDataType().getAttribute(field.getName());
                DataType valueType = attribute.getValueType();
                if (valueType != null && (Boolean.class.equals(valueType.getClazz()) || Boolean.TYPE.equals(valueType.getClazz()))) {
                    return new Div();
                } else {
                    Div operatorContainer = new Div();
                    Combobox operator = createOperator(field.getName(), selectedOperator, selectionListener);
                    YTestTools.modifyYTestId(operator, "operator-" + field.getName() + "-" + index);
                    UITools.modifySClass(operator, "yw-advancedsearch-operator", true);
                    operatorContainer.appendChild(operator);
                    operator.setDisabled(field.isDisabled() || isFieldComparatorDisabled(configuration));
                    addEnterSupport(operator);
                    return operatorContainer;
                }
            }
        });
    }

    @Override
    public ListModelList<SearchConditionData> prepareGridModel() {
        DistAdvancedSearchData searchData = getSearchDataFromModel();
        ListModelList<SearchConditionData> gridModel = new ListModelList();
        if ("Top".equalsIgnoreCase(getAddRowPosition())) {
            gridModel.add(GRID_ADD_LINE_HOLDER);
        }

        DataType dataType = getDataType();
        searchData.getOriginalSearchFields().stream().filter((searchField) -> {
            return ObjectUtils.notEqual("_orphanedSearchConditions", searchField);
        }).filter((searchField) -> {
            return dataType.getAttribute(searchField) != null;
        }).filter((searchField) -> {
            return permissionFacade.canReadProperty(searchData.getTypeCode(), searchField);
        }).forEach((searchField) -> {
            List<SearchConditionData> scData = searchData.getConditions(searchField);
            gridModel.addAll(scData.stream()
                .filter((entry) -> {
                return !(entry instanceof SearchConditionDataList);
            }).collect(Collectors.toList()));
        });
        if ("Bottom".equalsIgnoreCase(getAddRowPosition())) {
            gridModel.add(GRID_ADD_LINE_HOLDER);
        }

        return gridModel;
    }

    @Override
    protected String getAttributeLabel(String qualifier) {
        String attLabel = super.getAttributeLabel(qualifier);
        if (attLabel != null && !attLabel.isEmpty()) {
            return attLabel;
        } else {
            return String.format("[%s]", qualifier);
        }
    }

    @Override
    protected Editor createEditor(FieldType field, int rowIndex, SearchConditionData data, boolean isEditorInAddAttributeRow) {
        Editor editor = super.createEditor(field, rowIndex, data, isEditorInAddAttributeRow);

        if (isEditorInAddAttributeRow) {
            DataAttribute genericAttribute = getDataAttribute(field);
            DistAdvancedSearchData searchData = getSearchDataFromModel();
            if (genericAttribute.isLocalized()) {
                editor.addEventListener("onValueChanged", (event) -> {
                    updateSearchDataForLocalizedValue(searchData.getAdditionalCondition(), editor.getValue());
                });
                editor.addEventListener("onSelect", (event) -> {
                    updateLocalizedEditorValueOnLanguageSelected(editor, event.getData(), searchData.getAdditionalCondition());
                });
            } else {
                editor.addEventListener("onValueChanged", (event) -> {
                    searchData.getAdditionalCondition().updateValue(editor.getValue());
                });
            }
        }

        return editor;
    }

    private void renderAddAttributeRow(Grid container, Row row, int rowIndex, SearchConditionData data, AdvancedSearch configuration, DataType dataType) {
        UITools.modifySClass(row, "yw-add-field-row", true);
        YTestTools.modifyYTestId(row, "addFieldRow");
        Collection<? extends FieldType> selectableAttributes = prepareAlphabeticallySortedAttributeList((List)(configuration.getFieldList() != null ? configuration.getFieldList().getField() : new ArrayList()));
        if (CollectionUtils.isEmpty(selectableAttributes)) {
            Cell cell = new Cell();
            cell.appendChild(new Label(getWidgetInstanceManager().getLabel("cannotSpecifyAnySearchAttribute", new Object[]{labelService.getObjectLabel(dataType.getCode())})));
            row.appendChild(cell);
            cell.setColspan(5);
        } else {
            FieldType selectedField = selectableAttributes.iterator().next();
            Combobox attSelector = new Combobox();
            prepareAttributeSelectorCombobox(attSelector, selectableAttributes, selectedField);
            Button addAttribute = new Button();
            UITools.modifySClass(addAttribute, "yw-advancedsearch-add-btn", true);
            YTestTools.modifyYTestId(addAttribute, "addFieldBtn");
            String selectedFieldName;
            String selectedFieldOperator;
            if (selectedField != null) {
                selectedFieldName = selectedField.getName();
                selectedFieldOperator = selectedField.getOperator();
            } else {
                selectedFieldName = "";
                selectedFieldOperator = "";
            }

            ValueComparisonOperator valueComparisonOperator = advancedSearchOperatorService.findMatchingOperator(dataType.getAttribute(selectedFieldName), selectedFieldOperator);
            Editor editor = createEditor(selectedField, rowIndex, data, true);
            YTestTools.modifyYTestId(editor, "addValueSelector");
            updateEditorStateAccordingToOperator(editor, valueComparisonOperator, selectedField);
            Cell cellOperator = new Cell();
            UITools.modifySClass(cellOperator, "yw-advancedsearch-line", true);
            Combobox operatorSelector = createOperator(selectedFieldName, valueComparisonOperator, createSelectionListenerForOperatorsInAddAttributeRow(row, selectedField));
            YTestTools.modifyYTestId(operatorSelector, "addOperatorSelector");
            UITools.modifySClass(operatorSelector, "yw-advancedsearch-operator", true);
            cellOperator.appendChild(operatorSelector);
            addAttribute.addEventListener("onClick", (event) -> {
                FieldType fieldType = attSelector.getSelectedItem().getValue();
                if (!fieldType.isDisabled()) {
                    Comboitem selectedItem = operatorSelector.getSelectedItem();
                    ValueComparisonOperator operator = selectedItem.getValue();
                    Editor theEditor = getCurrentEditorForAddAttributeRow(row);

                    DataAttribute genericAttribute = this.getDataAttribute(fieldType);
                    if (genericAttribute.isLocalized()) {
                        UITools.modifySClass(editor, "yw-advancedsearch-localized", true);
                        editor.getEventListeners("onValueChanged")
                            .forEach(eventListener -> editor.removeEventListener("onValueChanged", eventListener));
                        editor.getEventListeners("onSelect")
                            .forEach(eventListener -> editor.removeEventListener("onSelect", eventListener));
                    } else {
                        editor.getEventListeners("onValueChanged")
                            .forEach(eventListener -> editor.removeEventListener("onValueChanged", eventListener));
                    }


                    Object fieldValue = theEditor.getValue();
                    DistAdvancedSearchData advancedSearchData = getSearchDataFromModel();
                    advancedSearchData.addCondition(fieldType, operator, fieldValue);
                    advancedSearchData.clearAdditionalCondition();
                    container.setModel(prepareGridModel());
                }

            });
            Cell emptyCellToSatisfyTogglingVisibilityOfSortColumn = new Cell();
            row.appendChild(attSelector);
            row.appendChild(cellOperator);
            row.appendChild(editor);
            row.appendChild(emptyCellToSatisfyTogglingVisibilityOfSortColumn);
            row.appendChild(addAttribute);
            EventListener<Event> changeTypeEvent = (event) -> {
                Comboitem selectedItem = attSelector.getSelectedItem();
                if (selectedItem != null) {
                    FieldType fieldType = selectedItem.getValue();
                    if (fieldType != null) {
                        String fieldName = fieldType.getName();
                        ValueComparisonOperator matchingOperator = advancedSearchOperatorService.findMatchingOperator(dataType.getAttribute(fieldName), fieldType.getOperator());
                        ListModelList<ValueComparisonOperator> model = new ListModelList();
                        Collection<ValueComparisonOperator> available = advancedSearchOperatorService.getAvailableOperators(dataType.getAttribute(fieldType.getName()));
                        model.addAll(available);
                        Component currentEditor = getCurrentEditorForAddAttributeRow(row);
                        Editor newEditor = createEditor(fieldType, rowIndex, data, true);
                        row.insertBefore(newEditor, currentEditor);
                        row.removeChild(currentEditor);
                        if (available.contains(matchingOperator)) {
                            model.setSelection(Collections.singletonList(matchingOperator));
                            updateEditorStateAccordingToOperator(newEditor, matchingOperator, fieldType);
                        }

                        operatorSelector.setModel(model);

                        // added
                        DistAdvancedSearchData advancedSearchData = getSearchDataFromModel();
                        advancedSearchData.addAdditionalCondition(fieldType, matchingOperator, newEditor.getValue());
                    }
                }

            };
            attSelector.addEventListener("onChange", changeTypeEvent);
            attSelector.addEventListener("onOK", changeTypeEvent);

            // added
            if (selectedField != null) {
                String fieldName = selectedField.getName();
                ValueComparisonOperator matchingOperator = advancedSearchOperatorService.findMatchingOperator(dataType.getAttribute(fieldName), selectedField.getOperator());
                DistAdvancedSearchData advancedSearchData = getSearchDataFromModel();
                advancedSearchData.addAdditionalCondition(selectedField, matchingOperator, editor.getValue());
            }
        }
    }

    private static void updateEditorStateAccordingToOperator(Editor editor, ValueComparisonOperator valueComparisonOperator, FieldType fieldType) {
        editor.setReadOnly(!valueComparisonOperator.isRequireValue() || fieldType.isDisabled());
    }

    private void prepareAttributeSelectorCombobox(Combobox attSelector, Collection<? extends FieldType> selectableAttributes, FieldType selectedField) {
        UITools.modifySClass(attSelector, "yw-additional-attributes-selector", true);
        ListModelList<FieldType> attributeModel = new ListModelList();
        attributeModel.addAll(selectableAttributes);
        attributeModel.setSelection(selectedField != null ? Collections.singletonList(selectedField) : Collections.emptyList());
        attSelector.setModel(attributeModel);
        YTestTools.modifyYTestId(attSelector, "addAttributeSelector");
        attSelector.setReadonly(true);
        attSelector.setItemRenderer((item, data, index) -> {
            FieldType fieldType = (FieldType) data;
            item.setDisabled(isFieldDisabled(fieldType));
            item.setLabel(getAttributeLabel(fieldType.getName()));
            item.setValue(data);
        });
    }

    private boolean isFieldDisabled(FieldType fieldType) {
        boolean disabledField = true;
        AdvancedSearchData searchData = getSearchDataFromModel();
        if (!fieldType.isDisabled()) {
            List<SearchConditionData> conditions = searchData.getConditions(fieldType.getName());
            SearchConditionData conditionData;
            if (CollectionUtils.isNotEmpty(conditions)) {
                for(Iterator var5 = conditions.iterator(); var5.hasNext(); disabledField &= conditionData.getFieldType().isDisabled()) {
                    conditionData = (SearchConditionData)var5.next();
                }
            } else {
                disabledField = false;
            }
        }

        return disabledField;
    }

    private Collection<? extends FieldType> prepareAlphabeticallySortedAttributeList(List<FieldType> fields) {
        DataType dataType = getDataType();
        Predicate<FieldType> searchableAttribute = (fieldType) -> {
            DataAttribute attribute = dataType.getAttribute(fieldType.getName());
            return attribute != null && attribute.isSearchable() && permissionFacade.canReadProperty(dataType.getCode(), fieldType.getName());
        };
        Map<String, String> labelsCache = getAttributesLabels(fields);
        Comparator<FieldType> alphabeticalComparator = (first, second) -> {
            String fName = (String) StringUtils.defaultIfBlank((CharSequence)labelsCache.get(first.getName()), "");
            String sName = (String)StringUtils.defaultIfBlank((CharSequence)labelsCache.get(second.getName()), "");
            return fName.compareToIgnoreCase(sName);
        };
        return fields.stream().filter(searchableAttribute).sorted(alphabeticalComparator).collect(Collectors.toList());
    }

    private boolean atLeastOneFieldSortable(AdvancedSearchData advancedSearch) {
        List<SearchConditionData> conditions = getSearchConditionData(advancedSearch);
        Iterator var3 = getFieldTypes(conditions).iterator();

        FieldType fieldType;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            fieldType = (FieldType)var3.next();
        } while(!isSortable(fieldType) || fieldType.isDisabled());

        return true;
    }

    private boolean isAddAttributeRowVisible() {
        return !"None".equalsIgnoreCase(getAddRowPosition());
    }

    private Component createRemoveLine(int attributeRowIndex, Grid container, AdvancedSearchData advancedSearchData, SearchConditionData data) {
        Div removeContainer = new Div();
        UITools.modifySClass(removeContainer, "yw-remove-container", true);
        if (!data.getFieldType().isMandatory()) {
            Button remove = new Button();
            remove.addEventListener("onClick", (event) -> {
                advancedSearchData.removeCondition(getActualIndexOfConditionInAdvancedSearchData(attributeRowIndex));
                container.setModel(prepareGridModel());
            });
            UITools.modifySClass(remove, "yw-advancedsearch-delete-btn ye-delete-btn", true);
            YTestTools.modifyYTestId(remove, "remove-" + data.getFieldType().getName() + "-" + attributeRowIndex);
            removeContainer.appendChild(remove);
        }

        return removeContainer;
    }

    private Combobox createOperator(String fieldTypeName, ValueComparisonOperator selectedOperator, EventListener<SelectEvent> selectionListener) {
        DataAttribute attribute = getDataType().getAttribute(fieldTypeName);
        Combobox operators = new Combobox();
        operators.setReadonly(true);
        ListModelList<ValueComparisonOperator> model = new ListModelList();
        Collection<ValueComparisonOperator> availableOperators = advancedSearchOperatorService.getAvailableOperators(attribute);
        if (selectedOperator != null && availableOperators.contains(selectedOperator)) {
            model.setSelection(Collections.singletonList(selectedOperator));
        }

        model.addAll(availableOperators);
        operators.setModel(model);
        operators.setItemRenderer((item, operator, index) -> {
            item.setLabel(Labels.getLabel(((ValueComparisonOperator)operator).getLabelKey()));
            item.setValue(operator);
        });
        if (selectionListener != null) {
            operators.addEventListener("onSelect", selectionListener);
        }

        return operators;
    }

    private boolean isFieldComparatorDisabled(AdvancedSearch configuration) {
        boolean disabledByConfig = configuration.getFieldList().isDisableAttributesComparator();
        boolean disabledBySetting = getWidgetInstanceManager().getWidgetSettings().getBoolean("disableAttributesComparator");
        return disabledByConfig || disabledBySetting;
    }

    private static void markEmptyMandatoryField(Editor editor) {
        HtmlBasedComponent parent = (HtmlBasedComponent)editor.getParent();
        UITools.modifySClass(parent, "yw-advancedsearch-mandatory-field", true);
        Clients.scrollIntoView(editor);
    }

    private static Column getSortOrderColumn(Grid container) {
        Column sortOrderColumn = null;
        Iterator var2 = container.getColumns().getChildren().iterator();

        while(var2.hasNext()) {
            Component column = (Component)var2.next();
            if (column.getId().equalsIgnoreCase("sortOrderColumn")) {
                sortOrderColumn = (Column)column;
            }
        }

        return sortOrderColumn;
    }

    private static Editor getCurrentEditorForAddAttributeRow(Row row) {
        return (Editor)row.getChildren().get(2);
    }

    private EventListener<SelectEvent> createSelectionListenerForOperatorsInAddAttributeRow(Row row, FieldType fieldType) {
        return (selectEvent) -> {
            if (selectEvent.getSelectedItems() != null && !selectEvent.getSelectedItems().isEmpty()) {
                ValueComparisonOperator operator = (ValueComparisonOperator)selectEvent.getSelectedObjects().iterator().next();
                Editor theEditor = getCurrentEditorForAddAttributeRow(row);
                updateEditorStateAccordingToOperator(theEditor, operator, fieldType);

                // added
                DistAdvancedSearchData advancedSearchData = getSearchDataFromModel();
                advancedSearchData.updateOperatorOfAdditionalCondition(operator);
            }

        };
    }

    protected void updateSearchDataForLocalizedValue(SearchConditionData scData, Object editorsValue) {
        if (editorsValue instanceof Map) {
            Map<Locale, Object> localizedValue = (Map)editorsValue;
            Optional<Locale> currentLocale = localizedValue.keySet().stream().findFirst();
            Optional<Entry<Locale, Object>> first = localizedValue.entrySet().stream().filter((entry) -> {
                return entry.getValue() != null;
            }).findFirst();
            if (first.isPresent()) {
                scData.updateLocalizedValue((Locale)((Entry)first.get()).getKey(), (first.get()).getValue());
            } else if (currentLocale.isPresent()) {
                scData.updateLocalizedValue(currentLocale.get(), null);
            } else {
                scData.resetLocalizedValues();
            }
        } else {
            scData.resetLocalizedValues();
        }

    }

    protected void updateLocalizedEditorValueOnLanguageSelected(Editor editor, Object eventData, SearchConditionData scData) {
        if (eventData instanceof Locale) {
            Map<Locale, Object> updateEditorValue = new HashMap();
            Object value = editor.getValue();
            if (value instanceof Map && !((Map)value).isEmpty()) {
                Map<Locale, Object> currentEditorValue = (Map)value;
                Optional<Object> foundValue = currentEditorValue.values().stream().filter((e) -> {
                    return e != null;
                }).findFirst();
                if (foundValue.isPresent()) {
                    updateEditorValue.put((Locale)eventData, foundValue.get());
                } else {
                    updateEditorValue.put((Locale)eventData, null);
                }

                editor.setValue(updateEditorValue);
                updateSearchDataForLocalizedValue(scData, updateEditorValue);
            } else {
                updateEditorValue.put((Locale)eventData, null);
                editor.setValue(updateEditorValue);
                updateSearchDataForLocalizedValue(scData, updateEditorValue);
            }
        }

    }

    @InextensibleMethod
    private Map<String, String> getAttributesLabels(List<FieldType> fields) {
        String typeCode = getDataType().getCode();
        if (StringUtils.isNotBlank(typeCode) && typeCode.indexOf(46) == -1) {
            Map<String, String> fieldLabelMap = new HashMap();
            fields.forEach((field) -> {
                if (!fieldLabelMap.containsKey(field.getName())) {
                    fieldLabelMap.put(field.getName(), labelService.getObjectLabel(typeCode + '.' + field.getName()));
                }

            });
            return fieldLabelMap;
        } else {
            return new HashMap();
        }
    }

    private static List<SearchConditionData> getSearchConditionData(AdvancedSearchData advancedSearch) {
        List<SearchConditionData> conditions = new ArrayList();
        advancedSearch.getSearchFields().stream().filter((field) -> {
            return ObjectUtils.notEqual("_orphanedSearchConditions", field);
        }).forEach((field) -> {
            conditions.addAll(advancedSearch.getConditions(field));
        });
        return conditions;
    }

    private Collection<? extends FieldType> getFieldTypes(List<SearchConditionData> conditions) {
        List<FieldType> fields = conditions.stream().map(SearchConditionData::getFieldType).collect(Collectors.toList());
        return prepareAlphabeticallySortedAttributeList(fields);
    }

    private int getActualIndexOfConditionInAdvancedSearchData(int rowIndex) {
        return rowIndex - ("Top".equalsIgnoreCase(getAddRowPosition()) ? 1 : 0);
    }

    protected DistAdvancedSearchData getSearchDataFromModel() {
        return (DistAdvancedSearchData) super.getSearchDataFromModel();
    }
}
