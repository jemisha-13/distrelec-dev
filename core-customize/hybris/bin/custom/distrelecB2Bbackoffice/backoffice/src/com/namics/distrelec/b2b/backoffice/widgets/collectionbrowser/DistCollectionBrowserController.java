package com.namics.distrelec.b2b.backoffice.widgets.collectionbrowser;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListView;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.collectionbrowser.CollectionBrowserController;
import com.hybris.cockpitng.widgets.collectionbrowser.mold.impl.listview.ListViewCollectionBrowserMoldStrategy;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import com.hybris.cockpitng.widgets.collectionbrowser.mold.CollectionBrowserMoldStrategy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DistCollectionBrowserController extends CollectionBrowserController {

    private static final Logger LOG = LoggerFactory.getLogger(DistCollectionBrowserController.class);

    private static final String AVAILABLE_PAGE_SIZES = "availablePageSizes";

    private Integer currentPageSize;

    @Wire
    private Combobox numberOfItemsPerPageSelect;

    private int previousSelection = -1;

    @Override
    public void initialize(Component component) {
        super.initialize(component);

        numberOfItemsPerPageSelect.addEventListener("onSelect", this::onSelectPageSize);
        ListModelList modelList = getAvailablePageSizesList();
        numberOfItemsPerPageSelect.setModel(modelList);

        modelList.stream().forEach(pageSize -> {
            numberOfItemsPerPageSelect.appendItem((String) pageSize);
        });

        numberOfItemsPerPageSelect.setSelectedIndex(0);
        setCurrentPageSize((String)modelList.get(0));
    }

    @Override
    protected void initializePagingContainer(Component parent) {
        if (StringUtils.equals("bottom", this.getWidgetSettings().getString("pagingPosition"))) {
            Executions.createComponents("/widgets/distCollectionBrowser/distConfigurablePaging.zul", this.getPagingContainerBottom(), Collections.emptyMap());
            this.getPagingContainerBottom().setVisible(true);
            this.getBottomBar().setVisible(true);
        } else {
            Executions.createComponents("/widgets/distCollectionBrowser/distConfigurablePaging.zul", this.getPagingContainerTop(), Collections.emptyMap());
            this.getPagingContainerBottom().setVisible(false);
        }
        Selectors.wireComponents(parent, this, true);
    }

    private ListModelList getAvailablePageSizesList() {
        String availablePageSizes = getWidgetSettings().getString(AVAILABLE_PAGE_SIZES);

        if (!StringUtils.isEmpty(availablePageSizes)) {
            List<String> availablePageSizesList = Arrays.asList(StringUtils.split(availablePageSizes, ","));
            return new ListModelList(availablePageSizesList);
        }
        return new ListModelList(Collections.emptyList());
    }

    public void onSelectPageSize(Event event) {
        int selectedIndex = numberOfItemsPerPageSelect.getSelectedIndex();

        if (selectedIndex != -1) {
            previousSelection = selectedIndex;
            String selectedValue = (String) getAvailablePageSizesList().get(selectedIndex);

            setCurrentPageSize(selectedValue);

            if(getPagingDelegateController().getCurrentPageable() != null) {
                process(getPagingDelegateController().getCurrentPageable());
            }
        } else {
            numberOfItemsPerPageSelect.setSelectedIndex(previousSelection);
        }
    }

    private void setCurrentPageSize(String pageSize){
        try {
            currentPageSize = Integer.parseInt(pageSize);
        } catch (NumberFormatException e) {
            LOG.info("Cannot format number of pages selection '{}' int integer", pageSize);
        }
    }

    protected <E> void process(Pageable<E> pageable, Consumer<Pageable<E>> callback) {
        if(pageable!=null) {
            pageable.setPageSize(currentPageSize);
        }
        super.process(pageable, callback);
    }

    public Combobox getNumberOfItemsPerPageSelect() {
        return numberOfItemsPerPageSelect;
    }

    public void setNumberOfItemsPerPageSelect(Combobox numberOfItemsPerPageSelect) {
        this.numberOfItemsPerPageSelect = numberOfItemsPerPageSelect;
    }

    @Deprecated
    public void setSelectedItems(Collection<?> selectedItems) {
        super.setSelectedItems(selectedItems);
        updateModelExportColumnsAndData();
    }

    @Override
    public void deselectItems(List<Object> itemsToDeselect) {
        super.deselectItems(itemsToDeselect);
        updateModelExportColumnsAndData();
    }

    @Override
    protected void updateModelExportColumnsAndData() {
        HashMap<Object, Object> exportColumnsAndData = null;
        if (getActiveMold() instanceof ListViewCollectionBrowserMoldStrategy) {
            Pageable<?> pageable = getPagingDelegateController().getCurrentPageable();
            ListView listView = getValue("columnConfig", ListView.class);
            exportColumnsAndData = new HashMap();
            exportColumnsAndData.put("pageable", pageable);
            exportColumnsAndData.put("columnConfig", listView);

            // added additionally to pass selected objects
            Set<Object> selectedObjects = getValue(MODEL_SELECTED_OBJECTS, Set.class);
            exportColumnsAndData.put(MODEL_SELECTED_OBJECTS, selectedObjects);
        }

        setValue("exportColumnsAndData", exportColumnsAndData);
    }


    @Override
    protected void updateMoldSelector(Component component, CollectionBrowserMoldStrategy mold, Div moldSelector) {
        CollectionBrowserMoldStrategy moldClass = (CollectionBrowserMoldStrategy)component.getAttribute("moldClass");
        String activeMoldSelector = String.format("yw-coll-browser-mold-sel-btn-%s-active", moldClass.getName());
        String inactiveMoldSelector = String.format("yw-coll-browser-mold-sel-btn-%s-inactive", moldClass.getName());
        if (ObjectUtils.notEqual(moldClass, mold)) {
            super.updateMoldSelector(component, mold, moldSelector);
        } else {
            UITools.modifySClass(moldSelector, activeMoldSelector, true);
            UITools.modifySClass(moldSelector, inactiveMoldSelector, false);
            setActiveMold(mold);
            setValue("multiSelectEnabled", (Object)null);
            Pageable pageable = getValue("pageable", Pageable.class);
            if (pageable != null) {
                mold.render(getBrowserContainer(), getPagingDelegateController().getCurrentSinglePageWithTypeCode(pageable.getTypeCode()));
            }
            getSelectAndFocusDelegateController().handleMoldChange();
        }
    }
}
