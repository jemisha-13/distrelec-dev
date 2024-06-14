package com.namics.distrelec.b2b.backoffice.actions.export.csv;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.export.csv.ListViewExportCSVAction;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListView;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.widgets.collectionbrowser.CollectionBrowserController;
import com.hybris.cockpitng.widgets.util.UILabelUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Difference from original action is support for exporting only selected objects.
 */
public class DistListViewExportCSVAction extends ListViewExportCSVAction {

    private static final Logger LOG = LoggerFactory.getLogger(DistListViewExportCSVAction.class);

    @Override
    public ActionResult<Object> perform(ActionContext<Map> ctx) {
        Pageable pageable = (Pageable) ctx.getData().get("pageable");
        ListView listView = (ListView) ctx.getData().get("columnConfig");
        Collection<Object> selectedObjects = (Collection<Object>) ctx.getData().get(CollectionBrowserController.MODEL_SELECTED_OBJECTS);

        if (selectedObjects == null) {
            selectedObjects = pageable.getAllResults();
        }

        String csvContent = createCsv(pageable, listView, selectedObjects);
        this.writeBinaryResponse(csvContent);
        return new ActionResult("success");
    }

    private String createCsv(Pageable pageable, ListView listView, Collection<Object> selectedObjects) {
        StringBuilder builder = new StringBuilder();
        List<ListColumn> columnsToRender = findColumnsPrintableInCSV(listView.getColumn());
        createCsvHeader(builder, pageable, columnsToRender);
        createCsvContent(builder, columnsToRender, selectedObjects);
        return builder.toString();
    }

    private List<ListColumn> findColumnsPrintableInCSV(List<ListColumn> columns) {
        return columns.stream().filter((listColumn) -> {
            return StringUtils.isBlank(listColumn.getSpringBean()) && StringUtils.isBlank(listColumn.getClazz());
        }).collect(Collectors.toList());
    }

    private void createCsvHeader(StringBuilder builder, Pageable pageable, List<ListColumn> columns) {
        Iterator columnIt = columns.iterator();

        while(columnIt.hasNext()) {
            ListColumn listColumn = (ListColumn)columnIt.next();
            String columnHeaderLabel = UILabelUtil.getColumnHeaderLabel(listColumn, pageable.getTypeCode(), getLabelService());
            builder.append(wrapHeaderForCSV(this.escapeForCSV(columnHeaderLabel))).append(";");
        }

        builder.append('\n');
    }

    /**
     * This method differs from original one by filtering only selected objects are exported.
     */
    private void createCsvContent(StringBuilder builder, List<ListColumn> columns, Collection<Object> selectedObjects) {

        for(Iterator objectIt = selectedObjects.iterator(); objectIt.hasNext(); builder.append('\n')) {
            Object object = objectIt.next();

            try {
                String dataTypeCode = this.getTypeFacade().getType(object);
                DataType dataType = StringUtils.isBlank(dataTypeCode) ? null : this.getTypeFacade().load(dataTypeCode);
                Iterator columnIt = columns.iterator();

                while (columnIt.hasNext()) {
                    ListColumn listColumn = (ListColumn) columnIt.next();
                    String stringValue = getLabel(object, dataType, listColumn);
                    builder.append(this.escapeForCSV(stringValue)).append(";");
                }
            } catch (TypeNotFoundException e) {
                LOG.warn("Could not find type", e);
            }
        }

    }

    private String getLabel(Object object, DataType dataType, ListColumn listColumn) {
        String stringValue = "";
        String qualifier = listColumn.getQualifier();
        if (!canReadProperty(object, qualifier)) {
            return this.getLabelService().getAccessDeniedLabel(object);
        } else {
            Object value = this.getPropertyValueService().readValue(object, qualifier);
            DataAttribute attribute = dataType != null ? dataType.getAttribute(qualifier) : null;
            if (attribute == null || attribute.getValueType() == null || !attribute.getValueType().isAtomic()) {
                stringValue = getLabel(qualifier, value);
            }

            if (value instanceof HashMap) {
                Locale currentLocale = getCockpitLocaleService().getCurrentLocale();
                String localizedValue = (String)((HashMap)value).get(currentLocale);
                stringValue = (String)StringUtils.defaultIfBlank(localizedValue, "");
            } else if (StringUtils.isBlank(stringValue)) {
                stringValue = value == null ? "" : value.toString();
            }

            return stringValue;
        }
    }

    private String getLabel(String qualifier, Object value) {
        String stringValue = "";

        try {
            stringValue = getLabelService().getObjectLabel(value);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Could not get value for field '" + qualifier + "'. Using string representation instead.", e);
            }
        }

        return stringValue;
    }

    private boolean canReadProperty(Object type, String qualifier) {
        try {
            return getPermissionFacade().canReadInstanceProperty(type, qualifier);
        } catch (Exception e) {
            LOG.warn("Could not check assigned permissions", e);
            return false;
        }
    }

    private String wrapHeaderForCSV(String header) {
        return String.format("\"%s\"", header);
    }

    @Override
    public boolean needsConfirmation(ActionContext<Map> ctx) {
        Map data = ctx.getData();
        if (data == null) {
            return false;
        } else {
            Set<Object> selectedItems = (Set<Object>) data.get(CollectionBrowserController.MODEL_SELECTED_OBJECTS);
            int confirmationThreshold = getConfirmationThreshold(ctx);
            return confirmationThreshold > 0 && selectedItems.size() > confirmationThreshold;
        }
    }

    @Override
    public String getConfirmationMessage(ActionContext<Map> ctx) {
        Set<Object> selectedItems = (Set<Object>) ctx.getData().get(CollectionBrowserController.MODEL_SELECTED_OBJECTS);
        return ctx.getLabel("export.csv.confirmation", new Object[]{selectedItems.size(), this.getConfirmationThreshold(ctx)});
    }

    @Override
    public boolean canPerform(ActionContext<Map> ctx) {
        Map data = (Map)ctx.getData();
        if (data != null) {
            Object pageable = data.get("pageable");
            Object listView = data.get("columnConfig");
            Set<Object> selectedObjects = (Set<Object>) data.get(CollectionBrowserController.MODEL_SELECTED_OBJECTS);
            if (pageable instanceof Pageable && listView instanceof ListView) {
                if (selectedObjects == null || (selectedObjects != null && !selectedObjects.isEmpty())) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getConfirmationThreshold(ActionContext<Map> ctx) {
        Object parameter = ctx.getParameter("confirmation.threshold");
        if (parameter instanceof Integer) {
            return (Integer)parameter;
        } else {
            if (parameter instanceof String) {
                try {
                    return Integer.parseInt((String)parameter);
                } catch (NumberFormatException e) {
                    LOG.warn(String.format("Invalid integer [%s]", parameter), e);
                }
            }

            return -1;
        }
    }
}
